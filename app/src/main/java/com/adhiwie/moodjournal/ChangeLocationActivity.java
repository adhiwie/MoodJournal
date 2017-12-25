package com.adhiwie.moodjournal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adhiwie.moodjournal.service.FetchAddressIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class ChangeLocationActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener {

    private static final String TAG = ChangeLocationActivity.class.getSimpleName();

    /** Initialization **/
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private double latitude, longitude;
    private AddressResultReceiver mResultReceiver;
    private String address;
    private GoogleMap map;
    private Marker marker;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_location);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        // Building the GoogleApi client
        buildGoogleApiClient();

        //Initialize progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Finding the location");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);

        //confirm button clicked
        View addButton = this.findViewById(R.id.confirm);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangeLocationActivity.this, PlanActivity.class);
                intent.putExtra("className", "ChangeLocationActivity");
                intent.putExtra("latitude", mLastLocation.getLatitude());
                intent.putExtra("longitude", mLastLocation.getLongitude());
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_action, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599

                new UpdateMap(getApplicationContext()).execute(query);

                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //searchItem.expandActionView();
        //searchView.requestFocus();

        return super.onCreateOptionsMenu(menu);

    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        mResultReceiver = new AddressResultReceiver(new Handler());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, "No geocoder available",
                        Toast.LENGTH_LONG).show();
                return;
            }

            startFetchAddressIntentService();
        }
        //Log.d(TAG, "Location found");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // TODO
        //Log.d(TAG, "Connection suspended!");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // TODO
        //Log.d(TAG, "Connection failed!");
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            LatLng loc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            latitude = loc.latitude;
            longitude = loc.longitude;

            UiSettings mapUiSettings = map.getUiSettings();

            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
            mapUiSettings.setZoomControlsEnabled(true);
            mapUiSettings.setCompassEnabled(true);

            marker = map.addMarker(new MarkerOptions()
                    .position(loc)
                    .draggable(true)
                    .title("Move to change location"));

            map.setOnMarkerDragListener(this);

        } else {
            Toast.makeText(ChangeLocationActivity.this, "Cannot detect your location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMarkerDrag(Marker marker){
    }

    @Override
    public void onMarkerDragStart(Marker marker){
    }

    @Override
    public void onMarkerDragEnd(Marker marker){
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        mLastLocation.setLatitude(latitude);
        mLastLocation.setLongitude(longitude);

        startFetchAddressIntentService();
    }

    protected void startFetchAddressIntentService() {
        mResultReceiver = new AddressResultReceiver(new Handler());
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    private class UpdateMap extends AsyncTask<String, Void, LatLng> {
        private Context context;

        UpdateMap(Context context){
            this.context = context;
        }

        @Override
        protected LatLng doInBackground(String... query) {
            Geocoder coder = new Geocoder(context);
            List<Address> address;
            LatLng loc;

            try {
                address = coder.getFromLocationName(query[0],5);
                if (address == null) {
                    return null;
                } else {
                    Address location=address.get(0);

                    loc = new LatLng(location.getLatitude(), location.getLongitude());

                    latitude = loc.latitude;
                    longitude = loc.longitude;

                    return loc;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mProgressDialog.show();
        }
        @Override
        protected void onPostExecute(LatLng loc) {
            mProgressDialog.dismiss();
            if(loc != null) {
                mLastLocation.setLatitude(loc.latitude);
                mLastLocation.setLongitude(loc.longitude);
                marker.setPosition(loc);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, 15);
                map.animateCamera(cameraUpdate);
                startFetchAddressIntentService();
            } else {
                Toast.makeText(context,"Address not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            address = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
            if (resultCode == FetchAddressIntentService.Constants.SUCCESS_RESULT) {
                TextView addr = (TextView) findViewById(R.id.address);
                addr.setText(address);
            }
            //Toast.makeText(getApplicationContext(), mAddressOutput, Toast.LENGTH_LONG).show();

        }
    }

}