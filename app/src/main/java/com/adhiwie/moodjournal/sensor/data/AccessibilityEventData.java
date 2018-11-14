package com.adhiwie.moodjournal.sensor.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.moodjournal.file.DataInterface;
import com.adhiwie.moodjournal.utils.DataTypes;

public class AccessibilityEventData implements DataInterface {

    private final int event_code;
    private final String package_name;
    private final long time;

    public AccessibilityEventData(int event_code, String package_name, long time) {
        this.event_code = event_code;
        this.package_name = package_name;
        this.time = time;
    }


    @Override
    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("event_code", event_code);
        json.put("package_name", package_name);
        json.put("time", time);
        return json.toString();
    }


    @Override
    public String getDataType() {
        return new DataTypes().ACCESSIBILITY_EVENT;
    }


}
