package com.adhiwie.moodjournal.plan;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.moodjournal.file.DataInterface;
import com.adhiwie.moodjournal.utils.DataTypes;

public class PlanData implements DataInterface
{
    private String uuid;
    private final String timing;
    private String routine_desc;
    private final int hour;
    private final int minute;

    public PlanData(String uuid, String timing, String routine_desc, int hour, int minute)
    {
        this.uuid = uuid;
        this.timing = timing;
        this.routine_desc = routine_desc;
        this.hour = hour;
        this.minute = minute;
    }


    public String toJSONString() throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put("uuid", uuid);
        json.put("timing", timing);
        json.put("routine_desc", routine_desc);
        json.put("hour", hour);
        json.put("minute", minute);
        return json.toString();
    }


    @Override
    public String getDataType()
    {
        return new DataTypes().PLAN;
    }


}
