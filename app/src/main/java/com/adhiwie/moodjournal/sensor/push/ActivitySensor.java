package com.adhiwie.moodjournal.sensor.push;

import android.app.IntentService;
import android.content.Intent;

import com.adhiwie.moodjournal.LinkedTasks;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.file.FileMgr;
import com.adhiwie.moodjournal.sensor.data.ActivityData;
import com.adhiwie.moodjournal.sensor.manager.ActivityManager;
import com.adhiwie.moodjournal.sensor.manager.AdaptiveSensingManager;
import com.adhiwie.moodjournal.utils.Log;




public class ActivitySensor extends IntentService
{

	public ActivitySensor()
	{
		super("ActivityRecognitionService");
	}


	/**
	 * Called when a new activity detection update is available.
	 */
	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log log = new Log();
		try 
		{
			log.v("New Activity");
			new LinkedTasks(getApplicationContext()).checkQuestionnaires();

			if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) ) 
				Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(getApplicationContext()) );

			if (ActivityRecognitionResult.hasResult(intent)) 
			{        
				ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent); 
				DetectedActivity detected_activity = result.getMostProbableActivity();
				String activity = new ActivityType().getNameFromType(detected_activity.getType());
				int confidence = detected_activity.getConfidence();
				long time = result.getTime();
				ActivityData ad = new ActivityData(activity, confidence, time);

				ActivityManager am = new ActivityManager(getApplicationContext());
				am.setCurrentActivity(ad);

				FileMgr fm = new FileMgr(getApplicationContext());
				fm.addData(ad);

				AdaptiveSensingManager as = new AdaptiveSensingManager(getApplicationContext());
				as.onNewActivity(ad);

				log.d("Activity Result: " + ad.toJSONString());
			}
		} 
		catch (Exception e) 
		{
			log.e(e.toString());
		}
	}





}
