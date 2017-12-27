package com.adhiwie.moodjournal.questionnaire.mood;

import java.util.Calendar;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.adhiwie.moodjournal.utils.NotificationMgr;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Time;

public class MoodQuestionnaireMgr {

	private final Context context;
	private final SharedPref sp;
	
	public MoodQuestionnaireMgr(Context context) 
	{
		this.context = context;
		this.sp = new SharedPref(context);
	}
	
	public void notifyUserIfRequired()
	{
		if(getMoodQuestionnaireCountForToday() > 3)
			return;
			
		long current_time = Calendar.getInstance().getTimeInMillis();

		// no notification if last notification was triggered within 30mins
		long last_trigger_time = getLastMoodQuestionnaireTriggerTime();
		if(current_time - last_trigger_time < 30*60*1000)
			return;
		
		long last_time = getLastMoodQuestionnaireTime();
		if(current_time - last_time < 3*60*60*1000)
			return;
		
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if(hour < 9 || hour > 22)
			return;
		
		Intent i = new Intent(context, MoodQuestionnaireActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

		
		PendingIntent pi = PendingIntent.getActivity(context, 601, i, PendingIntent.FLAG_CANCEL_CURRENT);
		new NotificationMgr().triggerPriorityNotification(context, pi, 6011, "Mood Questionnaire", "Your response needed!");
		
		updateLastMoodQuestionnaireTriggerTime();
	}
	
	
	private final String Mood_Notification_Time = "Mood_Notification_Time";
	public void updateLastMoodQuestionnaireTime()
	{
		sp.add(Mood_Notification_Time, Calendar.getInstance().getTimeInMillis());
		updateMoodNotifcationCount();
		updateMoodNotifcationCountForToday();
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
	private void updateMoodNotifcationCount()
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
	private void updateMoodNotifcationCountForToday()
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
}
