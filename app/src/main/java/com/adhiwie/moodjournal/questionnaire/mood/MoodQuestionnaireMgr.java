package com.adhiwie.moodjournal.questionnaire.mood;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.adhiwie.moodjournal.file.FileMgr;
import com.adhiwie.moodjournal.plan.PlanMgr;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.NotificationMgr;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Time;

import org.json.JSONArray;
import org.json.JSONException;

public class MoodQuestionnaireMgr {

    private final String DAILY_MOOD_REPORT_DATA = "DAILY_MOOD_REPORT_DATA";

    private final Context context;
    private final SharedPref sp;
    private int hour;
    private long currentTimeInMillis;
    private long lastTriggerTimeInMillis;

    public MoodQuestionnaireMgr(Context context) {
        this.context = context;
        this.sp = new SharedPref(context);
    }

    public void notifyUserIfRequired() {
        hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        lastTriggerTimeInMillis = getLastMoodQuestionnaireTriggerTime();

//		new Log().e("=======================");
//		new Log().e("Mood notification is triggered");
//		new Log().e("Current time - last trigger time: "+lastTriggerTimeInMillis);
//		new Log().e("Mood Questionnaire count for today: "+getMoodQuestionnaireCountForToday());
//		new Log().e("Mood Notification trigger count for today: "+getMoodNotificationTriggerCountForToday());
//		new Log().e("Hour of day: "+hour);
//		new Log().e("=======================");

        if (isRuleOk()) {

            new Log().e("Notification is sent");

            Intent i = new Intent(context, ReminderActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            context.startActivity(i);

            String routine_desc = new PlanMgr(context).getPlanRoutineDesc();
            String message = "Remember: if I " + routine_desc + ", then I will track my mood!";
//
//            PendingIntent pi = PendingIntent.getActivity(context, 601, i, PendingIntent.FLAG_CANCEL_CURRENT);
//            message = "Remember: if I " + routine_desc + ", then I will track my mood!";
//
//            new NotificationMgr().triggerPriorityNotification(context, pi, 6011, "Mood Journal", message);

            updateLastMoodQuestionnaireTriggerTime();

		    /* Log reminder and send the data to server */
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            String currentTimeInString = simpleDateFormat.format(currentTime);
            ReminderData data = new ReminderData(currentTimeInMillis, currentTimeInString, message);
            FileMgr fm = new FileMgr(context);
            fm.addData(data);
        } else {
            new Log().e("Mood notification trigger is resetted for today");

            int current_date = new Time(Calendar.getInstance()).getEpochDays();
            int last_date = sp.getInt(Mood_Notification_Trigger_Date_For_Today);

            if (current_date != last_date)
                resetMoodNotificationTriggerCountForToday();
        }

    }


    private final String Mood_Notification_Time = "Mood_Notification_Time";

    public void updateLastMoodQuestionnaireTime() {
        sp.add(Mood_Notification_Time, Calendar.getInstance().getTimeInMillis());
        updateMoodNotificationCount();
        updateMoodNotificationCountForToday();
    }

    public long getLastMoodQuestionnaireTime() {
        return sp.getLong(Mood_Notification_Time);
    }


    private final String Mood_Notification_Trigger_Time = "Mood_Notification_Trigger_Time";
    private final String Mood_Notification_Trigger_Date_For_Today = "Mood_Notification_Trigger_Date_For_Today";
    private final String Mood_Notification_Trigger_Count_For_Today = "Mood_Notification_Trigger_Count_For_Today";

    private void updateLastMoodQuestionnaireTriggerTime() {
        sp.add(Mood_Notification_Trigger_Time, Calendar.getInstance().getTimeInMillis());

        int current_date = new Time(Calendar.getInstance()).getEpochDays();
        int last_date = sp.getInt(Mood_Notification_Trigger_Date_For_Today);

        if (last_date == current_date) {
            int count = sp.getInt(Mood_Notification_Trigger_Count_For_Today);
            sp.add(Mood_Notification_Trigger_Count_For_Today, count + 1);
        } else {
            sp.add(Mood_Notification_Trigger_Date_For_Today, current_date);
            sp.add(Mood_Notification_Trigger_Count_For_Today, 1);
        }
    }

    private void resetMoodNotificationTriggerCountForToday() {
        sp.add(Mood_Notification_Trigger_Count_For_Today, 0);
    }

    private int getMoodNotificationTriggerCountForToday() {
        return sp.getInt(Mood_Notification_Trigger_Count_For_Today);
    }

    private long getLastMoodQuestionnaireTriggerTime() {
        return sp.getLong(Mood_Notification_Trigger_Time);
    }


    private final String Mood_Notification_Count = "Mood_Notification_Count";

    private void updateMoodNotificationCount() {
        if (getMoodQuestionnaireCount() == 0)
            sp.add(Mood_Notification_Count, 0);
        sp.add(Mood_Notification_Count, getMoodQuestionnaireCount() + 1);
    }

    public int getMoodQuestionnaireCount() {
        return sp.getInt(Mood_Notification_Count);
    }

    private final String Mood_Notification_Count_For_Today = "Mood_Notification_Count_For_Today";
    private final String Mood_Notification_Date_For_Today = "Mood_Notification_Date_For_Today";

    private void updateMoodNotificationCountForToday() {
        int current_date = new Time(Calendar.getInstance()).getEpochDays();
        int last_date = sp.getInt(Mood_Notification_Date_For_Today);

        if (last_date == current_date) {
            int count = sp.getInt(Mood_Notification_Count_For_Today);
            sp.add(Mood_Notification_Count_For_Today, count + 1);
        } else {
            sp.add(Mood_Notification_Date_For_Today, current_date);
            sp.add(Mood_Notification_Count_For_Today, 1);
        }
    }

    public int getMoodQuestionnaireCountForToday() {
        int current_date = new Time(Calendar.getInstance()).getEpochDays();
        int last_date = sp.getInt(Mood_Notification_Date_For_Today);
        if (last_date == current_date)
            return sp.getInt(Mood_Notification_Count_For_Today);
        else
            return 0;
    }

    public void saveDailyMoodReportData(String data) throws JSONException {
        String moodData = sp.getString(DAILY_MOOD_REPORT_DATA);

        JSONArray jsonArray;

        if (moodData == null) {
            jsonArray = new JSONArray();
        } else {
            jsonArray = new JSONArray(moodData);
        }

        jsonArray.put(data);
        sp.add(DAILY_MOOD_REPORT_DATA, jsonArray.toString());
    }

    private boolean isRuleOk() {
        new Log().e("getMoodQuestionnaireCountForToday : "+getMoodQuestionnaireCountForToday());
        new Log().e("getMoodNotificationTriggerCountForToday : "+getMoodNotificationTriggerCountForToday());
        new Log().e("hour : "+hour);
        new Log().e("group ID : "+new UserData(context).getGroupId());
        return ((getMoodQuestionnaireCountForToday() == 0) &&
                (getMoodNotificationTriggerCountForToday() == 0) &&
                (hour >= 12 && hour <= 14) &&
                (new UserData(context).getGroupId() == 1));
    }


}
