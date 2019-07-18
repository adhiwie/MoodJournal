package com.adhiwie.diary.questionnaire.diary;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.adhiwie.diary.plan.PlanMgr;
import com.adhiwie.diary.user.data.UserData;
import com.adhiwie.diary.utils.Log;
import com.adhiwie.diary.utils.NotificationMgr;
import com.adhiwie.diary.utils.SharedPref;
import com.adhiwie.diary.utils.Time;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;

public class DiaryStudyQuestionnaireMgr {

    private final String DIARY_STUDY_DATA = "DIARY_STUDY_DATA";

    private final Context context;
    private final SharedPref sp;
    private int hour;
    private long currentTimeInMillis;
    private long lastTriggerTimeInMillis;

    public DiaryStudyQuestionnaireMgr(Context context) {
        this.context = context;
        this.sp = new SharedPref(context);
    }

    public void notifyUserIfRequired() {
        hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        lastTriggerTimeInMillis = getLastDiaryStudyQuestionnaireTriggerTime();

		new Log().e("=======================");
		new Log().e("Diary notification is triggered");
		new Log().e("Current time - last trigger time: "+lastTriggerTimeInMillis);
		new Log().e("Diary Questionnaire count for today: "+getDiaryStudyQuestionnaireCountForToday());
		new Log().e("Diary Notification trigger count for today: "+getDiaryStudyNotificationTriggerCountForToday());
		new Log().e("Hour of day: "+hour);
		new Log().e("=======================");

        if (hour < 12 || hour > 17) {
            resetDiaryStudyNotificationTriggerCountForToday();
            return;
        }


        if (getDiaryStudyQuestionnaireCountForToday() > 0)
            return;

        if (getDiaryStudyNotificationTriggerCountForToday() > 0)
            return;

        if (new UserData(context).getGroupId() == 1)
            return;

        if (!new PlanMgr(context).isPlanGiven())
            return;


//        else {
//            new Log().e("Mood notification trigger is resetted for today");
//
//            int current_date = new Time(Calendar.getInstance()).getEpochDays();
//            int last_date = sp.getInt(Diary_Study_Notification_Trigger_Date_For_Today);
//
//            if (current_date != last_date)
//                resetDiaryStudyNotificationTriggerCountForToday();
//        }

        Intent i = new Intent(context, ReminderActivity.class);
        String routine_desc = new PlanMgr(context).getPlanRoutineDesc();
        String message = "Remember: if I " + routine_desc + ", then I will finish self–report diary";

        if (new UserData(context).getGroupId() == 3) {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            context.startActivity(i);
        }

        if (new UserData(context).getGroupId() == 2) {
            PendingIntent pi = PendingIntent.getActivity(context, 601, i, PendingIntent.FLAG_CANCEL_CURRENT);
            new NotificationMgr().triggerPriorityNotification(context, pi, 6011, "Diary Study", message);
        }

        updateLastDiaryStudyQuestionnaireTriggerTime();

    }


    private final String Diary_Study_Notification_Time = "Diary_Study_Notification_Time";

    public void updateLastDiaryStudyQuestionnaireTime() {
        sp.add(Diary_Study_Notification_Time, Calendar.getInstance().getTimeInMillis());
        updateDiaryStudyNotificationCount();
        updateDiaryStudyNotificationCountForToday();
    }

    public long getLastDiaryStudyQuestionnaireTime() {
        return sp.getLong(Diary_Study_Notification_Time);
    }


    private final String Diary_Study_Notification_Trigger_Time = "Diary_Study_Notification_Trigger_Time";
    private final String Diary_Study_Notification_Trigger_Date_For_Today = "Diary_Study_Notification_Trigger_Date_For_Today";
    private final String Diary_Study_Notification_Trigger_Count_For_Today = "Diary_Study_Notification_Trigger_Count_For_Today";

