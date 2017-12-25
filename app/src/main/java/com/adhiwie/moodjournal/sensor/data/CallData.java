package com.adhiwie.moodjournal.sensor.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.moodjournal.file.DataInterface;
import com.adhiwie.moodjournal.utils.DataTypes;

public class CallData  implements DataInterface{

	private String number;
	private long duration;
	private long time;
	private String type;


	public CallData(String number, long duration, long time, String type) 
	{
		this.number = number;
		this.duration = duration;
		this.time = time;
		this.type = type;
	}


	public String toJSONString() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("number", number);
		json.put("duration", duration);
		json.put("time", time);
		json.put("type", type);
		return json.toString();
	}

	
	public String getDataType()
	{
		return new DataTypes().CALL;
	}
	
	
	public static CallData jsonStringToObject(String string) throws JSONException
	{
		if(string == null)
			return null;
		JSONObject json = new JSONObject(string);

		CallData c_data = new CallData(
								json.getString("number"), 
								json.getLong("duration"), 
								json.getLong("time"), 
								json.getString("type")
								);
		return c_data;
	}


	
	
	
	public String getNumber() {
		return number;
	}


	public long getDuration() {
		return duration;
	}


	public long getTime() {
		return time;
	}


	public String getType() {
		return type;
	}





}

