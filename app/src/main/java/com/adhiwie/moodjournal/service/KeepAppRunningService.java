package com.adhiwie.moodjournal.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.model.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class KeepAppRunningService extends Service {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private long group;
    private long groupIsSet;

    /* For plan details */
    /*
    private String time;
    private String address;
    private double latitude;
    private double longitude;
    private long timeInMilis;
    private int preTest;
    private int postTest;
    private int isQuestionnaire;
    private String name;
    */


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get data from Firebase
        getData();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO for communication return IBinder implementation
        return null;
    }


    private void getData() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData user  = dataSnapshot.child("users").child(mUser.getUid()).getValue(UserData.class);
                groupIsSet = user.getGroup_is_set();
                group = user.getGroup_id();
                /*
                timeInMilis = user.getDaily_reminder_time();
                time = user.getDaily_reminder_time_string();
                address = user.getDaily_reminder_address();
                latitude = user.getDaily_reminder_latitude();
                longitude = user.getDaily_reminder_longitude();
                preTest = user.getPre_test();
                postTest = user.getPost_test();
                isQuestionnaire = user.is_questionnaire();
                name = user.getUser_name();
                */

                Log.d("KeepAppRunningService", "Service is running");
                Log.d("KeepAppRunningService", "Group is "+String.valueOf(group));
                Log.d("KeepAppRunningService", "Group is set value "+String.valueOf(groupIsSet));

                if (groupIsSet == 0 && group !=0) {
                    Intent dialogIntent = new Intent(getApplicationContext(), MainActivity.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
/*
public class KeepAppRunningService extends IntentService {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private long group;
    private long groupIsSet;

    public KeepAppRunningService() {
        super("KeepAppRunningService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData user  = dataSnapshot.child("users").child(mUser.getUid()).getValue(UserData.class);
                groupIsSet = user.getGroup_is_set();
                group = user.getGroup_id();

                Log.d("KeepAppRunningService", "Service is running");
                Log.d("KeepAppRunningService", "Group is "+String.valueOf(group));
                Log.d("KeepAppRunningService", "Group is set value "+String.valueOf(groupIsSet));

                if (groupIsSet == 0 && group !=0) {
                    Intent dialogIntent = new Intent(getApplicationContext(), MainActivity.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
*/