    private void updateLastDiaryStudyQuestionnaireTriggerTime() {
        sp.add(Diary_Study_Notification_Trigger_Time, Calendar.getInstance().getTimeInMillis());

        int current_date = new Time(Calendar.getInstance()).getEpochDays();
        int last_date = sp.getInt(Diary_Study_Notification_Trigger_Date_For_Today);

        if (last_date == current_date) {
            int count = sp.getInt(Diary_Study_Notification_Trigger_Count_For_Today);
            sp.add(Diary_Study_Notification_Trigger_Count_For_Today, count + 1);
        } else {
            sp.add(Diary_Study_Notification_Trigger_Date_For_Today, current_date);
            sp.add(Diary_Study_Notification_Trigger_Count_For_Today, 1);
        }
    }

    private void resetDiaryStudyNotificationTriggerCountForToday() {
        sp.add(Diary_Study_Notification_Trigger_Count_For_Today, 0);
    }

    private int getDiaryStudyNotificationTriggerCountForToday() {
        return sp.getInt(Diary_Study_Notification_Trigger_Count_For_Today);
    }

    private long getLastDiaryStudyQuestionnaireTriggerTime() {
        return sp.getLong(Diary_Study_Notification_Trigger_Time);
    }


    private final String Diary_Study_Notification_Count = "Diary_Study_Notification_Count";

    private void updateDiaryStudyNotificationCount() {
        if (getDiaryStudyQuestionnaireCount() == 0)
            sp.add(Diary_Study_Notification_Count, 0);
        sp.add(Diary_Study_Notification_Count, getDiaryStudyQuestionnaireCount() + 1);
    }

    public int getDiaryStudyQuestionnaireCount() {
        return sp.getInt(Diary_Study_Notification_Count);
    }

    private final String Diary_Study_Notification_Count_For_Today = "Diary_Study_Notification_Count_For_Today";
    private final String Diary_Study_Notification_Date_For_Today = "Diary_Study_Notification_Date_For_Today";

    private void updateDiaryStudyNotificationCountForToday() {
        int current_date = new Time(Calendar.getInstance()).getEpochDays();
        int last_date = sp.getInt(Diary_Study_Notification_Date_For_Today);

        if (last_date == current_date) {
            int count = sp.getInt(Diary_Study_Notification_Count_For_Today);
            sp.add(Diary_Study_Notification_Count_For_Today, count + 1);
        } else {
            sp.add(Diary_Study_Notification_Date_For_Today, current_date);
            sp.add(Diary_Study_Notification_Count_For_Today, 1);
        }
    }

    public int getDiaryStudyQuestionnaireCountForToday() {
        int current_date = new Time(Calendar.getInstance()).getEpochDays();
        int last_date = sp.getInt(Diary_Study_Notification_Date_For_Today);
        if (last_date == current_date)
            return sp.getInt(Diary_Study_Notification_Count_For_Today);
        else
            return 0;
    }

    public void saveDailyDiaryStudyReportData(String data) throws JSONException {
        String moodData = sp.getString(DIARY_STUDY_DATA);

        JSONArray jsonArray;

        if (moodData == null) {
            jsonArray = new JSONArray();
        } else {
            jsonArray = new JSONArray(moodData);
        }

        jsonArray.put(data);
        sp.add(DIARY_STUDY_DATA, jsonArray.toString());
    }

    private boolean isRuleOk() {
//        new Log().e("getDiaryStudyQuestionnaireCountForToday : "+getDiaryStudyQuestionnaireCountForToday());
//        new Log().e("getDiaryStudyNotificationTriggerCountForToday : "+getDiaryStudyNotificationTriggerCountForToday());
//        new Log().e("hour : "+hour);
//        new Log().e("group ID : "+new UserData(context).getGroupId());
        return ((getDiaryStudyQuestionnaireCountForToday() == 0) &&
                (getDiaryStudyNotificationTriggerCountForToday() == 0) &&
                (hour >= 12 && hour <= 14) &&
                (new UserData(context).getGroupId() != 1) &&
                new PlanMgr(context).isPlanGiven());
    }
}
