package com.adhiwie.moodjournal.questionnaire.mood;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.moodjournal.file.DataInterface;
import com.adhiwie.moodjournal.utils.DataTypes;

public class MoodQuestionnaireData implements DataInterface {
    private final long start_time;
    private final long end_time;
    private final int q1;
    private final int q2;
    private final int q3;

    public MoodQuestionnaireData(long start_time, long end_time, int q1, int q2, int q3) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
    }


    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("start_time", start_time);
        json.put("end_time", end_time);
        json.put("q1", q1);
        json.put("q2", q2);
        json.put("q3", q3);
        return json.toString();
    }


    @Override
    public String getDataType() {
        return new DataTypes().MOOD_QUESTIONNAIRE;
    }


}
