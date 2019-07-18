package com.adhiwie.diary.notification;

import android.annotation.SuppressLint;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.adhiwie.diary.LinkedTasks;
import com.adhiwie.diary.debug.CustomExceptionHandler;
import com.adhiwie.diary.utils.Log;

@SuppressLint("NewApi")
public class NotificationMonitorService extends NotificationListenerService {

    private final Log log = new Log();


    @SuppressLint("NewApi")
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        log.v("A new notification has been posted!");

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }


        final StatusBarNotification sbn_final = sbn;
        Thread thread = new Thread() {
            @Override
            public void run() {
                //log data
                new NotificationDataCollector(getApplicationContext()).onNotificationPosted(sbn_final);
            }
        };
        thread.start();

        // check all linked tasks
        try {
            new LinkedTasks(getApplicationContext()).checkAllExceptPermission();
        } catch (Exception e) {
        }
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        log.v("A notification has been removed!");

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }

        final StatusBarNotification sbn_final = sbn;
        Thread thread = new Thread() {
            @Override
            public void run() {
                //log data
                new NotificationDataCollector(getApplicationContext()).onNotificationRemoved(sbn_final);
            }
        };
        thread.start();


    }


}
