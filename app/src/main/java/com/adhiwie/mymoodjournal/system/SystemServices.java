package com.adhiwie.mymoodjournal.system;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class SystemServices {


    public boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        //check for network state
        if (ni != null && ni.isConnected()) {
            // if not connected to wifi
            if (ni.getType() != ConnectivityManager.TYPE_WIFI)
                return true;

            //if connected to wifi
            return isOnline(context);

        }
        return false;
    }


    private boolean isOnline(Context context) {
        Toast.makeText(context, "Checking for internet connectivity.", Toast.LENGTH_SHORT).show();

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }


    @SuppressLint("NewApi")
    public boolean isAppStatsAccessEnabled(Context context) {
        if (new APILevel().isSuitableForAppUsageStats()) {
            try {
                PackageManager pm = context.getPackageManager();
                ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager aom = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int mode = aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, ai.uid, ai.packageName);
                if (mode == AppOpsManager.MODE_ALLOWED)
                    return true;
                else
                    return false;
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }
        return true;
    }


    public boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
