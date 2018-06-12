package com.adhiwie.moodjournal.questionnaire.pretest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.adhiwie.moodjournal.questionnaire.personality.PersonalityTestActivity;
import com.adhiwie.moodjournal.utils.NotificationMgr;
import com.adhiwie.moodjournal.utils.SharedPref;

public class GCSMgr {

	private final Context context;
	private final SharedPref sp;

	public GCSMgr(Context context)
	{
		this.context = context;
		this.sp = new SharedPref(context);
	}

	public void notifyUserIfRequired()
	{
		if(getGCSStatus())
			return;

		Intent i = new Intent(context, GCSActivity.class);
		PendingIntent pi = PendingIntent.getActivity(context, 901, i, PendingIntent.FLAG_UPDATE_CURRENT);
		new NotificationMgr().triggerPriorityNotification(context, pi, 9011, "Goal Commitment Test", "Your response needed!");
		//setPersonalityTestNotification(count+1);
	}


	private final String GOAL_COMMITMENT_SCALE_STATUS = "GOAL_COMMITMENT_SCALE_STATUS";
	public void gcsCompleted()
	{
		sp.add(GOAL_COMMITMENT_SCALE_STATUS, true);
	}

	public boolean getGCSStatus()
	{
		return sp.getBoolean(GOAL_COMMITMENT_SCALE_STATUS);
	}


	private final String GCS_RESPONSE_KEY(int q_num)
	{
		return "GCS_RESPONSE_DATA_" + q_num;
	}

	public void storeGCSResponse(int q_num, String response)
	{
		sp.add(GCS_RESPONSE_KEY(q_num), response);
	}
	
	public String getGCSResponse(int q_num)
	{
		return sp.getString(GCS_RESPONSE_KEY(q_num));
	}
}
