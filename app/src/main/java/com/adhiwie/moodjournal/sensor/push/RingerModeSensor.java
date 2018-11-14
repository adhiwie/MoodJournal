package com.adhiwie.moodjournal.sensor.push;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.adhiwie.moodjournal.LinkedTasks;
import com.adhiwie.moodjournal.file.FileMgr;
import com.adhiwie.moodjournal.sensor.data.RingerModeData;
import com.adhiwie.moodjournal.system.APILevel;
import com.adhiwie.moodjournal.utils.Log;

public class RingerModeSensor extends BroadcastReceiver {


    public final String RINGER_MODE_SOUND = "SOUND";
    public final String RINGER_MODE_SILENT = "SILENT";
    public final String RINGER_MODE_VIBRATE = "VIBRATE";


    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log log = new Log();
        try {
            log.v("New Ringer Mode");
            Bundle b = intent.getExtras();
            String key = "android.media.EXTRA_RINGER_MODE";
            String ringer_mode = getRingerModeValue(b.getInt(key));
            int zen_mode_code = -1;
            try {
                if (new APILevel().getDeviceAPILevel() >= Build.VERSION_CODES.LOLLIPOP) // zen mode was introduced in Lolipop
                    zen_mode_code = Settings.Global.getInt(context.getContentResolver(), "zen_mode");

            } catch (Exception e) {
                new Log().e(e.toString());
            }
            String zen_mode = getZenModeValue(zen_mode_code);

            RingerModeData rm_data = new RingerModeData(ringer_mode, zen_mode, Calendar.getInstance().getTimeInMillis());

            FileMgr fm = new FileMgr(context);
            fm.addData(rm_data);
            log.d("Ringer Mode result: " + rm_data.toJSONString());

            // check all linked tasks
            try {
                new LinkedTasks(context).checkAll();
            } catch (Exception e) {
            }

        } catch (Exception e) {
            log.e(e.toString());
        }
    }


    private String getRingerModeValue(int code) {
        switch (code) {
            case 0:
                return RINGER_MODE_SILENT;
            case 1:
                return RINGER_MODE_VIBRATE;
            case 2:
                return RINGER_MODE_SOUND;
            default:
                return "UNKNOWN";
        }
    }

    private String getZenModeValue(int code) {
        String INTERRUPTION_MODE_ALL = "ALL";
        String INTERRUPTION_MODE_PRIORITY = "PRIORITY";
        String INTERRUPTION_MODE_NONE = "NONE";

        switch (code) {
            case 0:
                return INTERRUPTION_MODE_ALL;
            case 1:
                return INTERRUPTION_MODE_PRIORITY;
            case 2:
                return INTERRUPTION_MODE_NONE;
            default:
                return "UNKNOWN";
        }
    }


}
