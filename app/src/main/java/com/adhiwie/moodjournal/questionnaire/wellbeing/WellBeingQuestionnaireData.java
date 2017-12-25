package com.adhiwie.moodjournal.questionnaire.wellbeing;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.moodjournal.file.DataInterface;
import com.adhiwie.moodjournal.utils.DataTypes;

public class WellBeingQuestionnaireData implements DataInterface
{
	private final long start_time;
	private final long end_time;
	private final int q1; 
	private final int q2; 
	private final int q3; 
	private final int q4; 
	private final int q5; 
	private final int q6; 
	private final int q7; 
	private final int q8;
	
	public WellBeingQuestionnaireData(long start_time, long end_time, int q1, int q2, int q3, int q4, int q5, int q6, int q7, int q8) 
	{
		this.start_time = start_time;
		this.end_time = end_time;
		this.q1 = q1;
		this.q2 = q2;
		this.q3 = q3;
		this.q4 = q4;
		this.q5 = q5;
		this.q6 = q6;
		this.q7 = q7;
		this.q8 = q8;
	}
	

	public String toJSONString() throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("start_time", start_time);
		json.put("end_time", end_time);
		json.put("q1", q1); 
		json.put("q2", q2); 
		json.put("q3", q3);
		json.put("q4", q4);
		json.put("q5", q5);
		json.put("q6", q6);
		json.put("q7", q7);
		json.put("q8", q8);
		return json.toString();
	}


	@Override
	public String getDataType() 
	{
		return new DataTypes().DAILY_QUESTIONNAIRE;
	}

	
}
