package com.adhiwie.moodjournal.sensor.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.moodjournal.file.DataInterface;
import com.adhiwie.moodjournal.utils.DataTypes;

public class ErrorLogData implements DataInterface {

    private final String error_log;

    public ErrorLogData(String error_log) {
        this.error_log = error_log;
    }


    @Override
    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("log", error_log);
        return json.toString();
    }


    public String getDataType() {
        return new DataTypes().ERROR_LOG;
    }

}
