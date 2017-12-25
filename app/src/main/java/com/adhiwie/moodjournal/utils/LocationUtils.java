package com.adhiwie.moodjournal.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationUtils implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private Context context;
    private GoogleApiClient mApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    public LocationUtils(Context context) {
        this.context = context;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        buildGoogleApiClient();
        createLocationRequest();
    }

    private void buildGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
    }

    public GoogleApiClient getApiClient() {
        return mApiClient;
    }

    public Location getLastLocation(Activity context) {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(context, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;
                            }
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return mLastLocation;
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(600000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
