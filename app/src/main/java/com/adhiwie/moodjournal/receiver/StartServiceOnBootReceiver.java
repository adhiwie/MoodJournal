package com.adhiwie.moodjournal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adhiwie.moodjournal.service.KeepAppRunning;

public class StartServiceOnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, KeepAppRunning.class);
        context.startService(startServiceIntent);
    }
}
