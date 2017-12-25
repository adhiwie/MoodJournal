package com.adhiwie.moodjournal.communication.helper;

import java.util.Calendar;

import android.content.Context;

import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Time;


public class CommunicationMgr {

	private final String CM_LAST_DATA_TRANSMISSION_DATE = "CM_LAST_DATA_TRANSMISSION_DATE";
	private final Context context;
	private final SharedPref sp;

	public CommunicationMgr(Context context) 
	{
		this.context = context;
		this.sp = new SharedPref(context);
		
		if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) ) 
		{
			Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(context) );
		}
	}


	public void transmissionDataIfRequired()
	{
		new Log().e("Transmit data if required");
		int current_date = new Time(Calendar.getInstance()).getEpochDays();
		int last_date = getLastDataTransmissionDate();
		new Log().e("current date : "+Integer.toString(current_date));
		new Log().e("last date : "+Integer.toString(last_date));
		if((current_date - last_date) > 0)
		{
			new Log().e("Transmitting data.");
			new DataTransmitterMgr(context).transmitAllData();
			updateLastDataTransmissionDate();
		}
	}


	private int getLastDataTransmissionDate()
	{
		return sp.getInt(CM_LAST_DATA_TRANSMISSION_DATE);
	}

	private void updateLastDataTransmissionDate()
	{
		sp.add(CM_LAST_DATA_TRANSMISSION_DATE, new Time(Calendar.getInstance()).getEpochDays());
	}

}
