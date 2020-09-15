package com.adhiwie.moodjournal.questionnaire.posttest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.NotificationMgr;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Time;

import java.util.Calendar;

public class SRBAIMgr {

    private final Context context;
    private final SharedPref sp;

    public SRBAIMgr(Context context) {
        this.context = context;
        this.sp = new SharedPref(context);
    }

    private final String SRBAI_TEST_STATUS = "SRBAI_TEST_STATUS";

    public void completeSRBAI() {
        sp.add(SRBAI_TEST_STATUS, true);
    }

    public boolean isSRBAIDone() {
        return sp.getBoolean(SRBAI_TEST_STATUS);
    }


    private final String SRBAI_RESPONSE_KEY(int q_num) {
        return "SRBAI_RESPONSE_DATA_" + q_num;
    }

    public void storeSRBAIResponse(int q_num, String response) {
        sp.add(SRBAI_RESPONSE_KEY(q_num), response);
    }

    public String getSRBAIResponse(int q_num) {
        return sp.getString(SRBAI_RESPONSE_KEY(q_num));
    }

    public void notifyUserIfRequired() {

        int participation_days = new UserData(context).getParticipationDays();

        //DEBUG
//		new Log().e("SRBAI questionnaire is triggered");
//		new Log().e("Count for today: "+sp.getInt(SRBAI_Notification_Trigger_Count_For_Today));
//		new Log().e("Participation days: "+participation_days);
//		new Log().e("Last date: "+sp.getInt(SRBAI_Notification_Trigger_Date_For_Today));

        if (getNotificationTriggerCountForToday() > 0 && isLastNotificationTriggeredToday())
            return;

        if (participation_days == 7 || participation_days == 14 || participation_days == 21 || participation_days == 28) {
            Intent i = new Intent(context, SRBAIActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            PendingIntent pi = PendingIntent.getActivity(context, 901, i, PendingIntent.FLAG_CANCEL_CURRENT);
            new NotificationMgr().triggerPriorityNotification(context, pi, 9011, "Mood Journal", "Your response needed");

            updateLastNotificationTriggerCountForToday();
        } else {
            int current_date = new Time(Calendar.getInstance()).getEpochDays();
            int last_date = sp.getInt(SRBAI_Notification_Trigger_Date_For_Today);

            if (current_date != last_date)
                resetLastNotificationTriggerCountForToday();
        }

    }

    private final String SRBAI_Notification_Trigger_Date_For_Today = "SRBAI_Notification_Trigger_Date_For_Today";
    private final String SRBAI_Notification_Trigger_Count_For_Today = "SRBAI_Notification_Trigger_Count_For_Today";

    private void updateLastNotificationTriggerCountForToday() {
        int current_date = new Time(Calendar.getInstance()).getEpochDays();
        int last_date = sp.getInt(SRBAI_Notification_Trigger_Date_For_Today);

        if (last_date == current_date) {
            int count = sp.getInt(SRBAI_Notification_Trigger_Count_For_Today);
            sp.add(SRBAI_Notification_Trigger_Count_For_Today, count + 1);
        } else {
            sp.add(SRBAI_Notification_Trigger_Date_For_Today, current_date);
            sp.add(SRBAI_Notification_Trigger_Count_For_Today, 1);
        }
    }

    private void resetLastNotificationTriggerCountForToday() {
        sp.add(SRBAI_Notification_Trigger_Count_For_Today, 0);
    }

    private int getNotificationTriggerCountForToday() {
        return sp.getInt(SRBAI_Notification_Trigger_Count_For_Today);
    }

    private boolean isLastNotificationTriggeredToday() {
        int current_date = new Time(Calendar.getInstance()).getEpochDays();
        int last_date = sp.getInt(SRBAI_Notification_Trigger_Date_For_Today);

        return (current_date == last_date);
    }

    private final String SRBAI_RESULT_TRANSMITTED = "SRBAI_RESULT_TRANSMITTED";

    public void resetData() {
        sp.add(SRBAI_TEST_STATUS, false);
        sp.add(SRBAI_RESULT_TRANSMITTED, false);
    }
}
