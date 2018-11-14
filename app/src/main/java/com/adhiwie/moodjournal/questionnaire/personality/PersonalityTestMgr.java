package com.adhiwie.moodjournal.questionnaire.personality;

import java.util.Calendar;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.NotificationMgr;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Time;

public class PersonalityTestMgr {

    private final Context context;
    private final SharedPref sp;

    public PersonalityTestMgr(Context context) {
        this.context = context;
        this.sp = new SharedPref(context);
    }

    public void notifyUserIfRequired() {
        if (getPersonalityTestStatus())
            return;

        int days_passed = new Time(Calendar.getInstance()).getEpochDays() - new UserData(context).getStartDate();
        int count = getPersonalityTestNotificationCount();

        if (days_passed / 7 < (count + 1))
            return;

        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10)
            return;

        Intent i = new Intent(context, PersonalityTestActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 901, i, PendingIntent.FLAG_UPDATE_CURRENT);
        new NotificationMgr().triggerPriorityNotification(context, pi, 9011, "Personality Test", "Your response needed!");
        setPersonalityTestNotification(count + 1);
    }


    private final String PERSONALITY_TEST_STATUS = "PERSONALITY_TEST_STATUS";

    public void personalityTestCompleted() {
        sp.add(PERSONALITY_TEST_STATUS, true);
    }

    public boolean getPersonalityTestStatus() {
        return sp.getBoolean(PERSONALITY_TEST_STATUS);
    }

    private final String PERSONALITY_TEST_NOTIFICATION_COUNT = "PERSONALITY_TEST_NOTIFICATION_COUNT";

    private void setPersonalityTestNotification(int count) {
        sp.add(PERSONALITY_TEST_NOTIFICATION_COUNT, count);
    }

    private int getPersonalityTestNotificationCount() {
        return sp.getInt(PERSONALITY_TEST_NOTIFICATION_COUNT);
    }


    private final String PERSONALITY_TEST_RESPONSE_KEY(int q_num) {
        return "PERSONALITY_TEST_RESPONSE_DATA_" + q_num;
    }

    public void storePersonalityTestResponse(int q_num, String response) {
        sp.add(PERSONALITY_TEST_RESPONSE_KEY(q_num), response);
    }

    public String getPersonalityTestResponse(int q_num) {
        return sp.getString(PERSONALITY_TEST_RESPONSE_KEY(q_num));
    }

}
