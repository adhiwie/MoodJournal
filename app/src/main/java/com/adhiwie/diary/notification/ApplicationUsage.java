package com.adhiwie.diary.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import com.adhiwie.diary.system.APILevel;
import com.adhiwie.diary.utils.Log;
import com.adhiwie.diary.utils.SharedPref;

public class ApplicationUsage {

    private final Log log;
    private final Context context;

    public ApplicationUsage(Context context) {
        this.log = new Log();
        this.context = context;
    }


    public boolean isAppLaunched(String package_name, long start_time) {
        log.d("Looking for: " + package_name + ", at: " + start_time);
        if (new APILevel().isSuitableForAppUsageStats()) {
            Map<String, Long> apps = getAllRecentlyUsedAppsWithAppStats();
            log.i("Using API 21: " + apps.toString());
            long time_error_millis = 10 * 1000;
            for (Map.Entry<String, Long> e : apps.entrySet()) {
                if (e.getKey().equalsIgnoreCase(package_name)) {
                    log.e("Match found- Package: " + e.getKey() + ", Time: " + e.getValue());
                    log.e("Looking for: " + package_name + ", Time: " + start_time);
                    if (e.getValue() >= (start_time - time_error_millis))
                        return true;
                }
            }
            return false;
        } else {
            Set<String> apps = getAllRecentlyUsedAppsWithAppManager();
            log.i("Using API 19 and below: " + apps.toString());
            return apps.contains(package_name);
        }
    }


    @SuppressLint("NewApi")
    private Map<String, Long> getAllRecentlyUsedAppsWithAppStats() {
        Map<String, Long> apps = new HashMap<String, Long>();
        Calendar c = Calendar.getInstance();
        long current_time = c.getTimeInMillis();

        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        ;
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                (current_time - (60 * 60 * 1000)), current_time);
        for (UsageStats stat : queryUsageStats) {
            c.setTimeInMillis(stat.getLastTimeUsed());
            apps.put(stat.getPackageName(), c.getTimeInMillis());
        }
        return apps;
    }


    private Set<String> getAllRecentlyUsedAppsWithAppManager() {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);

        ArrayList<String> current_apps = new ArrayList<String>();


//		List<RunningTaskInfo> tasks = am.getRunningTasks(3); 
//		for(RunningTaskInfo task : tasks)
//		{
//			current_apps.add(task.topActivity.getPackageName());
//		}	

        List<ActivityManager.RunningAppProcessInfo> pi_list = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo pi : pi_list) {
            if (pi.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                current_apps.addAll(Arrays.asList(pi.pkgList));
            }
        }


        Set<String> recently_used_apps = new HashSet<String>();
        ArrayList<String> last_apps = getLastUsedApps();
        setLastUsedApps(current_apps);

        if (last_apps == null) {
            for (String current_app : current_apps) {
                recently_used_apps.add(current_app);
            }
            return recently_used_apps;
        }

        //if new apps stack has extra elements
        if (last_apps.size() < current_apps.size()) {
            for (int i = 0; i < current_apps.size(); i++) {
                if (!last_apps.contains(current_apps.get(i))) {
                    recently_used_apps.add(current_apps.get(i));
                }
            }
            return recently_used_apps;
        }


        int last_apps_counter = last_apps.size() - 1;
        for (int i = current_apps.size() - 1; i >= 0; i--) {
            String current_app = current_apps.get(i);
            String last_app = last_apps.get(last_apps_counter--);
            if (current_app.equalsIgnoreCase(last_app)) {
                continue;
            } else {
                if (last_apps.contains(current_app)) {
                    int j;
                    for (j = last_apps.indexOf(current_app) - 1; j >= 0; j--) {
                        i = i - 1;
                        if (i < 0) {
                            break;
                        }
                        String app1 = current_apps.get(i);
                        String app2 = last_apps.get(j);
                        if (!app1.equalsIgnoreCase(app2)) {
                            break;
                        }
                    }
                    if (j < 0) {
                        i = i - 1;
                    }
                    for (j = i; j >= 0; j--) {
                        recently_used_apps.add(current_apps.get(j));
                    }
                    return recently_used_apps;
                } else {
                    for (int j = i; j >= 0; j--) {
                        recently_used_apps.add(current_apps.get(j));
                    }
                    return recently_used_apps;
                }
            }
        }
        return recently_used_apps;
    }

    String[] getActivePackages() {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        final Set<String> activePackages = new HashSet<String>();
        final List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                activePackages.addAll(Arrays.asList(processInfo.pkgList));
            }
        }
        return activePackages.toArray(new String[activePackages.size()]);
    }

    private ArrayList<String> getLastUsedApps() {
        ArrayList<String> last_apps = new ArrayList<String>();
        SharedPref sp = new SharedPref(context);
        String last_apps_string = sp.getString("LAST_USED_APPS_STRING");

        if (last_apps_string == null)
            return null;

        try {
            JSONArray ja = new JSONArray(last_apps_string);
            for (int i = 0; i < ja.length(); i++) {
                last_apps.add(ja.getString(i));
            }
            return last_apps;
        } catch (JSONException e) {
            log.e(e.toString());
            return null;
        }
    }

    private void setLastUsedApps(ArrayList<String> last_apps) {
        SharedPref sp = new SharedPref(context);
        JSONArray ja = new JSONArray();
        for (String app : last_apps) {
            ja.put(app);
        }
        sp.add("LAST_USED_APPS_STRING", ja.toString());
    }

}
