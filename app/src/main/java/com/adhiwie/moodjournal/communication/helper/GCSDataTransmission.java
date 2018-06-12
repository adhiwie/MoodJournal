package com.adhiwie.moodjournal.communication.helper;

import android.content.Context;

import com.adhiwie.moodjournal.communication.DataTransmitter;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.questionnaire.personality.PersonalityTestMgr;
import com.adhiwie.moodjournal.questionnaire.pretest.GCSMgr;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.DataTypes;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GCSDataTransmission {

	private final String GCS_RESULT = "GCS_RESULT";
	private final String GCS_RESULT_TRANSMITTED = "GCS_RESULT_TRANSMITTED";
	private final String GCS_RESULT_AVAILABLE = "GCS_RESULT_AVAILABLE";
	private final Context context;
	private final SharedPref sp;

	public GCSDataTransmission(Context context)
	{
		this.context = context;
		this.sp = new SharedPref(context);
		
		if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) ) 
		{
			Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(context) );
		}
	}

	public boolean isDataAvailable()
	{
		return sp.getBoolean(GCS_RESULT_AVAILABLE);
	}

	public boolean isDataTransmitted()
	{
		return sp.getBoolean(GCS_RESULT_TRANSMITTED);
	}

	public void transmitData()  
	{
		sp.add(GCS_RESULT_AVAILABLE, true);
		JSONArray data = new JSONArray();
		try
		{
			JSONObject uuid = new JSONObject();
			uuid.put("uuid", new UserData(context).getUuid());
			data.put(uuid);

			int total_scores = sp.getInt(GCS_RESULT);
			JSONObject jo_total = new JSONObject();
			jo_total.put("goal_commitment_score", total_scores);
			data.put(jo_total);

			GCSMgr gm = new GCSMgr(context);
			for(int i = 1; i <= 5; i++)
			{
				try
				{
					String s = gm.getGCSResponse(i);
					JSONObject jo = new JSONObject(s);
					data.put(jo);
				}
				catch(JSONException e)
				{
					new Log().e(e.toString());
				}
			}

			new DataTransmitter(this.context, new DataTypes().GOAL_COMMITMENT_SCALE, data.toString())
			{
				@Override
				protected void onPostExecute(Boolean result) 
				{
					new Log().e("GCS data transmission result: " + result);
					if( result )
						sp.add(GCS_RESULT_TRANSMITTED, true);
				};
			}.execute();

		}
		catch(Exception e)
		{
			sp.add(GCS_RESULT_TRANSMITTED, false);
		}
	}


	
	
}
