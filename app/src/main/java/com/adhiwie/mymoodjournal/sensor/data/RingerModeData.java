package com.adhiwie.mymoodjournal.sensor.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.mymoodjournal.file.DataInterface;
import com.adhiwie.mymoodjournal.utils.DataTypes;

public class RingerModeData implements DataInterface {

    private final String ringer_mode;
    private final String zen_mode;
    private final long time;


    public RingerModeData(String ringer_mode, String zen_mode, long time) {
        this.ringer_mode = ringer_mode;
        this.zen_mode = zen_mode;
        this.time = time;
    }

    public String toJSONString() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("ringer_mode", ringer_mode);
        jo.put("zen_mode", zen_mode);
        jo.put("time", time);
        return jo.toString();
    }


    public String getDataType() {
        return new DataTypes().RINGER_MODE;
    }


    public static RingerModeData jsonStringToObject(String json_string) throws JSONException {
        if (json_string == null)
            return null;

        JSONObject json = new JSONObject(json_string);
        String ringer_mode = json.getString("ringer_mode");
        String zen_mode = json.getString("zen_mode");
        long time = json.getLong("time");

        return new RingerModeData(ringer_mode, zen_mode, time);
    }


}
