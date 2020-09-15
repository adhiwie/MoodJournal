package com.adhiwie.moodjournal.sensor.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adhiwie.moodjournal.LinkedTasks;
import com.adhiwie.moodjournal.exception.ConsetMissingException;

public class ScreenStateSensor extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            try {
                new LinkedTasks(context).checkQuestionnaires();
            } catch (ConsetMissingException e) {
                e.printStackTrace();
            }
        }
    }
}
