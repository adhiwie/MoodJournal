package com.adhiwie.moodjournal.questionnaire.wellbeing;

import java.util.Calendar;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.adhiwie.moodjournal.utils.NotificationMgr;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Time;

public class WellBeingQuestionnaireMgr {

	private final Context context;
	private final SharedPref sp;

	public WellBeingQuestionnaireMgr(Context context) 
	{
		this.context = context;
		this.sp = new SharedPref(context);
	}

	public void notifyUserIfRequired()
	{
		if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 16)
			return;

		// no notification if last notification was triggered within 30mins
		long current_time = Calendar.getInstance().getTimeInMillis();
		long last_trigger_time = getLastDailyQuestionnaireTriggerTime();
		if(current_time - last_trigger_time < 30*60*1000)
			return;

		int current_date = new Time(Calendar.getInstance()).getEpochDays();
		int last_date = getLastDailyQuestionnaireDate();

		if(current_date - last_date < 1)
			return;

		Intent i = new Intent(context, WellBeingQuestionnaireActivity.class);
		PendingIntent pi = PendingIntent.getActivity(context, 501, i, PendingIntent.FLAG_CANCEL_CURRENT);
		new NotificationMgr().triggerPriorityNotification(context, pi, 5011, "Well-being Questionnaire", "Your response needed!");

		updateLastDailyQuestionnaireTriggerTime();
	}

	private final String Daily_Notification_Date = "Daily_Notification_Date";
	public void updateLastDailyQuestionnaireDate()
	{
		sp.add(Daily_Notification_Date, new Time(Calendar.getInstance()).getEpochDays());
		updateDailyNotifcationCount();
		updateDailyNotifcationCountForToday();
	}

	private int getLastDailyQuestionnaireDate()
	{
		return sp.getInt(Daily_Notification_Date);
	}


	private final String Daily_Notification_Count = "Daily_Notification_Count";
	private void updateDailyNotifcationCount()
	{
		if(getDailyQuestionnaireCount() == 0)
			sp.add(Daily_Notification_Count, 0);
		sp.add(Daily_Notification_Count, getDailyQuestionnaireCount()+1);
	}

	public int getDailyQuestionnaireCount()
	{
		return sp.getInt(Daily_Notification_Count);
	}


	private final String Daily_Notification_Trigger_Time = "Daily_Notification_Trigger_Time";
	private void updateLastDailyQuestionnaireTriggerTime()
	{
		sp.add(Daily_Notification_Trigger_Time, Calendar.getInstance().getTimeInMillis());
	}

	private long getLastDailyQuestionnaireTriggerTime()
	{
		return sp.getLong(Daily_Notification_Trigger_Time);
	}


	private final String Daily_Notification_Count_For_Today = "Daily_Notification_Count_For_Today";
	private final String Daily_Notification_Date_For_Today = "Daily_Notification_Date_For_Today";
	private void updateDailyNotifcationCountForToday()
	{
		int current_date = new Time(Calendar.getInstance()).getEpochDays();
		int last_date = sp.getInt(Daily_Notification_Date_For_Today);

		if(last_date == current_date)
		{
			int count = sp.getInt(Daily_Notification_Count_For_Today);
			sp.add(Daily_Notification_Count_For_Today, count+1);
		}
		else
		{
			sp.add(Daily_Notification_Date_For_Today, current_date);
			sp.add(Daily_Notification_Count_For_Today, 1);
		}
	}

	public int getDailyQuestionnaireCountForToday()
	{
		int current_date = new Time(Calendar.getInstance()).getEpochDays();
		int last_date = sp.getInt(Daily_Notification_Date_For_Today);
		if(last_date == current_date)
			return sp.getInt(Daily_Notification_Count_For_Today);
		else
			return 0;
	}


}
