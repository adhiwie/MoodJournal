package com.adhiwie.diary.user.permission;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.adhiwie.diary.MainActivity;
import com.adhiwie.diary.R;
import com.adhiwie.diary.debug.CustomExceptionHandler;
import com.adhiwie.diary.utils.Log;
import com.adhiwie.diary.utils.Popup;

public class NSLPermissionActivity extends AppCompatActivity {


    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsl_permission);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (new Permission(getApplicationContext()).isNSLPermitted()) {
            new Log().v("Permission granted");
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        } else {
            new Popup().showPopup(NSLPermissionActivity.this, "Permission Required",
                    "Provide the permission to keep the app running. "
                            + "\n\n"
                            + "If you have previously given this permission, please reset it by disabling and enabling it again.");
        }
    }

    public void openSettings(View v) {
        Intent i = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivityForResult(i, 0);
    }
}
