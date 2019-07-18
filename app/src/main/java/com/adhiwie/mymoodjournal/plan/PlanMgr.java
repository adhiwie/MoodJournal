package com.adhiwie.mymoodjournal.plan;

import android.content.Context;

import com.adhiwie.mymoodjournal.utils.SharedPref;

public class PlanMgr {

    private final SharedPref sp;

    public PlanMgr(Context context) {
        this.sp = new SharedPref(context);
    }

    private final String USER_PLAN_STATUS = "USER_PLAN_STATUS";
    private final String USER_PLAN_HOUR = "USER_PLAN_HOUR";
    private final String USER_PLAN_MINUTE = "USER_PLAN_MINUTE";
    private final String USER_PLAN_ROUTINE_DESC = "USER_PLAN_ROUTINE_DESC";
    private final String USER_PLAN_TIMING = "USER_PLAN_TIMING";
    private final String USER_PLAN_DATA_STRING = "USER_PLAN_DATA_STRING";

    public boolean isPlanGiven() {
        return sp.getBoolean(USER_PLAN_STATUS);
        //return true;
    }

    public void setPlanGiven() {
        sp.add(USER_PLAN_STATUS, true);
    }

    public String getPlanTiming() {
        return sp.getString(USER_PLAN_TIMING);
    }

    public void setPlanTiming(String timing) {
        sp.add(USER_PLAN_TIMING, timing);
    }

    public int getPlanHour() {
        return sp.getInt(USER_PLAN_HOUR);
    }

    public void setPlanHour(int hour) {
        sp.add(USER_PLAN_HOUR, hour);
    }

    public int getPlanMinute() {
        return sp.getInt(USER_PLAN_MINUTE);
    }

    public void setPlanMinute(int minute) {
        sp.add(USER_PLAN_MINUTE, minute);
    }

    public String getPlanRoutineDesc() {
        return sp.getString(USER_PLAN_ROUTINE_DESC);
    }

    public void setPlanRoutineDesc(String routine) {
        sp.add(USER_PLAN_ROUTINE_DESC, routine);
    }

    public String getPlanDataString() {
        return sp.getString(USER_PLAN_DATA_STRING);
    }

    public void setPlanDataString(String planDataString) {
        sp.add(USER_PLAN_DATA_STRING, planDataString);
    }
}
