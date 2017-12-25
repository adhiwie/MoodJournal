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
import com.adhiwie.moodjournal.model.MoodModel;
import com.adhiwie.moodjournal.model.NotificationModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Map;

public class ReminderReceiver extends BroadcastReceiver {

    private final String CHANNEL_ID = "reminder";
    private final int NOTIFICATION_ID = 123;

    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public void onReceive(Context context, Intent intent) {
        String timeInString = intent.getStringExtra("timeInString");
        String action = "It is "+timeInString+", time to record your mood.";

        String uid = intent.getStringExtra("uid");
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

        Calendar cal = Calendar.getInstance();
        long timestamp = cal.getTimeInMillis();

        boolean isLocation = false;

        if (currentLatitude == 0 && currentLongitude == 0) {
            isLocation = true;
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
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

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        NotificationModel notificationModel = new NotificationModel(uid, timestamp);
        Map<String, Object> notificationValue = notificationModel.toMap();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("notifications").push().setValue(notificationValue);
    }
}