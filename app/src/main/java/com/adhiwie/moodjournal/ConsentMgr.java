package com.adhiwie.moodjournal;

import android.content.Context;

import com.adhiwie.moodjournal.utils.SharedPref;

public class ConsentMgr {

    private final SharedPref sp;

    protected ConsentMgr(Context context) {
        this.sp = new SharedPref(context);
    }

    private final String User_Consent_Status = "User_Consent_Status";

    protected boolean isConsentGiven() {
        return sp.getBoolean(User_Consent_Status);
        //return true;
    }

    protected void setConsentGiven() {
        sp.add(User_Consent_Status, true);
    }

}
