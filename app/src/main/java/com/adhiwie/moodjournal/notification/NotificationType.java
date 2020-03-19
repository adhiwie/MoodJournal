package com.adhiwie.moodjournal.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.service.notification.StatusBarNotification;

import com.adhiwie.moodjournal.utils.Log;

@SuppressLint("NewApi")
public class NotificationType {
    private final Log log = new Log();
    private final StatusBarNotification sbn;
    private final Notification n;
    private final String package_name;

    protected NotificationType(StatusBarNotification sbn) {
        this.sbn = sbn;
        this.n = sbn.getNotification();
        this.package_name = sbn.getPackageName();
    }

    public boolean isSilentNotification() {
        if (n.defaults == Notification.DEFAULT_ALL || getVibrate(n) || getSound(n)) {
            return false;
        }
        return true;
    }

    private boolean getVibrate(Notification n) {
        long[] v_pattern = n.vibrate;
        if (n.defaults == Notification.DEFAULT_ALL || n.defaults == Notification.DEFAULT_VIBRATE
                || n.defaults == (Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                || n.defaults == (Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                || v_pattern != null)
            return true;
        return false;
    }

    private boolean getSound(Notification n) {
        if (n.defaults == Notification.DEFAULT_ALL || n.defaults == Notification.DEFAULT_SOUND
                || n.defaults == (Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                || n.defaults == (Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                || n.sound != null)
            return true;
        return false;
    }


    public boolean isLowPriorityNotification() {
        if (n.priority < Notification.PRIORITY_DEFAULT) {
            log.i("Notification should be ignored because notification is of low priority!");
            return true;
        }
        return false;
    }

    public boolean isCallNotification() {
        if (package_name.contains("com.android.server.telecom")
                || package_name.contains("com.google.android.dialer")
                || package_name.contains("com.android.phone")) {
            log.i("Phone ringing!! (Notification should be ignored)");
            return true;
        }
        return false;
    }

    public boolean isOngoingNotification() {
        return !sbn.isClearable() || sbn.isOngoing();
//		int flag = n.flags;
//		if(flag == Notification.FLAG_ONGOING_EVENT || flag == Notification.FLAG_FOREGROUND_SERVICE || flag == Notification.FLAG_NO_CLEAR)
//		{
//			log.i("Notification should be ignored because notification is on going!");
//			return true;
//		}
//		return false;
    }

    public boolean isSelfNotification() {
        if (package_name.contains(this.getClass().getPackage().getName())) {
            log.i("Notification should be ignored because notification is trigger by our own app!");
            return true;
        }
        return false;
    }


    public boolean isCollectionNotification() {
        String title = "";
        if (n.extras.getCharSequence(Notification.EXTRA_TITLE) != null)
            title = n.extras.getCharSequence(Notification.EXTRA_TITLE).toString();

        String headerText = "";
        if (n.extras.getCharSequence(Notification.EXTRA_TEXT) != null)
            headerText = n.extras.getCharSequence(Notification.EXTRA_TEXT).toString();

        if (title.contains(" new messages") || title.equals("") || headerText.equals("") || headerText.contains(" new messages")) {
            log.i("Notification should be ignored because notification is a collection notification!");
            return true;
        }
        return false;
    }

}
