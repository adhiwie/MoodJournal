package com.adhiwie.mymoodjournal.plan;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.mymoodjournal.file.DataInterface;
import com.adhiwie.mymoodjournal.utils.DataTypes;

public class PlanData implements DataInterface {
    private String uuid;
    private String routine_desc;

    public PlanData(String uuid, String routine_desc) {
        this.uuid = uuid;
        this.routine_desc = routine_desc;
    }


    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("uuid", uuid);
        json.put("routine_desc", routine_desc);
        return json.toString();
    }


    @Override
    public String getDataType() {
        return new DataTypes().PLAN;
    }


}
