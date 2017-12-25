package com.adhiwie.moodjournal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adhiwie.moodjournal.service.KeepAppRunningService;

public class StartServiceOnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, KeepAppRunningService.class);
        context.startService(startServiceIntent);
    }
}
