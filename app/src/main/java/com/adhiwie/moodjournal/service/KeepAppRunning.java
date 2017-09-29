package com.adhiwie.moodjournal.service;

import android.app.IntentService;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.PlanActivity;
import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.model.UserData;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class KeepAppRunning extends IntentService {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private long group;
    private long groupIsSet;

    public KeepAppRunning() {
        super("KeepAppRunning");
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

                Log.d("KeepAppRunning", "Service is running");
                Log.d("KeepAppRunning", "Group is "+String.valueOf(group));
                Log.d("KeepAppRunning", "Group is set value "+String.valueOf(groupIsSet));

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
