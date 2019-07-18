package com.adhiwie.diary.questionnaire.diary;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.diary.file.DataInterface;
import com.adhiwie.diary.utils.DataTypes;

public class DiaryStudyQuestionnaireData implements DataInterface {
    private final long start_time;
    private final long end_time;
    private final int q1;
    private final int q2;
    private final String tasks_done;
    private final String time_spent;
    private final String tasks_not_done;
    private final int participation_days;
    private final String report_time;

    public DiaryStudyQuestionnaireData(long start_time, long end_time, int q1, int q2, String tasks_done, String time_spent, String tasks_not_done, int participation_days, String report_time) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.q1 = q1;
        this.q2 = q2;
        this.tasks_done = tasks_done;
        this.time_spent = time_spent;
        this.tasks_not_done = tasks_not_done;
        this.participation_days = participation_days;
        this.report_time = report_time;
    }


    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("start_time", start_time);
        json.put("end_time", end_time);
        json.put("q1", q1);
        json.put("q2", q2);
        json.put("tasks_done", tasks_done);
        json.put("time_spent", time_spent);
        json.put("tasks_not_done", tasks_not_done);
        json.put("participation_days", participation_days);
        json.put("report_time", report_time);
        return json.toString();
    }


    @Override
    public String getDataType() {
        return new DataTypes().DAILY_STUDY_QUESTIONNAIRE;
    }


}
