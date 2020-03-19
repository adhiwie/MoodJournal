package com.adhiwie.moodjournal.sensor.push;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.adhiwie.moodjournal.LinkedTasks;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.file.FileMgr;
import com.adhiwie.moodjournal.sensor.data.LocationData;
import com.adhiwie.moodjournal.sensor.manager.LocationManager;
import com.adhiwie.moodjournal.utils.Log;


public class LocationSensor extends IntentService {

    public LocationSensor() {
        super("NOTIFICATION_SERVICE");
    }


    /**
     * Called when a new location update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Log log = new Log();
        try {
            log.v("New Location");
            if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler))
                Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));

            Bundle b = intent.getExtras();
            String key = "com.google.android.location.LOCATION";
            Location loc = (Location) b.get(key);
            LocationData ld = new LocationData(
                    loc.getLatitude(),
                    loc.getLongitude(),
                    loc.getTime(),
                    loc.getProvider(),
                    loc.getAccuracy());

            LocationManager lm = new LocationManager(getApplicationContext());
            lm.setCurrentLocation(ld);

            FileMgr fm = new FileMgr(getApplicationContext());
            fm.addData(ld);
            log.d("Location Result: " + ld.toJSONString());

            // check questionnaires
            new LinkedTasks(getApplicationContext()).checkQuestionnaires();

        } catch (Exception e) {
            log.e(e.toString());
        }
    }


}
