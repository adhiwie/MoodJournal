package com.adhiwie.moodjournal.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.MoodQuestionActivity;
import com.adhiwie.moodjournal.R;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String timeInString = intent.getStringExtra("timeInString");
        String action = "It is "+timeInString+", time to record your mood.";

        long group = intent.getLongExtra("group", 0);


        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);
        Location designatedLocation = new Location("");
        designatedLocation.setLatitude(latitude);
        designatedLocation.setLongitude(longitude);

        double currentLatitude = intent.getDoubleExtra("currentLatitude", 0);
        double currentLongitude = intent.getDoubleExtra("currentLongitude", 0);
        Location currentLocation = new Location("");
        currentLocation.setLatitude(currentLatitude);
        currentLocation.setLongitude(currentLongitude);

        boolean isLocation = false;

        if (currentLatitude == 0 && currentLongitude == 0) {
            isLocation = true;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.small_icon)
                        .setContentTitle("Mood Journal")
                        .setContentText(action)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);

        Intent resultIntent = new Intent(context, MoodQuestionActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (group == 1) {
            mNotificationManager.notify(123, mBuilder.build());
        } else if (group == 2 && isLocation) {
            mNotificationManager.notify(123, mBuilder.build());
        }

    }

}