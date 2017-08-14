package com.adhiwie.moodjournal;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
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

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PlanActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private long dailyReminderTime;
    private TextView locationTextView;
    private TextView timeTextView;
    private TextView planTextView;
    private static String time;
    private static String address;
    private String plan;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        initStuff();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences("TIME_PREF", 0);
                sharedPref.edit().clear().apply();

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

                Toast.makeText(getApplicationContext(), "Your plan has been changed.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(PlanActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //getSupportFragmentManager().beginTransaction().(R.id.fragment, new PlanSettingsFragment()).commit();
    }

    private void initStuff() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        locationTextView = (TextView) findViewById(R.id.location);
        timeTextView = (TextView) findViewById(R.id.time);
        planTextView = (TextView) findViewById(R.id.plan);
        if (getIntent().getStringExtra("className").equals("MainActivity")) {
            time = getIntent().getStringExtra("time");
            address = getIntent().getStringExtra("address");
            plan = getIntent().getStringExtra("plan");
            latitude = getIntent().getDoubleExtra("latitude", 0);
            longitude = getIntent().getDoubleExtra("longitude", 0);

            planTextView.setText(getIntent().getStringExtra("plan"));

            if (!address.equals("")) locationTextView.setText(address);
            if (!time.equals("")) timeTextView.setText(time);

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
            String planStr2 = ", THEN I will complete the mood questionnaires";

            String str = planStr + timeTextView.getText().toString() + " and I am at "+ address + planStr2;
            Log.i("PlanActivity", str);

            planTextView.setText(str);
        }
    }

    public void getDailyReminderTime(long dailyReminderTime) {
        this.dailyReminderTime = dailyReminderTime;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
            String planStr2 = ", THEN I will complete the mood questionnaires";
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
            editor.apply();

            PlanActivity planActivity = (PlanActivity) getActivity();
            planActivity.getDailyReminderTime(notifiedAt);
        }
    }
}
