package com.adhiwie.diary.user.permission;

import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.adhiwie.diary.notification.NotificationMonitorService;
import com.adhiwie.diary.sensor.push.AccessibilitySensor;
import com.adhiwie.diary.system.APILevel;
import com.adhiwie.diary.system.SystemServices;
import com.adhiwie.diary.utils.NotificationMgr;

@SuppressLint("InlinedApi")
public class Permission {

    private final Context context;

    public Permission(Context context) {
        this.context = context;
    }


    // permission checks
    public boolean isNSLPermitted() {
        if (!new APILevel().isSuitableForNLS())
            return true;

        return new SystemServices().isMyServiceRunning(context, NotificationMonitorService.class);
    }


    @SuppressLint("NewApi")
    public boolean isAppAccessPermitted() {
        if (!new APILevel().isSuitableForAppUsageStats())
            return true;

        if (isAppAccessPermittedNew())
            return true;

        long current_time = Calendar.getInstance().getTimeInMillis();
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        ;

        final List<UsageStats> u_list = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                (current_time - (24 * 60 * 60 * 1000)), current_time);

        for (UsageStats stat : u_list)
            if (stat.getPackageName().equalsIgnoreCase("com.adhiwie.diary") == false)
                return true;
        return false;
    }

    private boolean isAppAccessPermittedNew() {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


//	public boolean isAccessibilityPermitted() 
//	{
//		AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
//		String id = context.getPackageName() + "/.sensor.push.AccessibilitySensor";
//		System.out.println(id);
//		List<AccessibilityServiceInfo> runningServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
//		for (AccessibilityServiceInfo service : runningServices) 
//			if (id.equals(service.getId())) 
//				return true;
//		return false;
//	}

    public boolean isAccessibilityPermitted() {
        SystemServices ss = new SystemServices();
        return ss.isMyServiceRunning(context, AccessibilitySensor.class);
    }

    // activity starters
    public void startNSLPermissionActivityIfRequired() {
        if (!new APILevel().isSuitableForNLS() || isNSLPermitted())
            return;
        Intent i = new Intent(context, NSLPermissionActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public void startAppUsagePermissionActivityIfRequired() {
        if (!new APILevel().isSuitableForAppUsageStats() || isAppAccessPermitted())
            return;
        Intent i = new Intent(context, AppUsagePermissionActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public void startAccessibilityServicePermissionActivityIfRequired() {
        if (isAccessibilityPermitted())
            return;
        Intent i = new Intent(context, AccessibilityServicePermissionActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }


    // notifications
    public void notifyUserIfNSLPermissionRevoked() {
        if (!new APILevel().isSuitableForNLS() || isNSLPermitted())
            return;

        Intent i = new Intent(context, NSLPermissionActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this.context, 201, i, PendingIntent.FLAG_UPDATE_CURRENT);
        new NotificationMgr().triggerPriorityNotification(context, pi, 2011, "App stopped", "Permission required!");
    }


    public void notifyUserIfAppUsagePermissionRevoked() {
        if (!new APILevel().isSuitableForAppUsageStats() || isAppAccessPermitted())
            return;

        Intent i = new Intent(context, AppUsagePermissionActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this.context, 202, i, PendingIntent.FLAG_UPDATE_CURRENT);
        new NotificationMgr().triggerPriorityNotification(context, pi, 2021, "App stopped", "Permission required!");
    }


    public void notifyUserIfAccessibilityPermissionRevoked() {
        if (isAccessibilityPermitted())
            return;

        Intent i = new Intent(context, AccessibilityServicePermissionActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this.context, 203, i, PendingIntent.FLAG_UPDATE_CURRENT);
        new NotificationMgr().triggerPriorityNotification(context, pi, 2031, "App stopped", "Permission required!");
    }


}
