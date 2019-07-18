package com.adhiwie.mymoodjournal.sensor.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.mymoodjournal.file.DataInterface;
import com.adhiwie.mymoodjournal.utils.DataTypes;

public class PhoneUsageData implements DataInterface {

    private final long event_time;
    private final String event_type;
    private final String package_name;

    public PhoneUsageData(long event_time, String event_type, String package_name) {
        this.event_time = event_time;
        this.event_type = event_type;
        this.package_name = package_name;
    }


    public String toJSONString() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("event_time", event_time);
        jo.put("event_type", event_type);
        jo.put("package_name", package_name);
        return jo.toString();
    }


    public String getDataType() {
        return new DataTypes().PHONE_USAGE;
    }


    public static PhoneUsageData jsonStringToObject(String json_string) throws JSONException {
        JSONObject json = new JSONObject(json_string);
        long event_time = json.getLong("event_time");
        String event_type = json.getString("event_type");
        String package_name = json.getString("package_name");
        return new PhoneUsageData(event_time, event_type, package_name);
    }

}
