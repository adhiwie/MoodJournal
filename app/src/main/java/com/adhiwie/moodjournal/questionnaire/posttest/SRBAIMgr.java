package com.adhiwie.moodjournal.questionnaire.posttest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.adhiwie.moodjournal.file.FileMgr;
import com.adhiwie.moodjournal.plan.PlanMgr;
import com.adhiwie.moodjournal.questionnaire.mood.MoodQuestionnaireActivity;
import com.adhiwie.moodjournal.questionnaire.mood.ReminderData;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.NotificationMgr;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Time;

import java.util.Calendar;

public class SRBAIMgr {

	private final Context context;
	private final SharedPref sp;

	public SRBAIMgr(Context context)
	{
		this.context = context;
		this.sp = new SharedPref(context);
	}

	private final String SRBAI_TEST_STATUS = "SRBAI_TEST_STATUS";
	public void srbaiCompleted()
	{
		sp.add(SRBAI_TEST_STATUS, true);
	}

	public boolean getSRBAIStatus()
	{
		return sp.getBoolean(SRBAI_TEST_STATUS);
	}


	private final String SRBAI_RESPONSE_KEY(int q_num)
	{
		return "SRBAI_RESPONSE_DATA_" + q_num;
	}

	public void storeSRBAIResponse(int q_num, String response)
	{
		sp.add(SRBAI_RESPONSE_KEY(q_num), response);
	}
	
	public String getSRBAIResponse(int q_num)
	{
		return sp.getString(SRBAI_RESPONSE_KEY(q_num));
	}

	public void notifyUserIfRequired()
	{
		if(getSRBAIStatus())
			return;

		int start_date = new UserData(context).getStartDate();
		int current_date = new Time(Calendar.getInstance()).getEpochDays();
		int participation_days = 1 + current_date - start_date;

		if(!new SRBAIMgr(context).getSRBAIStatus() && participation_days == 28) {
			Intent i = new Intent(context, SRBAIActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

			PendingIntent pi = PendingIntent.getActivity(context, 901, i, PendingIntent.FLAG_CANCEL_CURRENT);
			new NotificationMgr().triggerPriorityNotification(context, pi, 9011, "SRBAI Test", "Your response needed");
		}

	}
}
