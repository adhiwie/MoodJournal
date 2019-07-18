package com.adhiwie.mymoodjournal.sensor.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.mymoodjournal.file.DataInterface;
import com.adhiwie.mymoodjournal.utils.DataTypes;

public class ActivityData implements DataInterface {

    private final String activity;
    private final int confidence;
    private final long time;


    public ActivityData(String activity, int confidence, long time) {
        this.activity = activity;
        this.confidence = confidence;
        this.time = time;
    }

    public String toJSONString() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("activity", activity);
        jo.put("confidence", confidence);
        jo.put("time", time);
        return jo.toString();
    }


    public String getDataType() {
        return new DataTypes().ACTIVITY;
    }

    public static ActivityData jsonStringToObject(String json_string) throws JSONException {
        if (json_string == null)
            return null;

        JSONObject json = new JSONObject(json_string);
        String activity = json.getString("activity");
        int confidence = json.getInt("confidence");
        long time = json.getLong("time");

        return new ActivityData(activity, confidence, time);
    }


    public String getActivity() {
        return activity;
    }


    public int getConfidence() {
        return confidence;
    }

    public long getTime() {
        return time;
    }

}
