package com.adhiwie.mymoodjournal.sensor.pull;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.usage.UsageEvents;
import android.app.usage.UsageEvents.Event;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import com.adhiwie.mymoodjournal.file.DataInterface;
import com.adhiwie.mymoodjournal.sensor.data.PhoneUsageData;
import com.adhiwie.mymoodjournal.utils.Log;

public class PhoneUsageSensor {

    private final Context context;

    public PhoneUsageSensor(Context context) {
        this.context = context;
    }


    @SuppressLint("NewApi")
    public ArrayList<DataInterface> getPhoneUsageEvents(Calendar c1, Calendar c2) {
        ArrayList<DataInterface> data = new ArrayList<DataInterface>();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager manager = (UsageStatsManager) context.getSystemService("usagestats");
                ;
                UsageEvents ues = manager.queryEvents(c1.getTimeInMillis(), c2.getTimeInMillis());

                Event e = new Event();
                while (ues.getNextEvent(e)) {
                    long event_time = e.getTimeStamp();
                    String pack_name = e.getPackageName();
                    int event_type = e.getEventType();

                    if (event_type == Event.MOVE_TO_FOREGROUND) {
                        data.add(new PhoneUsageData(event_time, "MOVE_TO_FOREGROUND", pack_name));
                    } else if (event_type == Event.MOVE_TO_BACKGROUND) {
                        data.add(new PhoneUsageData(event_time, "MOVE_TO_BACKGROUND", pack_name));
                    }
                }
                e = new Event();
            }
        } catch (Exception e) {
            new Log().e(e.toString());
        }
        return data;
    }

}
