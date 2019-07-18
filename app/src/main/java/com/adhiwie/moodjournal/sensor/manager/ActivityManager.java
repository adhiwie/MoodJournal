package com.adhiwie.moodjournal.sensor.manager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.sensor.data.ActivityData;
import com.adhiwie.moodjournal.sensor.push.ActivitySensor;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.google.android.gms.location.ActivityRecognitionClient;

public class ActivityManager {

    private Intent intent;
    private PendingIntent pendingIntent;
    private ActivityRecognitionClient activityRecognitionClient;

    private int frequencyUpdates = 1 * 60 * 1000;
    private final Context context;
    private Log log = new Log();

    public ActivityManager(Context context) {
        this.context = context;
        intent = new Intent(context, ActivitySensor.class);

        pendingIntent = PendingIntent.getService(context, 21, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        activityRecognitionClient = new ActivityRecognitionClient(context);

        activityRecognitionClient.requestActivityUpdates(frequencyUpdates, pendingIntent);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));
        }
    }


    /**
     * @param frequency (mins)
     */
    public void requestActivityUpdates(int frequency) {
        frequencyUpdates = frequency * 60 * 1000;
        log.d("Request Activity Updates: " + frequencyUpdates / 1000 + "s");
        log.d("Request Activity Updates");
        activityRecognitionClient.requestActivityUpdates(frequencyUpdates, pendingIntent);
    }

    public void stopActivityUpdates() {
        log.d("stopActivityUpdates");
        activityRecognitionClient.removeActivityUpdates(pendingIntent);
    }

    /**
     * @param frequency (mins)
     */
    public void resetActivityUpdates(int frequency) {
        frequencyUpdates = frequency * 60 * 1000;
        log.d("Reset Activity Updates: " + frequencyUpdates / 1000 + "s");
        activityRecognitionClient.removeActivityUpdates(pendingIntent);
        activityRecognitionClient.requestActivityUpdates(frequencyUpdates, pendingIntent);
    }


    private final String Current_Activity_Type = "Current_Activity_Type";
    private final String Current_Activity_Confidence = "Current_Activity_Confidence";
    private final String Current_Activity_Time = "Current_Activity_Time";

    public ActivityData getCurrentActivity() {
        try {
            SharedPref sp = new SharedPref(context);
            String type = sp.getString(Current_Activity_Type);
            int confidence = sp.getInt(Current_Activity_Confidence);
            long time = sp.getLong(Current_Activity_Time);
            return new ActivityData(type, confidence, time);
        } catch (NullPointerException e) {
            new Log().e(e.toString());
            return null;
        }
    }

    public void setCurrentActivity(ActivityData ad) {
        try {
            SharedPref sp = new SharedPref(context);
            sp.add(Current_Activity_Type, ad.getActivity());
            sp.add(Current_Activity_Confidence, ad.getConfidence());
            sp.add(Current_Activity_Time, ad.getTime());
        } catch (Exception e) {
            new Log().e(e.toString());
        }
    }

}