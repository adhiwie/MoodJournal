package com.adhiwie.mymoodjournal.user.permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.adhiwie.mymoodjournal.MainActivity;
import com.adhiwie.mymoodjournal.R;
import com.adhiwie.mymoodjournal.debug.CustomExceptionHandler;
import com.adhiwie.mymoodjournal.utils.Log;
import com.adhiwie.mymoodjournal.utils.Popup;

public class AccessibilityServicePermissionActivity extends Activity {


    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_permission);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (new Permission(getApplicationContext()).isAccessibilityPermitted()) {
            new Log().v("Permission granted");
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        } else {
            new Popup().showPopup(AccessibilityServicePermissionActivity.this, "Permission Required",
                    "Provide the permission to keep the app running. "
                            + "\n\n"
                            + "If you have previously given this permission, please reset it by diabling and enabling it again.");
        }
    }

    public void openSettings(View v) {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, 0);
    }

}
