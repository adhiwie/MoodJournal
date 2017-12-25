package com.adhiwie.moodjournal.file;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.adhiwie.moodjournal.user.data.UserData;

public class DataFormatter {

	private final String uuid;
	private final String version = "v1";
	
	public DataFormatter(Context context) 
	{
		this.uuid = new UserData(context).getUuid();
	}

	public JSONObject createJSONObjectForDataEntry(String data_type, String data) throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("uuid", uuid);
		json.put("version", version);
		json.put("data_type", data_type);
		json.put("data", data);
		return json;
	}
}
