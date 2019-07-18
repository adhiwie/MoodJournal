package com.adhiwie.diary.sensor.push;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.adhiwie.diary.LinkedTasks;
import com.adhiwie.diary.debug.CustomExceptionHandler;
import com.adhiwie.diary.file.FileMgr;
import com.adhiwie.diary.sensor.data.PowerConnectionData;
import com.adhiwie.diary.utils.Log;

public class PowerConnectionSensor extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log log = new Log();
        try {
            log.v("New Power Connection");
            if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler))
                Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));

            boolean is_charging = isCharging(intent.getAction());
            String source = getPluggedState(context);
            float level = getBatteryLevel(context);
            PowerConnectionData pc = new PowerConnectionData(is_charging, source, level, Calendar.getInstance().getTimeInMillis());

            FileMgr fm = new FileMgr(context);
            fm.addData(pc);
            log.d("Power Connection Result: " + pc.toJSONString());

            // check all linked tasks
            try {
                new LinkedTasks(context).checkAll();
            } catch (Exception e) {
            }

        } catch (Exception e) {
            log.e(e.toString());
        }

    }

    private boolean isCharging(String action) {
        return action.equals(Intent.ACTION_POWER_CONNECTED);
    }

    private String getPluggedState(Context context) {
        Intent chargingIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        final int pluggedState = chargingIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        switch (pluggedState) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return PowerConnectionData.SOURCE_AC;
            case BatteryManager.BATTERY_PLUGGED_USB:
                return PowerConnectionData.SOURCE_USB;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return PowerConnectionData.SOURCE_WIRELESS;
            default:
                return PowerConnectionData.SOURCE_UNKNOWN;
        }
    }

    private float getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (level == -1 || scale == -1) {
            return 0.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }

}
