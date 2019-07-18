package com.adhiwie.diary.sensor.manager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.adhiwie.diary.debug.CustomExceptionHandler;
import com.adhiwie.diary.sensor.data.LocationData;
import com.adhiwie.diary.sensor.push.LocationSensor;
import com.adhiwie.diary.utils.Log;
import com.adhiwie.diary.utils.SharedPref;


public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final Context context;
    private GoogleApiClient googleApiClient;
    private boolean inProgress;
    private final Log log = new Log();

    public enum REQUEST_TYPE {START, STOP, LAST_KNOWN}

    private REQUEST_TYPE requestType;
    private Intent intent;
    LocationRequest mLocationRequest;


    public LocationManager(Context context) {
        this.context = context;
        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        intent = new Intent(context, LocationSensor.class);
        inProgress = false;

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));
        }

    }


    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        log.e("Connection failed !!!!!!");
        inProgress = false;
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        log.e("Connection suspended !!!!!!");
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        PendingIntent pendingIntent = PendingIntent.getService(context, 11, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        switch (requestType) {
            case START:
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, pendingIntent);
                break;

            case STOP:
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, pendingIntent);
                break;

            default:
                log.e("Unknown request type in onConnected().");
                break;
        }

        inProgress = false;
        googleApiClient.disconnect();

    }


    /**
     * @param frequency (minutes) minimum time interval between location updates
     */
    public void requestLocationUpdates(double frequency) {
        log.i("New location request");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval((long) (frequency * 60 * 1000));
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(100);
        mLocationRequest.setFastestInterval((long) (frequency / 5 * 60 * 1000));
        if (inProgress) {
            log.e("A request is already underway");
            return;
        }
        inProgress = true;
        requestType = REQUEST_TYPE.START;
        googleApiClient.connect();
    }

    public void removeContinuousUpdates() {
        log.i("Removing location service");
        if (inProgress) {
            log.e("A request is already underway");
            return;
        }
        inProgress = true;
        requestType = REQUEST_TYPE.STOP;
        googleApiClient.connect();
    }


    private final String Current_Loc_Lat = "Current_Loc_Lat";
    private final String Current_Loc_Lon = "Current_Loc_Lon";
    private final String Current_Loc_Time = "Current_Loc_Time";
    private final String Current_Loc_Provider = "Current_Loc_Provider";
    private final String Current_Loc_Accuracy = "Current_Loc_Accuracy";

    public LocationData getCurrentLocation() {
        try {
            SharedPref sp = new SharedPref(context);
            double lat = Double.parseDouble(sp.getString(Current_Loc_Lat));
            double lon = Double.parseDouble(sp.getString(Current_Loc_Lon));
            long time = sp.getLong(Current_Loc_Time);
            String provider = sp.getString(Current_Loc_Provider);
            float accuracy = sp.getFloat(Current_Loc_Accuracy);
            return new LocationData(lat, lon, time, provider, accuracy);
        } catch (NullPointerException e) {
            new Log().e(e.toString());
            return null;
        }
    }

    public void setCurrentLocation(LocationData ld) {
        try {
            SharedPref sp = new SharedPref(context);
            sp.add(Current_Loc_Lat, String.valueOf(ld.getLat()));
            sp.add(Current_Loc_Lon, String.valueOf(ld.getLon()));
            sp.add(Current_Loc_Time, ld.getTime());
            sp.add(Current_Loc_Provider, ld.getProvider());
            sp.add(Current_Loc_Accuracy, ld.getAccuracy());
        } catch (Exception e) {
            new Log().e(e.toString());
        }
    }

}
