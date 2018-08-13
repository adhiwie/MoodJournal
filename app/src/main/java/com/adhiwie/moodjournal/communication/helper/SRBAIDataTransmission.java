package com.adhiwie.moodjournal.communication.helper;

import android.content.Context;

import com.adhiwie.moodjournal.communication.DataTransmitter;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.questionnaire.posttest.SRBAIMgr;
import com.adhiwie.moodjournal.questionnaire.pretest.GCSMgr;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.DataTypes;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SRBAIDataTransmission {

	private final String SRBAI_RESULT = "SRBAI_RESULT";
	private final String SRBAI_RESULT_TRANSMITTED = "SRBAI_RESULT_TRANSMITTED";
	private final String SRBAI_RESULT_AVAILABLE = "SRBAI_RESULT_AVAILABLE";
	private final Context context;
	private final SharedPref sp;

	public SRBAIDataTransmission(Context context)
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
		return sp.getBoolean(SRBAI_RESULT_AVAILABLE);
	}

	public boolean isDataTransmitted()
	{
		return sp.getBoolean(SRBAI_RESULT_TRANSMITTED);
	}
	
	public void transmitData()  
	{
		sp.add(SRBAI_RESULT_AVAILABLE, true);
		JSONObject json = new JSONObject();
		try
		{
			int total_scores = sp.getInt(SRBAI_RESULT);

			JSONArray data = new JSONArray();

			final SRBAIMgr srbaiMgr = new SRBAIMgr(context);
			for(int i = 1; i <= 4; i++)
			{
				try
				{
					String s = srbaiMgr.getSRBAIResponse(i);
					JSONObject jo = new JSONObject(s);
					data.put(jo);
				}
				catch(JSONException e)
				{
					new Log().e(e.toString());
				}
			}

			json.put("uuid", new UserData(context).getUuid());
			json.put("srbai_score", total_scores);
			json.put("answers", data);

			new DataTransmitter(this.context, new DataTypes().SRBAI_SCORE, json.toString())
			{
				@Override
				protected void onPostExecute(Boolean result) 
				{
					new Log().e("SRBAI data transmission result: " + result);
					if( result )
						sp.add(SRBAI_RESULT_TRANSMITTED, true);
						srbaiMgr.resetData();
				};
			}.execute();

		}
		catch(Exception e)
		{
			sp.add(SRBAI_RESULT_TRANSMITTED, false);
		}
	}


	
	
}
