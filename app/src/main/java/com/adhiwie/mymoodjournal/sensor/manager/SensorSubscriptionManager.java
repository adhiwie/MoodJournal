package com.adhiwie.mymoodjournal.sensor.manager;

import java.util.Calendar;

import android.content.Context;

import com.adhiwie.mymoodjournal.debug.CustomExceptionHandler;
import com.adhiwie.mymoodjournal.sensor.data.LocationData;
import com.adhiwie.mymoodjournal.utils.Log;
import com.adhiwie.mymoodjournal.utils.SharedPref;

public class SensorSubscriptionManager {

    private final Context context;
    private final SharedPref sp;
    private final int ACTIVITY_FREQUENCY_FAST_MINS = 1;
    private final long ACTIVITY_DELAY_THRESHOLD_MILLIS = 5 * 60 * 1000;

    public SensorSubscriptionManager(Context context) {
        this.context = context;
        this.sp = new SharedPref(context);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));
        }
    }

    public void startActivitySensingIfNotWorking() {
        try {
            ActivityManager am = new ActivityManager(context);
            long last_activity_sensing_time = am.getCurrentActivity().getTime();
            long current_time = Calendar.getInstance().getTimeInMillis();
            int current_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            long difference_time = current_time - last_activity_sensing_time;
            new Log().i("Time since lst activity: " + difference_time);
            if (difference_time > ACTIVITY_DELAY_THRESHOLD_MILLIS && (current_hour < 2 || current_hour > 4)) // no sensing from 2-5
            {
                new Log().i("Starting activity sensing");
                ActivityManager a_mgr = new ActivityManager(context);
                a_mgr.resetActivityUpdates(ACTIVITY_FREQUENCY_FAST_MINS);
            } else {
                new Log().i("Adaptive sensing is already in progress");
            }
        } catch (Exception e) {

        }
    }


    public void startLocationSensingIfWorking() {
        try {
            LocationManager lm = new LocationManager(context);
            LocationData ld = lm.getCurrentLocation();
            long current_time = Calendar.getInstance().getTimeInMillis();

            if (((current_time - getLastLocationStartTime()) > (30 * 1000) || getLastLocationStartTime() == 0)
                    &&
                    (ld == null || (current_time - ld.getTime() > (30 * 60 * 1000)))
                    ) {
                lm.requestLocationUpdates(AdaptiveSensingManager.LOCATION_FREQUENCY_SLOW);
                setLastLocationTime(current_time);
            }
        } catch (Exception e) {
            new Log().e(e.toString());
        }
    }


    private final String SSM_LAST_LOCATION_START_TIME = "SSM_LAST_LOCATION_START_TIME";

    private long getLastLocationStartTime() {
        return sp.getLong(SSM_LAST_LOCATION_START_TIME);
    }

    private void setLastLocationTime(long time) {
        sp.add(SSM_LAST_LOCATION_START_TIME, time);
    }

}
