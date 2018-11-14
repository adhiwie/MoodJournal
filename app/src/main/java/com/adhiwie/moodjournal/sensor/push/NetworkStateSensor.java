package com.adhiwie.moodjournal.sensor.push;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.adhiwie.moodjournal.LinkedTasks;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.file.FileMgr;
import com.adhiwie.moodjournal.sensor.data.NetworkStateData;
import com.adhiwie.moodjournal.utils.Log;

public class NetworkStateSensor extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log log = new Log();
        try {
            log.v("New Network State");
            if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler))
                Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));

            Bundle b = intent.getExtras();
            NetworkInfo ni = (NetworkInfo) b.get("networkInfo");

            String type = ni.getTypeName();
            String description = ni.getExtraInfo();
            String state = ni.getState().name();
            long time = Calendar.getInstance().getTimeInMillis();

            if (type == null)
                type = "unknown";
            if (description == null)
                description = "unknown";

            NetworkStateData nsd = new NetworkStateData(type, description, state, time);

            FileMgr fm = new FileMgr(context);
            fm.addData(nsd);
            log.d("Network Result: " + nsd.toJSONString());

            // check all linked tasks
            try {
                new LinkedTasks(context).checkAll();
            } catch (Exception e) {
            }
        } catch (Exception e) {
            log.e(e.toString());
        }
    }

}