package com.adhiwie.diary.user.permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.adhiwie.diary.MainActivity;
import com.adhiwie.diary.R;
import com.adhiwie.diary.debug.CustomExceptionHandler;
import com.adhiwie.diary.utils.Log;
import com.adhiwie.diary.utils.Popup;

public class AppUsagePermissionActivity extends Activity {

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage_permission);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (new Permission(getApplicationContext()).isAppAccessPermitted()) {
            new Log().v("Permission granted");
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        } else {
            new Popup().showPopup(AppUsagePermissionActivity.this, "Permission Required",
                    "Provide the permission to keep the app running. "
                            + "\n\n"
                            + "If you have previously given this permission, please reset it by diabling and enabling it again.");
        }
    }


    @SuppressLint("NewApi")
    public void openSettings(View v) {

        try {
            Intent i = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(i, 0);
        } catch (Exception e) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$UsageAccessSettingsActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e2) {
                new Log().e(e2.toString());
            }
        }
    }

}