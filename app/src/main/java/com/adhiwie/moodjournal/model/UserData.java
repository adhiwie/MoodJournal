package com.adhiwie.moodjournal.model;

import java.util.HashMap;
import java.util.Map;


public class UserData {

    private String user_name;
    private String plan;
    private int group_id;
    private int group_is_set;
    private int daily_reminder_status;
    private long daily_reminder_time;
    private String daily_reminder_time_string;
    private String daily_reminder_address;
    private double daily_reminder_latitude;
    private double daily_reminder_longitude;
    private int pre_test;
    private int post_test;
    private int is_questionnaire;

    public UserData() {

    }

    public UserData(String user_name,
                    String plan,
                    int group_id,
                    int group_is_set,
                    int daily_reminder_status,
                    long daily_reminder_time,
                    String daily_reminder_time_string,
                    String daily_reminder_address,
                    double daily_reminder_latitude,
                    double daily_reminder_longitude,
                    int pre_test,
                    int post_test,
                    int is_questionnaire) {
        this.user_name = user_name;
        this.plan = plan;
        this.group_id = group_id;
        this.group_is_set = group_is_set;
        this.daily_reminder_status = daily_reminder_status;
        this.daily_reminder_time = daily_reminder_time;
        this.daily_reminder_time_string = daily_reminder_time_string;
        this.daily_reminder_address = daily_reminder_address;
        this.daily_reminder_latitude = daily_reminder_latitude;
        this.daily_reminder_longitude = daily_reminder_longitude;
        this.pre_test = pre_test;
        this.post_test = post_test;
        this.is_questionnaire = is_questionnaire;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_name", user_name);
        result.put("plan", plan);
        result.put("group_id", group_id);
        result.put("group_is_set", group_is_set);
        result.put("daily_reminder_status", daily_reminder_status);
        result.put("daily_reminder_time", daily_reminder_time);
        result.put("daily_reminder_time_string", daily_reminder_time_string);
        result.put("daily_reminder_address", daily_reminder_address);
        result.put("daily_reminder_latitude", daily_reminder_latitude);
        result.put("daily_reminder_longitude", daily_reminder_longitude);
        result.put("pre_test", pre_test);
        result.put("post_test", post_test);
        result.put("is_questionnaire", is_questionnaire);

        return result;
    }

    public String getUser_name(){
        return user_name;
    }

    public String getPlan() {
        return plan;
    }

    public int getGroup_id() {
        return group_id;
    }

    public int getGroup_is_set() {
        return group_is_set;
    }

    public int getDaily_reminder_status() {
        return daily_reminder_status;
    }

    public long getDaily_reminder_time() {
        return daily_reminder_time;
    }

    public String getDaily_reminder_time_string() {
        return daily_reminder_time_string;
    }

    public String getDaily_reminder_address() {
        return daily_reminder_address;
    }

    public double getDaily_reminder_latitude() {
        return daily_reminder_latitude;
    }

    public double getDaily_reminder_longitude() {
        return daily_reminder_longitude;
    }

    public int getPre_test() {
        return pre_test;
    }

    public int getPost_test() {
        return post_test;
    }

    public int is_questionnaire() {
        return is_questionnaire;
    }
}
