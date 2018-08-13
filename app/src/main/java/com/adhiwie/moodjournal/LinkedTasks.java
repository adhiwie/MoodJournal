package com.adhiwie.moodjournal;

import android.content.Context;
import android.content.Intent;

import com.adhiwie.moodjournal.communication.helper.CommunicationMgr;
import com.adhiwie.moodjournal.exception.ConsetMissingException;
import com.adhiwie.moodjournal.plan.PlanActivity;
import com.adhiwie.moodjournal.questionnaire.mood.MoodQuestionnaireMgr;
import com.adhiwie.moodjournal.questionnaire.posttest.SRBAIActivity;
import com.adhiwie.moodjournal.questionnaire.posttest.SRBAIMgr;
import com.adhiwie.moodjournal.questionnaire.pretest.GCSActivity;
import com.adhiwie.moodjournal.questionnaire.pretest.GCSMgr;
import com.adhiwie.moodjournal.questionnaire.wellbeing.WellBeingQuestionnaireMgr;
import com.adhiwie.moodjournal.sensor.manager.SensorSubscriptionManager;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.user.permission.Permission;
import com.adhiwie.moodjournal.utils.Time;

import java.io.IOException;
import java.util.Calendar;

public class LinkedTasks {

	private final Context context;

	public LinkedTasks(Context context) throws ConsetMissingException
	{
		if(!new ConsentMgr(context).isConsentGiven())
			throw new ConsetMissingException();
		this.context = context;
	}


	public void checkAll()
	{
		// check for mood questionnaire
		new MoodQuestionnaireMgr(context).notifyUserIfRequired();

		// check for mood questionnaire
		// new WellBeingQuestionnaireMgr(context).notifyUserIfRequired();

		// check for data transmission
		new CommunicationMgr(context).transmissionDataIfRequired();

		//check for sensor sampling 
		SensorSubscriptionManager ss = new SensorSubscriptionManager(context);
		ss.startActivitySensingIfNotWorking();
		//ss.startLocationSensingIfWorking();

		//check for permissions
		Permission p = new Permission(context);
		//p.notifyUserIfAccessibilityPermissionRevoked();
		//p.notifyUserIfAppUsagePermissionRevoked();
		p.notifyUserIfNSLPermissionRevoked();

		// check for SRBAI questionnaire
		new SRBAIMgr(context).notifyUserIfRequired();

		checkUserGroup();
	}
	
	public void checkAllExceptPermission()
	{
		// check for mood questionnaire
		new MoodQuestionnaireMgr(context).notifyUserIfRequired();

		// check for mood questionnaire
		// new WellBeingQuestionnaireMgr(context).notifyUserIfRequired();

		// check for data transmission
		new CommunicationMgr(context).transmissionDataIfRequired();

		//check for sensor sampling 
		SensorSubscriptionManager ss = new SensorSubscriptionManager(context);
		ss.startActivitySensingIfNotWorking();
		//ss.startLocationSensingIfWorking();

		// check for SRBAI questionnaire
		new SRBAIMgr(context).notifyUserIfRequired();

		checkUserGroup();
	}




	public void checkQuestionnaires()
	{
		// check for mood questionnaire
		new MoodQuestionnaireMgr(context).notifyUserIfRequired();

		// check for mood questionnaire
		// new WellBeingQuestionnaireMgr(context).notifyUserIfRequired();

		// check for SRBAI questionnaire
		new SRBAIMgr(context).notifyUserIfRequired();

		try {
			new UserData(context).updateGroupId(new UserData(context).getUuid());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkUserGroup() {
		int start_date = new UserData(context).getStartDate();
		int current_date = new Time(Calendar.getInstance()).getEpochDays();
		int participation_days = 1 + current_date - start_date;

		if (participation_days < 7){
			try {
				new UserData(context).updateGroupId(new UserData(context).getUuid());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

//	public void checkPermission()
//	{
//		//check for permissions
//		Permission p = new Permission(context);
//		p.notifyUserIfAccessibilityPermissionRevoked();
//		p.notifyUserIfAppUsagePermissionRevoked();
//		p.notifyUserIfNSLPermissionRevoked();
//	}
	
}
