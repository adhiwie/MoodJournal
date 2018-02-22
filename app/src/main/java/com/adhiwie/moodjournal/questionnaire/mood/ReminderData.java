package com.adhiwie.moodjournal.questionnaire.mood;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.moodjournal.file.DataInterface;
import com.adhiwie.moodjournal.utils.DataTypes;

public class ReminderData implements DataInterface
{
    private String uuid;
    private long sent_at;
    private String routine_desc;

    public ReminderData(String uuid, long sent_at, String routine_desc)
    {
        this.uuid = uuid;
        this.sent_at = sent_at;
        this.routine_desc = routine_desc;
    }


    public String toJSONString() throws JSONException
    {
        JSONObject json = new JSONObject();
        json.put("uuid", uuid);
        json.put("sent_at", sent_at);
        json.put("routine_desc", routine_desc);
        return json.toString();
    }


    @Override
    public String getDataType()
    {
        return new DataTypes().REMINDER;
    }


}

