package com.adhiwie.mymoodjournal.questionnaire.mood;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.mymoodjournal.file.DataInterface;
import com.adhiwie.mymoodjournal.utils.DataTypes;

public class MoodQuestionnaireData implements DataInterface {
    private final long start_time;
    private final long end_time;
    private final int q1;
    private final String notes;
    private final int participation_days;
    private final String report_time;

    public MoodQuestionnaireData(long start_time, long end_time, int q1, String notes, int participation_days, String report_time) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.q1 = q1;
        this.notes = notes;
        this.participation_days = participation_days;
        this.report_time = report_time;
    }


    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("start_time", start_time);
        json.put("end_time", end_time);
        json.put("q1", q1);
        json.put("notes", notes);
        json.put("participation_days", participation_days);
        json.put("report_time", report_time);
        return json.toString();
    }


    @Override
    public String getDataType() {
        return new DataTypes().MOOD_QUESTIONNAIRE;
    }


}
