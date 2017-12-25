package com.adhiwie.moodjournal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetDailyReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(intent.getStringExtra("uid")).child("daily_reminder_status").setValue(0);
    }
}
