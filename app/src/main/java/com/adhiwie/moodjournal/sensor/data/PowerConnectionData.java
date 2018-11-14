package com.adhiwie.moodjournal.sensor.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.moodjournal.file.DataInterface;
import com.adhiwie.moodjournal.utils.DataTypes;

public class PowerConnectionData implements DataInterface {

    public static final String SOURCE_AC = "AC";
    public static final String SOURCE_USB = "USB";
    public static final String SOURCE_WIRELESS = "WIRELESS";
    public static final String SOURCE_UNKNOWN = "UNKNOWN";

    private final boolean is_charging;
    private final String source;
    private final long time;
    private final float level;

    public PowerConnectionData(boolean is_charging, String source, float level, long time) {
        this.is_charging = is_charging;
        this.source = source;
        this.time = time;
        this.level = level;
    }


    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("charging", is_charging);
        json.put("source", source);
        json.put("time", time);
        json.put("level", level);
        return json.toString();
    }


    public String getDataType() {
        return new DataTypes().POWER_CONNECTIVITY;
    }


    public static PowerConnectionData jsonStringToObject(String json_string) throws JSONException {
        JSONObject json = new JSONObject(json_string);
        boolean is_charging = json.getBoolean("charging");
        String source = json.getString("source");
        long time = json.getLong("time");
        String level_string = json.getString("level");
        float level = level_string == null ? 0f : Float.parseFloat(level_string);
        return new PowerConnectionData(is_charging, source, level, time);
    }


}
