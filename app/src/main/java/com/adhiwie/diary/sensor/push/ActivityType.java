package com.adhiwie.diary.sensor.push;

import java.util.ArrayList;

import com.google.android.gms.location.DetectedActivity;

public class ActivityType {

    public final String ACTIVITY_IN_VEHICLE = "in_vehicle";
    public final String ACTIVITY_ON_BICYCLE = "on_bicycle";
    public final String ACTIVITY_RUNNING = "running";
    public final String ACTIVITY_WALKING = "walking";
    public final String ACTIVITY_ON_FOOT = "on_foot";
    public final String ACTIVITY_STILL = "still";
    public final String ACTIVITY_UNKNOWN = "unknown";
    public final String ACTIVITY_TILTING = "tilting";

    /**
     * Map detected activity types to strings
     *
     * @param activityType The detected activity type
     * @return A user-readable name for the type
     */
    public String getNameFromType(int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                return ACTIVITY_IN_VEHICLE;
            case DetectedActivity.ON_BICYCLE:
                return ACTIVITY_ON_BICYCLE;
            case DetectedActivity.ON_FOOT:
                return ACTIVITY_ON_FOOT;
            case DetectedActivity.STILL:
                return ACTIVITY_STILL;
            case DetectedActivity.UNKNOWN:
                return ACTIVITY_UNKNOWN;
            case DetectedActivity.TILTING:
                return ACTIVITY_TILTING;
            case DetectedActivity.WALKING:
                return ACTIVITY_WALKING;
            case DetectedActivity.RUNNING:
                return ACTIVITY_RUNNING;
        }
        return ACTIVITY_UNKNOWN;

    }

    public ArrayList<String> getAllActivityValues() {
        ArrayList<String> activities = new ArrayList<String>();
        activities.add(ACTIVITY_IN_VEHICLE);
        activities.add(ACTIVITY_ON_BICYCLE);
        activities.add(ACTIVITY_ON_FOOT);
        activities.add(ACTIVITY_STILL);
        activities.add(ACTIVITY_UNKNOWN);
        activities.add(ACTIVITY_TILTING);
        activities.add(ACTIVITY_WALKING);
        activities.add(ACTIVITY_RUNNING);
        return activities;
    }


    public boolean isDynamic(String activity) {
        return !activity.equals(ACTIVITY_STILL);
    }

}
