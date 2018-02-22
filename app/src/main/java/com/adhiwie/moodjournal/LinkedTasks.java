package com.adhiwie.moodjournal;

import android.content.Context;

import com.adhiwie.moodjournal.communication.helper.CommunicationMgr;
import com.adhiwie.moodjournal.exception.ConsetMissingException;
import com.adhiwie.moodjournal.questionnaire.mood.MoodQuestionnaireMgr;
import com.adhiwie.moodjournal.questionnaire.wellbeing.WellBeingQuestionnaireMgr;
import com.adhiwie.moodjournal.sensor.manager.SensorSubscriptionManager;
import com.adhiwie.moodjournal.user.permission.Permission;

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
		//new MoodQuestionnaireMgr(context).notifyUserIfRequired();

		// check for mood questionnaire
		// new WellBeingQuestionnaireMgr(context).notifyUserIfRequired();

		// check for data transmission
		new CommunicationMgr(context).transmissionDataIfRequired();

		//check for sensor sampling 
		SensorSubscriptionManager ss = new SensorSubscriptionManager(context);
		ss.startActivitySensingIfNotWorking();
		ss.startLocationSensingIfWorking();

		//check for permissions
		Permission p = new Permission(context);
		//p.notifyUserIfAccessibilityPermissionRevoked();
		p.notifyUserIfAppUsagePermissionRevoked();
		p.notifyUserIfNSLPermissionRevoked();
	}
	
	public void checkAllExceptPermission()
	{
		// check for mood questionnaire
		//new MoodQuestionnaireMgr(context).notifyUserIfRequired();

		// check for mood questionnaire
		// new WellBeingQuestionnaireMgr(context).notifyUserIfRequired();

		// check for data transmission
		new CommunicationMgr(context).transmissionDataIfRequired();

		//check for sensor sampling 
		SensorSubscriptionManager ss = new SensorSubscriptionManager(context);
		ss.startActivitySensingIfNotWorking();
		ss.startLocationSensingIfWorking();
	}




	public void checkQuestionnaires()
	{
		// check for mood questionnaire
		//new MoodQuestionnaireMgr(context).notifyUserIfRequired();

		// check for mood questionnaire
		// new WellBeingQuestionnaireMgr(context).notifyUserIfRequired();
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
