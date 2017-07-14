package com.adhiwie.moodjournal;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MoodJournalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/TTNorms-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

    }
}
