package com.adhiwie.moodjournal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adhiwie.moodjournal.model.UserData;
import com.adhiwie.moodjournal.receiver.ReminderReceiver;
import com.adhiwie.moodjournal.receiver.ResetDailyReminderReceiver;
import com.adhiwie.moodjournal.service.KeepAppRunningService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "123";
    private static final String TAG = "MainActivity" ;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private TextView dailyReminderView;
    private Button answerNowView;
    private Button changePlanView;
    private String planText;
    private long group;
    private long groupIsSet;
    private long dailyReminderStatus;

    /* For plan details */
    private String time;
    private String address;
    private double latitude;
    private double longitude;
    private long timeInMilis;
    private int preTest;
    private int postTest;
    private int isQuestionnaire;
    private String name;

    private TextView mPlanTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dailyReminderView = findViewById(R.id.daily_reminder);
        answerNowView = findViewById(R.id.answer_now);
        changePlanView = findViewById(R.id.change_plan);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        startService(new Intent(getApplicationContext(), KeepAppRunningService.class));

        resetDailyReminderStatus();

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
                preTest = user.getPre_test();
                postTest = user.getPost_test();
                isQuestionnaire = user.is_questionnaire();
                name = user.getUser_name();

                mPlanTextView = findViewById(R.id.plan);
                mPlanTextView.setText(planText);

                /*
                /
                 */

                if (dailyReminderStatus > 0) {
                    dailyReminderView.setText(R.string.message_mood_reported);
                    answerNowView.setVisibility(View.GONE);
                } else {
                    dailyReminderView.setText(R.string.message_mood_not_reported);
                    answerNowView.setVisibility(View.VISIBLE);
                }

                if (group != 0 ) {
                    changePlanView.setVisibility(View.VISIBLE);
                } else {
                    changePlanView.setVisibility(View.GONE);
                }

                startDailyReminder();
                //pd.dismiss();

                if (groupIsSet == 0 && group !=0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Reminder Settings");
                    builder.setMessage("Do you know that you can change your reminder settings? Tap the button below to change it.");
                    builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
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

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show();
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

    private void resetDailyReminderStatus() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), ResetDailyReminderReceiver.class);
        intent.putExtra("uid", mUser.getUid());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400000, alarmIntent);
    }

    private void startDailyReminder() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMilis);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), ReminderReceiver.class);
        intent.putExtra("uid", mUser.getUid());
        intent.putExtra("plan", planText);
        intent.putExtra("group", group);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("timeInString", time);
        // TODO set current location before sending a reminder
        intent.putExtra("currentLatitude",0);
        intent.putExtra("currentLongitude", 0);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400000, alarmIntent);
    }

    private void answerQuestionnaire() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        if (preTest == 1) {
            builder.setTitle("Pre-test Questionnaire");
        } else {
            builder.setTitle("Post-test Questionnaire");
        }
        builder.setMessage("Please answer the following questionnaire, it's important for our study.");
        builder.setPositiveButton("Answer questionnaire", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String urlPreTest1 = "https://docs.google.com/forms/d/e/1FAIpQLSdkXpxR8BZe2UMwwKcq1t18N9d9II0yuJy8GC-8eaUWUjl8hA/viewform?usp=pp_url&entry.503799090=";
                String urlPreTest2 = "&entry.584420084&entry.788256975";

                String urlPostTest1 = "https://docs.google.com/forms/d/e/1FAIpQLSeBrAkHZMGR2pre63OpyZLCLt5oVg78FyTvxiNf-t0_aTZC9Q/viewform?usp=pp_url&entry.369076977=";
                String urlPostTest2 = "&entry.1519610942";
                Intent intent = new Intent(MainActivity.this, QuestionnaireActivity.class);
                if (preTest == 1) {
                    intent.putExtra("url", urlPreTest1+mUser.getUid()+urlPreTest2);
                } else {
                    intent.putExtra("url", urlPostTest1+mUser.getUid()+urlPostTest2);
                }
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
