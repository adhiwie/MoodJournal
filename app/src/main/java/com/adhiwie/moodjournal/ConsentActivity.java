package com.adhiwie.moodjournal;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.user.data.UserRegistrationActivity;
import com.adhiwie.moodjournal.utils.SharedPref;

import androidx.appcompat.app.AppCompatActivity;

public class ConsentActivity extends AppCompatActivity {

    private Button consent_btn;
    private Switch consent_switch;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }

        consent_btn = (Button) findViewById(R.id.consent_btn);
        consent_switch = (Switch) findViewById(R.id.consent_switch);

        consent_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (consent_switch.isChecked())
                    consent_btn.setEnabled(true);
                else
                    consent_btn.setEnabled(false);
            }
        });
    }

    public void iAgreeBtnClick(View v) {
//        addShortcutIcon(MainActivity.class, getResources().getString(R.string.app_name));
//        addShortcutIconNewerAndroid(MainActivity.class);

        startActivity(new Intent(this, UserRegistrationActivity.class));
        finish();
    }


    private void addShortcutIcon(Class<MainActivity> launcher_activity_class, String title) {
        SharedPref sp = new SharedPref(getApplicationContext());
        String ICON_PLACED_STATUS = "ICON_PLACED_STATUS";
        if (!sp.getBoolean(ICON_PLACED_STATUS)) {
            Intent shortcutIntent = new Intent(this, launcher_activity_class);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Intent addIntent = new Intent();
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
            addIntent.putExtra("duplicate", false);
            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            sendBroadcast(addIntent);
            sp.add(ICON_PLACED_STATUS, true);
        }

    }

    private void addShortcutIconNewerAndroid(Class<MainActivity> launcher_activity_class) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

            ShortcutManager shortcutManager =
                    getApplication().getSystemService(ShortcutManager.class);

            if (shortcutManager.isRequestPinShortcutSupported()) {
                Intent shortcutIntent = new Intent(this, launcher_activity_class);
                shortcutIntent.setAction(Intent.ACTION_VIEW);

                ShortcutInfo shortcut = new ShortcutInfo.Builder(getApplicationContext(), "shortcut")
                        .setShortLabel(getResources().getString(R.string.app_name))
                        .setIcon(Icon.createWithResource(getApplicationContext(), R.drawable.ic_launcher))
                        .setIntent(shortcutIntent)
                        .build();

                Intent pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(shortcut);
                PendingIntent successCallback = PendingIntent.getBroadcast(this, 0, pinnedShortcutCallbackIntent, 0);
                shortcutManager.requestPinShortcut(shortcut, successCallback.getIntentSender());
            }
        }
    }


}
