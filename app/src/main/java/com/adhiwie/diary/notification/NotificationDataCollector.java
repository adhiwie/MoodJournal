package com.adhiwie.diary.notification;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.PowerManager;
import android.service.notification.StatusBarNotification;

import com.adhiwie.diary.file.FileMgr;
import com.adhiwie.diary.sensor.data.NotificationData;
import com.adhiwie.diary.system.APILevel;
import com.adhiwie.diary.utils.Log;
import com.adhiwie.diary.utils.SharedPref;

public class NotificationDataCollector {

    private final Log log;
    private final Context context;
    private final SharedPref sp;

    public NotificationDataCollector(Context context) {
        this.log = new Log();
        this.context = context;
        this.sp = new SharedPref(context);
    }


    @SuppressLint("NewApi")
    protected void onNotificationPosted(StatusBarNotification sbn) {
        try {
            NotificationType nt = new NotificationType(sbn);
            if (nt.isCallNotification() || nt.isOngoingNotification()
                    || nt.isCollectionNotification()) {
                return;
            }


            Notification n = sbn.getNotification();
            int n_id = sbn.getId();
            String tag = sbn.getTag();
            if (tag == null)
                tag = "unknown";
            String key = "unknown";
            if (new APILevel().getDeviceAPILevel() >= 20)
                key = sbn.getKey();
            int priority = n.priority;
            long arrivalTime = sbn.getPostTime();
            long removalTime = 0;
            int clicked = 0; //temporary
            boolean led = hasLED(n);
            boolean vibrate = hasVibration(n);
            boolean sound = hasSound(n);
            boolean unique_sound = hasUniqueSound(n);
            String app_name = getAppNameFromPackage(context, sbn.getPackageName());
            String app_package = sbn.getPackageName();
            String title = "";
            if (n.extras.getCharSequence(Notification.EXTRA_TITLE) != null)
                title = n.extras.getCharSequence(Notification.EXTRA_TITLE).toString();
            if (title.equalsIgnoreCase(app_name) && n.extras.getCharSequence(Notification.EXTRA_TEXT) != null)
                title = n.extras.getCharSequence(Notification.EXTRA_TEXT).toString();
            title = title.replace("\n", " ").trim();


            NotificationData n_data = new NotificationData(n_id, tag, key, priority,
                    title, arrivalTime, removalTime,
                    clicked, led, vibrate, sound, unique_sound, app_name, app_package);

            new Log().i("Saving notification in SharedPref: " + n_data.toJSONString());
            saveNotificationData(n_data);


        } catch (Exception e) {
            log.e("Error: unable to save notification data in shared pref!! " + e.toString());
        }

    }


    @SuppressLint("NewApi")
    protected void onNotificationRemoved(StatusBarNotification sbn) {

        NotificationType nt = new NotificationType(sbn);
        if (nt.isCallNotification() || nt.isOngoingNotification()
                || nt.isCollectionNotification()) {
            return;
        }

        final Notification n = sbn.getNotification();

        // check for screen lock
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        @SuppressWarnings("deprecation")
        boolean isSceenAwake = (Build.VERSION.SDK_INT < 20 ? powerManager.isScreenOn() : powerManager.isInteractive());


        new Log().e("Screen awake: " + isSceenAwake);

        String app_name = getAppNameFromPackage(context, sbn.getPackageName());

        String title = "";
        if (n.extras.getCharSequence(Notification.EXTRA_TITLE) != null)
            title = n.extras.getCharSequence(Notification.EXTRA_TITLE).toString();
        if (title.equalsIgnoreCase(app_name) && n.extras.getCharSequence(Notification.EXTRA_TEXT) != null)
            title = n.extras.getCharSequence(Notification.EXTRA_TEXT).toString().trim();

        NotificationData n_data = null;

        try {
            n_data = getNotificationData(app_name, title);
        } catch (Exception e) {
            log.e("Error: unable to get notification data from shared pref!! " + e.toString());
        }

        if (n_data != null) {
            // remove notification from shared pref
            removeNotificationData(app_name, title);

            // set removal time
            n_data.setRemovalTime(Calendar.getInstance().getTimeInMillis());

            if (isSceenAwake == false) // device is locked
            {
                n_data.setClicked(-1);
                logNotificationData(context, n_data);
            } else {
                final NotificationResponseDetector async_task = new NotificationResponseDetector(context, n_data) {
                    @Override
                    protected void onPostExecute(NotificationData n_data) {
                        logNotificationData(context, n_data);
                    }
                };
                int corePoolSize = 60;
                int maximumPoolSize = 80;
                int keepAliveTime = 10;
                BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
                Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                        keepAliveTime, TimeUnit.SECONDS, workQueue);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    async_task.executeOnExecutor(threadPoolExecutor);
                else
                    async_task.execute();
            }
        } else {
            log.d("Notification ignored because it was not saved in the SharedPref when posted!");
            log.d("Title: " + title);
        }

    }


    private void logNotificationData(Context context, NotificationData n_data) {
        try {
            // store data in file for server transmission
            FileMgr fm = new FileMgr(context);
            fm.addData(n_data);
            log.e("Logging notification data in database/file: " + n_data.toJSONString());
        } catch (JSONException e) {
            log.e(e.toString());
        }
    }


    private void saveNotificationData(NotificationData n_data) throws JSONException {
        String app_name = n_data.getAppName();
        String title = n_data.getTitle();
        sp.add(createKey(app_name, title), n_data.toJSONString());
    }

    private NotificationData getNotificationData(String app_name, String title) throws JSONException {
        String n_string = sp.getString(createKey(app_name, title));
        if (n_string == null)
            return null;
        return NotificationData.jsonStringToDataObject(n_string);
    }

    private void removeNotificationData(String app_name, String title) {
        sp.remove(createKey(app_name, title));
    }

    private String createKey(String app_name, String title) {
        app_name = app_name.replace(" ", "_");
        title = title.replace(" ", "_");
        int end_index = title.length() > 15 ? 15 : title.length();
        title = title.substring(0, end_index);
        return app_name + title;
    }


    private boolean hasLED(Notification n) {
        if (n.defaults == Notification.DEFAULT_ALL || n.defaults == Notification.DEFAULT_LIGHTS
                || n.defaults == (Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                || n.defaults == (Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                || n.ledOnMS > 0)
            return true;
        return false;
    }

    private boolean hasVibration(Notification n) {
        long[] v_pattern = n.vibrate;
        if (n.defaults == Notification.DEFAULT_ALL || n.defaults == Notification.DEFAULT_VIBRATE
                || n.defaults == (Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                || n.defaults == (Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                || v_pattern != null)
            return true;
        return false;
    }

    private boolean hasSound(Notification n) {
        if (n.defaults == Notification.DEFAULT_ALL || n.defaults == Notification.DEFAULT_SOUND
                || n.defaults == (Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                || n.defaults == (Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                || n.sound != null)
            return true;
        return false;
    }

    private boolean hasUniqueSound(Notification n) {
        return n.sound != null;
    }


    private String getAppNameFromPackage(Context context, String package_name) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(package_name, 0);
            if (applicationInfo == null) {
                return "UNKNOWN";
            } else {
                String app_name = packageManager.getApplicationLabel(applicationInfo).toString();
                if (app_name.toLowerCase(Locale.getDefault()).contains("google search"))
                    app_name = "Google Services";
                return app_name;
            }
        } catch (NameNotFoundException e) {
            return "UNKNOWN";
        }
    }

}
