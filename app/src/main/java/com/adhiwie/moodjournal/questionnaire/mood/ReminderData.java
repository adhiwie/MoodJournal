package com.adhiwie.moodjournal.questionnaire.mood;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.moodjournal.file.DataInterface;
import com.adhiwie.moodjournal.utils.DataTypes;

public class ReminderData implements DataInterface {
    private long sent_at_millis;
    private String sent_at_time;
    private String message;

    public ReminderData(long sent_at_millis, String sent_at_time, String message) {
        this.sent_at_millis = sent_at_millis;
        this.sent_at_time = sent_at_time;
        this.message = message;
    }


    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("sent_at_millis", sent_at_millis);
        json.put("sent_at_time", sent_at_time);
        json.put("message", message);
        return json.toString();
    }


    @Override
    public String getDataType() {
        return new DataTypes().REMINDER;
    }


}

