package com.adhiwie.diary.communication.helper;

import android.content.Context;

import com.adhiwie.diary.communication.DataTransmitter;
import com.adhiwie.diary.debug.CustomExceptionHandler;
import com.adhiwie.diary.plan.PlanMgr;
import com.adhiwie.diary.utils.DataTypes;
import com.adhiwie.diary.utils.Log;
import com.adhiwie.diary.utils.SharedPref;

public class PlanDataTransmission {

    private final String PLAN_DATA_TRANSMITTER = "PLAN_DATA_TRANSMITTER";
    private final String PLAN_DATA_AVAILABLE = "PLAN_DATA_AVAILABLE";
    private final Context context;
    private final SharedPref sp;
    private final String data;


    public PlanDataTransmission(Context context) {
        this.context = context;
        this.sp = new SharedPref(context);
        this.data = new PlanMgr(context).getPlanDataString();

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));
        }
    }


    public boolean isDataAvailable() {
        return sp.getBoolean(PLAN_DATA_AVAILABLE);
    }

    public boolean isDataTransmitted() {
        return sp.getBoolean(PLAN_DATA_TRANSMITTER);
    }


    public void transmitData() {
        sp.add(PLAN_DATA_AVAILABLE, true);

        try {
            new DataTransmitter(this.context, new DataTypes().PLAN, data) {
                @Override
                protected void onPostExecute(Boolean result) {
                    new Log().e("Plan data transmission result: " + result);
                    if (result)
                        sp.add(PLAN_DATA_TRANSMITTER, true);
                }

                ;
            }.execute();
        } catch (Exception e) {
            sp.add(PLAN_DATA_TRANSMITTER, false);
        }
    }


}
