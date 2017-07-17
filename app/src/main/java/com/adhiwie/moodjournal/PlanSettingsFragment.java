package com.adhiwie.moodjournal;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

public class PlanSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
