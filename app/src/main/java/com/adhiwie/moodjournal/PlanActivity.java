package com.adhiwie.moodjournal;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PlanActivity extends AppCompatActivity {

    private static final String TAG = "PlanActivity" ;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private long dailyReminderTime;
    private TextView locationTextView;
    private TextView timeTextView;
    private TextView planTextView;
    private static String time;
    private long timeInMilis;
    private static String address;
    private String plan;
    private double latitude;
    private double longitude;
    private long group;
    private LinearLayout locationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        initStuff();

        Button fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences("TIME_PREF", 0);
                if (sharedPref.contains("timeInMilis")) {
                    dailyReminderTime = sharedPref.getLong("timeInMilis", 0);
                }


                TextView plan = (TextView) findViewById(R.id.plan);

                Map<String, Object> userDataValue = new HashMap<>();
                userDataValue.put("plan", plan.getText().toString());
                userDataValue.put("daily_reminder_time", dailyReminderTime);
                userDataValue.put("daily_reminder_time_string", time);
                userDataValue.put("daily_reminder_address", address);
                userDataValue.put("daily_reminder_latitude", latitude);
                userDataValue.put("daily_reminder_longitude", longitude);
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child("users").child(mUser.getUid()).updateChildren(userDataValue);

                //Log.d(TAG, String.valueOf(dailyReminderTime));
                sharedPref.edit().clear().apply();

                Toast.makeText(getApplicationContext(), "Your plan has been changed.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(PlanActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initStuff() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        locationLayout = (LinearLayout) findViewById(R.id.locationLayout);
        locationTextView = (TextView) findViewById(R.id.location);
        timeTextView = (TextView) findViewById(R.id.time);
        planTextView = (TextView) findViewById(R.id.plan);
        address = "";
        latitude = 0;
        longitude = 0;

        if (getIntent().getStringExtra("className") != null) {
            if (getIntent().getStringExtra("className").equals("MainActivity")) {
                time = getIntent().getStringExtra("time");
                timeInMilis = getIntent().getLongExtra("timeInMilis", 0);
                address = getIntent().getStringExtra("address");
                plan = getIntent().getStringExtra("plan");
                latitude = getIntent().getDoubleExtra("latitude", 0);
                longitude = getIntent().getDoubleExtra("longitude", 0);
                group = getIntent().getLongExtra("group", 0);

                planTextView.setText(getIntent().getStringExtra("plan"));

                if (!address.equals("")) locationTextView.setText(address);
                if (!time.equals("")) {
                    timeTextView.setText(time);
                    SharedPreferences sharedPref = getSharedPreferences("TIME_PREF", 0);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("time", time);
                    editor.putLong("timeInMilis", timeInMilis);
                    editor.apply();
                }

                if (group == 1) {
                    locationLayout.setVisibility(View.GONE);
                } else {
                    locationLayout.setVisibility(View.VISIBLE);
                }

            } else if (getIntent().getStringExtra("className").equals("ChangeLocationActivity")) {
                SharedPreferences sharedPref = getSharedPreferences("TIME_PREF", 0);
                if (!sharedPref.getString("time", "").equals("")) {
                    timeTextView.setText(sharedPref.getString("time", ""));
                }

                address = getIntent().getStringExtra("address");
                locationTextView.setText(address);
                latitude = getIntent().getDoubleExtra("latitude", 0);
                longitude = getIntent().getDoubleExtra("longitude", 0);

                String planStr = "IF the time is ";
                String planStr2 = ", THEN I will record my mood.";
                String str;

                if (!sharedPref.getString("time", "").equals("")) {
                    timeTextView.setText(sharedPref.getString("time", ""));
                    str = planStr + timeTextView.getText().toString() + " and I am at "+ address + planStr2;
                } else {
                    str = " IF I am at "+ address + planStr2;
                }

                Log.i("PlanActivity", str);

                planTextView.setText(str);
            }
        }


    }

    public void setDailyReminderTime(long dailyReminderTime) {
        this.dailyReminderTime = dailyReminderTime;
    }

    public void onSetTimeClicked(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"TimePicker");
    }

    public void onChangeLocationClick(View v) {
        Intent intent = new Intent(this, ChangeLocationActivity.class);
        startActivity(intent);
    }

    @SuppressLint("ValidFragment")
    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        private long notifiedAt;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //Use the current time as the default values for the time picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            //Create and return a new instance of TimePickerDialog
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        //onTimeSet() callback method
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            //Do something with the user chosen time
            //Get reference of host activity (XML Layout File) TextView widget

            //Set a message for user
            String mins = "";
            if (minute < 10) {
                mins = "0" + String.valueOf(minute);
            } else {
                mins = String.valueOf(minute);
            }
            timeTextView.setText(String.valueOf(hourOfDay) + ":" + mins);
            time = timeTextView.getText().toString();

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            notifiedAt = cal.getTimeInMillis();

            String planStr = "IF the time is ";
            String planStr2 = ", THEN I will record my mood.";
            String str;

            if (!address.equals("")) {
                str = planStr + timeTextView.getText().toString() + " and I am at "+ address + planStr2;
            } else {
                str = planStr + timeTextView.getText().toString() + planStr2;
            }
            planTextView.setText(str);

            SharedPreferences sharedPref = getSharedPreferences("TIME_PREF", 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("time", time);
            editor.putLong("timeInMilis", notifiedAt);
            editor.apply();

            PlanActivity planActivity = (PlanActivity) getActivity();
            planActivity.setDailyReminderTime(notifiedAt);
        }
    }
}
