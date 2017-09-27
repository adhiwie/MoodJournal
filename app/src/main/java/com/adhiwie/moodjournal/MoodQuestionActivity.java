package com.adhiwie.moodjournal;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.adhiwie.moodjournal.model.MoodModel;
import com.adhiwie.moodjournal.service.FetchAddressIntentService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MoodQuestionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private View moodLayout;
    private Map<String, Object> moodValue;

    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    public AddressResultReceiver mResultReceiver;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_question);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        getLastLocation();

        final List<String> questionList = new ArrayList<>();
        final String[] questionArray = getResources().getStringArray(R.array.phq_questions);
        Collections.addAll(questionList, questionArray);

        moodLayout = findViewById(R.id.mood_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                SeekBar seekBar1 = findViewById(R.id.seekBar);
                SeekBar seekBar2 = findViewById(R.id.seekBar2);
                SeekBar seekBar3 = findViewById(R.id.seekBar3);

                int happinessScore = seekBar1.getProgress() + 1;
                int stressScore = seekBar2.getProgress() + 1;
                int arousalScore = seekBar3.getProgress() + 1;

                Calendar cal = Calendar.getInstance();
                long timestamp = cal.getTimeInMillis();

                double longitude;
                double latitude;
                String locAddress;

                if (mLastLocation == null) {
                    longitude = 0;
                    latitude = 0;
                    locAddress = "";
                } else {
                    longitude = mLastLocation.getLongitude();
                    latitude = mLastLocation.getLatitude();
                    locAddress = address;
                }

                com.adhiwie.moodjournal.model.Location location = new com.adhiwie.moodjournal.model.Location(longitude, latitude, locAddress);

                MoodModel moodModel = new MoodModel(mUser.getUid(), happinessScore, stressScore, arousalScore, timestamp, location);
                moodValue = moodModel.toMap();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child("users").child(mUser.getUid()).child("daily_reminder_status").setValue(1);
                dbRef.child("mood_score").push().setValue(moodValue);

                Toast.makeText(getApplicationContext(), "Thanks. Your data has been recorded!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
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

    protected void startIntentService() {
        mResultReceiver = new AddressResultReceiver(new Handler());
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    public class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            address = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);

        }
    }

}
