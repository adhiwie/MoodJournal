package com.adhiwie.moodjournal.file;

import org.json.JSONException;

public interface DataInterface 
{

	public String toJSONString() throws JSONException;

	public String getDataType();
}
