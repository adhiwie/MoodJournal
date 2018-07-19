package com.adhiwie.moodjournal.questionnaire.mood;

import java.util.Calendar;
import java.util.Map;

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

public class MoodQuestionnaireMgr {

	private final String MOOD_REPORT_DATA = "mood_report_data";

	private final Context context;
	private final SharedPref sp;
	
	public MoodQuestionnaireMgr(Context context) 
	{
		this.context = context;
		this.sp = new SharedPref(context);
	}
	
	public void notifyUserIfRequired()
	{
		new Log().e(String.valueOf("REMINDER IS OPENED FOR TODAY? " +checkIfReminderIsOpenedForToday()));
		if(checkIfReminderIsOpenedForToday())
			return;

		if(getMoodQuestionnaireCountForToday() > 0)
			return;

		// no notification if last notification was triggered within 30mins
		long current_time = Calendar.getInstance().getTimeInMillis();
		long last_trigger_time = getLastMoodQuestionnaireTriggerTime();
		if(current_time - last_trigger_time < 1000)
			return;

		/*
		long last_time = getLastMoodQuestionnaireTime();
		if(current_time - last_time < 3*60*60*1000)
			return;
		*/

		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if(hour < 10 || hour > 22)
			return;

//		PlanMgr planMgr = new PlanMgr(context);
//		int trigger_time = planMgr.getPlanHour();
//		current_time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//
//		new Log().e("trigger_time: "+trigger_time);
//		new Log().e("current_time: "+current_time);

//		if (trigger_time - current_time == 1) {
			Intent i = new Intent(context, ReminderActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

			String routine_desc = new PlanMgr(context).getPlanRoutineDesc();
			String message;

			PendingIntent pi = PendingIntent.getActivity(context, 601, i, PendingIntent.FLAG_CANCEL_CURRENT);
			message = "Remember: if I "+routine_desc+", then I will track my mood!";

			new NotificationMgr().triggerPriorityNotification(context, pi, 6011, "Mood Journal", message);

			updateLastMoodQuestionnaireTriggerTime();

			/* Log reminder and send the data to server */

//			current_time = Calendar.getInstance().getTimeInMillis();
			ReminderData data = new ReminderData(new UserData(context).getUuid(), current_time, message);
			FileMgr fm = new FileMgr(context);
			fm.addData(data);
//		}

	}
	
	
	private final String Mood_Notification_Time = "Mood_Notification_Time";
	public void updateLastMoodQuestionnaireTime()
	{
		sp.add(Mood_Notification_Time, Calendar.getInstance().getTimeInMillis());
		updateMoodNotificationCount();
		updateMoodNotificationCountForToday();
	}
	
	public long getLastMoodQuestionnaireTime()
	{
		return sp.getLong(Mood_Notification_Time);
	}
	
	
	private final String Mood_Notification_Trigger_Time = "Mood_Notification_Trigger_Time";
	private void updateLastMoodQuestionnaireTriggerTime()
	{
		sp.add(Mood_Notification_Trigger_Time, Calendar.getInstance().getTimeInMillis());
	}
	
	private long getLastMoodQuestionnaireTriggerTime()
	{
		return sp.getLong(Mood_Notification_Trigger_Time);
	}
	
	
	
	private final String Mood_Notification_Count = "Mood_Notification_Count";
	private void updateMoodNotificationCount()
	{
		if(getMoodQuestionnaireCount() == 0)
			sp.add(Mood_Notification_Count, 0);
		sp.add(Mood_Notification_Count, getMoodQuestionnaireCount()+1);
	}
	
	public int getMoodQuestionnaireCount()
	{
		return sp.getInt(Mood_Notification_Count);
	}

	private final String Mood_Notification_Count_For_Today = "Mood_Notification_Count_For_Today";
	private final String Mood_Notification_Date_For_Today = "Mood_Notification_Date_For_Today";
	private void updateMoodNotificationCountForToday()
	{
		int current_date = new Time(Calendar.getInstance()).getEpochDays();
		int last_date = sp.getInt(Mood_Notification_Date_For_Today);

		if(last_date == current_date)
		{
			int count = sp.getInt(Mood_Notification_Count_For_Today);
			sp.add(Mood_Notification_Count_For_Today, count+1);
		}
		else
		{
			sp.add(Mood_Notification_Date_For_Today, current_date);
			sp.add(Mood_Notification_Count_For_Today, 1);
		}
	}

	public int getMoodQuestionnaireCountForToday()
	{
		int current_date = new Time(Calendar.getInstance()).getEpochDays();
		int last_date = sp.getInt(Mood_Notification_Date_For_Today);
		if(last_date == current_date)
			return sp.getInt(Mood_Notification_Count_For_Today);
		else
			return 0;
	}

	public void saveDailyMoodReportData(String data) {
		sp.add(MOOD_REPORT_DATA, data);
		Map<String, ?> map = sp.getAll();
		new Log().d("Daily mood report");
		new Log().d(map.toString());
	}

	private final String REMINDER_OPENED_FOR_TODAY = "REMINDER_OPENED_FOR_TODAY";
	private final String REMINDER_DATE_FOR_TODAY = "REMINDER_DATE_FOR_TODAY";
	public boolean checkIfReminderIsOpenedForToday() {
		int current_date = new Time(Calendar.getInstance()).getEpochDays();
		int last_date = sp.getInt(REMINDER_DATE_FOR_TODAY);

//		new Log().e(String.valueOf("LAST DATE="+last_date));
//		new Log().e(String.valueOf("CURRENT DATE="+current_date));
//		new Log().e(String.valueOf(sp.getBoolean(REMINDER_OPENED_FOR_TODAY)));

		return sp.getBoolean(REMINDER_OPENED_FOR_TODAY);

//		if(last_date == current_date) {
//			return true;
//		} else {
//			return sp.getBoolean(REMINDER_OPENED_FOR_TODAY);
//		}
	}


}
