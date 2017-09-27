package com.adhiwie.moodjournal;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MoodJournalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


    }
}
