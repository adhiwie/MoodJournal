package com.adhiwie.diary;

import android.content.Context;

import com.adhiwie.diary.utils.SharedPref;

public class ConsentMgr {

    private final SharedPref sp;

    public ConsentMgr(Context context) {
        this.sp = new SharedPref(context);
    }

    private final String User_Consent_Status = "User_Consent_Status";

    public boolean isConsentGiven() {
        return sp.getBoolean(User_Consent_Status);
        //return true;
    }

    public void setConsentGiven() {
        sp.add(User_Consent_Status, true);
    }

}
