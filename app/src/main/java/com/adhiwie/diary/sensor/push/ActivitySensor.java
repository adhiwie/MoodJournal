package com.adhiwie.diary.sensor.push;

import android.app.IntentService;
import android.content.Intent;

import com.adhiwie.diary.LinkedTasks;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.adhiwie.diary.debug.CustomExceptionHandler;
import com.adhiwie.diary.file.FileMgr;
import com.adhiwie.diary.sensor.data.ActivityData;
import com.adhiwie.diary.sensor.manager.ActivityManager;
import com.adhiwie.diary.sensor.manager.AdaptiveSensingManager;
import com.adhiwie.diary.utils.Log;


public class ActivitySensor extends IntentService {

    public ActivitySensor() {
        super("ActivityRecognitionService");
    }


    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log log = new Log();
        try {
            log.v("New Activity");

            if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler))
                Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));

            if (ActivityRecognitionResult.hasResult(intent)) {
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                DetectedActivity detected_activity = result.getMostProbableActivity();
                String activity = new ActivityType().getNameFromType(detected_activity.getType());
                int confidence = detected_activity.getConfidence();
                long time = result.getTime();
                ActivityData ad = new ActivityData(activity, confidence, time);

                ActivityManager am = new ActivityManager(getApplicationContext());
                am.setCurrentActivity(ad);

                FileMgr fm = new FileMgr(getApplicationContext());
                fm.addData(ad);

                AdaptiveSensingManager as = new AdaptiveSensingManager(getApplicationContext());
                as.onNewActivity(ad);

                if (activity.equals("STILL")) {
                    new LinkedTasks(getApplicationContext()).checkQuestionnaires();
                }

                log.d("Activity Result: " + ad.toJSONString());
            }
        } catch (Exception e) {
            log.e(e.toString());
        }
    }


}
