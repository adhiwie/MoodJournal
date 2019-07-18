package com.adhiwie.mymoodjournal.questionnaire.mood;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.mymoodjournal.file.DataInterface;
import com.adhiwie.mymoodjournal.utils.DataTypes;

public class ReminderData implements DataInterface {
    private long sent_at_millis;
    private String sent_at_time;
    private long response_at_millis;

    public ReminderData(long sent_at_millis, String sent_at_time, long response_at_millis) {
        this.sent_at_millis = sent_at_millis;
        this.sent_at_time = sent_at_time;
        this.response_at_millis = response_at_millis;
    }


    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("sent_at_millis", sent_at_millis);
        json.put("sent_at_time", sent_at_time);
        json.put("response_at_millis", response_at_millis);
        return json.toString();
    }


    @Override
    public String getDataType() {
        return new DataTypes().REMINDER;
    }


}

