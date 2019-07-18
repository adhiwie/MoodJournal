package com.adhiwie.diary.questionnaire.diary;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.diary.file.DataInterface;
import com.adhiwie.diary.utils.DataTypes;

public class ReminderData implements DataInterface {
    private long sent_at_millis;
    private String sent_at_time;
    private long response_at_millis;
    private boolean is_opportune;

    public ReminderData(long sent_at_millis, String sent_at_time, long response_at_millis, boolean is_opportune) {
        this.sent_at_millis = sent_at_millis;
        this.sent_at_time = sent_at_time;
        this.response_at_millis = response_at_millis;
        this.is_opportune = is_opportune;
    }


    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("sent_at_millis", sent_at_millis);
        json.put("sent_at_time", sent_at_time);
        json.put("response_at_millis", response_at_millis);
        json.put("is_opportune", is_opportune);
        return json.toString();
    }


    @Override
    public String getDataType() {
        return new DataTypes().REMINDER;
    }


}

