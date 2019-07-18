package com.adhiwie.diary.communication;

import org.json.JSONException;
import org.json.JSONObject;

public class DataFormatter {

    public String formatData(String data_type, String data) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("data_type", data_type);
        json.put("data", data);
        return json.toString();
    }
}
