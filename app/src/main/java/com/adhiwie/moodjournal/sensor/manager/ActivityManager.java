package com.adhiwie.moodjournal.sensor.manager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.sensor.data.ActivityData;
import com.adhiwie.moodjournal.sensor.push.ActivitySensor;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;

public class ActivityManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Intent intent;
    private GoogleApiClient googleApiClient;

    public enum REQUEST_TYPE {START, STOP, RESET}

    private REQUEST_TYPE requestType;
    private boolean inProgress;
    int frequencyUpdates = 1 * 60 * 1000;
    final Context context;
    private Log log = new Log();

    public ActivityManager(Context context) {
        this.context = context;
        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        intent = new Intent(context, ActivitySensor.class);
        inProgress = false;

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult cr) {
        log.e("Connection failed -- " + cr.getErrorCode());
        inProgress = false;
    }

    @Override
    public void onConnectionSuspended(int cs) {
        log.e("Connection suspended -- " + cs);
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        log.v("onConnected with request type: " + requestType + ", frequency: " + frequencyUpdates);
        PendingIntent pendingIntent = PendingIntent.getService(context, 21, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        switch (requestType) {
            case START:
                ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, frequencyUpdates, pendingIntent);
                break;

            case STOP:
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient, pendingIntent);
                break;

            case RESET:
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient, pendingIntent);
                ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, frequencyUpdates, pendingIntent);
                break;

            default:
                log.e("Unknown request type in onConnected().");
                break;
        }

        inProgress = false;
        googleApiClient.disconnect();
    }


    /**
     * @param frequency (mins)
     */
    public void requestActivityUpdates(int frequency) {
        frequencyUpdates = frequency * 60 * 1000;
        log.d("Request Activity Updates: " + frequencyUpdates / 1000 + "s");
        log.d("Request Activity Updates");
        if (inProgress) {
            log.e("A request is already underway");
            return;
        }
        requestType = REQUEST_TYPE.START;
        inProgress = true;
        googleApiClient.connect();
        log.e("Request activity updates in progress");
    }

    public void stopActivityUpdates() {
        log.d("stopActivityUpdates");
        if (inProgress) {
            log.e("A request is already underway");
            return;
        }
        requestType = REQUEST_TYPE.STOP;
        log.d("Request activity updates in progress");
        inProgress = true;
        googleApiClient.connect();
    }

    /**
     * @param frequency (mins)
     */
    public void resetActivityUpdates(int frequency) {
        frequencyUpdates = frequency * 60 * 1000;
        log.d("Reset Activity Updates: " + frequencyUpdates / 1000 + "s");
        if (inProgress) {
            log.e("A request is already underway");
            return;
        }
        requestType = REQUEST_TYPE.RESET;
        inProgress = true;
        googleApiClient.connect();
        log.d("Request activity updates in progress");
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
