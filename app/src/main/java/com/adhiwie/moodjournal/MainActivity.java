package com.adhiwie.moodjournal;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adhiwie.moodjournal.model.UserData;
import com.adhiwie.moodjournal.service.FetchAddressIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import com.adhiwie.moodjournal.service.FetchAddressIntentService.Constants;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "123";
    private static final String TAG = "MainActivity" ;
    private TextView mTextMessage;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TextView dailyReminderView;
    private TextView answerNowView;
    private TextView changePlanView;
    private boolean isLocation = false;
    private String planText;
    private long group;
    private long groupIsSet;
    private long dailyReminderStatus;

    private GoogleApiClient mApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = false;
    private Location mLastLocation;
    public AddressResultReceiver mResultReceiver;

    /* For plan details */
    private String time;
    private String address;
    private double latitude;
    private double longitude;
    private long timeInMilis;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    public void onStart() {
        super.onStart();
        dailyReminderView = (TextView) findViewById(R.id.daily_reminder);
        answerNowView = (TextView) findViewById(R.id.answer_now);
        changePlanView = (TextView) findViewById(R.id.change_button);

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait, we are refreshing the data.");
        pd.setCancelable(false);
        pd.show();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData user  = dataSnapshot.child("users").child(mUser.getUid()).getValue(UserData.class);
                planText = user.getPlan();
                group = user.getGroup_id();
                groupIsSet = user.getGroup_is_set();
                timeInMilis = user.getDaily_reminder_time();
                time = user.getDaily_reminder_time_string();
                address = user.getDaily_reminder_address();
                latitude = user.getDaily_reminder_latitude();
                longitude = user.getDaily_reminder_longitude();
                dailyReminderStatus = user.getDaily_reminder_status();

                TextView tv = (TextView) findViewById(R.id.plan);
                tv.setText(planText);

                if (dailyReminderStatus > 0) {
                    dailyReminderView.setText("Awesome! You have completed the mood questionnaires for today. Have a good day :)");
                    answerNowView.setVisibility(View.GONE);
                } else {
                    dailyReminderView.setText("You have not completed the questionnaires for today");
                    answerNowView.setVisibility(View.VISIBLE);
                }


                if (group != 0 ) {
                    if (dailyReminderStatus == 0) startDailyReminder();
                    changePlanView.setVisibility(View.VISIBLE);
                } else {
                    changePlanView.setVisibility(View.GONE);
                }
                pd.dismiss();

                if (groupIsSet == 0 && group !=0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setMessage("You need to specify the condition for your reminder. Tap the button below to set it.");
                    builder.setPositiveButton("Set reminder", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                            dbRef.child("users").child(mUser.getUid()).child("group_is_set").setValue(1);
                            Intent intent = new Intent(MainActivity.this, PlanActivity.class);
                            intent.putExtra("className", "MainActivity");
                            intent.putExtra("plan", planText);
                            intent.putExtra("time", time);
                            intent.putExtra("timeInMilis", timeInMilis);
                            intent.putExtra("address", address);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);
                            intent.putExtra("group", group);
                            startActivity(intent);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient(this);

        initVariables();

        getLastLocation();
        createLocationRequest();
        setLocationCallback();
        resetDailyReminderStatus();

        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    protected void initVariables() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mTextMessage = (TextView) findViewById(R.id.message);
        mTextMessage.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        super.onSaveInstanceState(outState);
    }

    private void signOut() {
        mAuth.signOut();
    }

    public void answerNow(View view) {
        Intent intent = new Intent(MainActivity.this, MoodQuestionActivity.class);
        startActivity(intent);
    }

    public void changePlan(View view) {
        Intent intent = new Intent(MainActivity.this, PlanActivity.class);
        intent.putExtra("className", "MainActivity");
        intent.putExtra("plan", planText);
        intent.putExtra("time", time);
        intent.putExtra("address", address);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("group", group);
        intent.putExtra("timeInMilis", timeInMilis);
        startActivity(intent);
    }



    protected void getLastLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mLastLocation = location;
                            startIntentService();
                        }
                    }
                });
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1800000);
        mLocationRequest.setFastestInterval(900000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.build();
    }

    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    protected void startIntentService() {
        mResultReceiver = new AddressResultReceiver(new Handler());
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    protected void setLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLastLocation = location;
                }
            }
        };
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Cannot connect to Google API", Toast.LENGTH_SHORT).show();

    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (isLocation) {
                writeLocationAddressToFirebase(mAddressOutput);
            }

           //Toast.makeText(getApplicationContext(), mAddressOutput, Toast.LENGTH_LONG).show();

        }
    }

    private void buildGoogleApiClient(Context context) {
        mApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void writeLocationAddressToFirebase(String address) {
        Map<String, Object> val = new HashMap<>();
        val.put("latitude", mLastLocation.getLatitude());
        val.put("longitude", mLastLocation.getLongitude());
        val.put("address", address);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(mUser.getUid()).child("location").setValue(val);
    }

    private void resetDailyReminderStatus() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), DailyReminderReceiver.class);
        intent.putExtra("uid", mUser.getUid());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400000, alarmIntent);

        //Log.i(TAG, "daily reminder service");
    }

    private void startDailyReminder() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMilis);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("plan", planText);
        intent.putExtra("group", group);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("currentLatitude", mLastLocation.getLatitude());
        intent.putExtra("currentLongitude", mLastLocation.getLongitude());

        Log.d(TAG, "Current location: "+String.valueOf(mLastLocation.getLatitude())+","+String.valueOf(mLastLocation.getLongitude()));
        Log.d(TAG, "Designated location: "+String.valueOf(latitude)+","+String.valueOf(longitude));

        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400000, alarmIntent);


        //java.text.DateFormat dateFormat = DateFormat.getDateTimeInstance();
        //Log.i(TAG, dateFormat.format(calendar.getTimeInMillis()));
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String plan = intent.getStringExtra("plan");
            String action = plan.substring(plan.indexOf("will") + 5, plan.length());

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

            boolean isLocation = false;

            if (currentLocation.distanceTo(designatedLocation) < 50) {
                isLocation = true;
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
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

            if (group == 1) {
                mNotificationManager.notify(123, mBuilder.build());
            } else if (group == 2 && isLocation) {
                mNotificationManager.notify(123, mBuilder.build());
            }

        }

    }

    public static class DailyReminderReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("users").child(intent.getStringExtra("uid")).child("daily_reminder_status").setValue(0);
            //Log.i(TAG, "daily reminder received");
        }
    }
}
