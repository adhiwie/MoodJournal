package com.adhiwie.mymoodjournal.sensor.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.mymoodjournal.file.DataInterface;
import com.adhiwie.mymoodjournal.utils.DataTypes;

public class NetworkStateData implements DataInterface {

    private final String type;
    private final String description;
    private final String state;
    private final long time;


    public NetworkStateData(String type, String description, String state, long time) {
        this.type = type;
        this.description = description;
        this.state = state;
        this.time = time;
    }

    @Override
    public String toJSONString() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("type", type);
        jo.put("description", description);
        jo.put("state", state);
        jo.put("time", time);
        return jo.toString();
    }

    @Override
    public String getDataType() {
        return new DataTypes().NETWORK;
    }


    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }

    public long getTime() {
        return time;
    }

}
