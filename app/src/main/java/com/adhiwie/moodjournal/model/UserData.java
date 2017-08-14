package com.adhiwie.moodjournal.model;

import java.util.HashMap;
import java.util.Map;


public class UserData {

    private String plan;
    private int group_id;
    private int daily_reminder_status;
    private long daily_reminder_time;
    private String daily_reminder_time_string;
    private String daily_reminder_address;
    private double daily_reminder_latitude;
    private double daily_reminder_longitude;

    public UserData() {

    }

    public UserData(String plan,
                    int group_id,
                    int daily_reminder_status,
                    long daily_reminder_time,
                    String daily_reminder_time_string,
                    String daily_reminder_address,
                    double daily_reminder_latitude,
                    double daily_reminder_longitude) {
        this.plan = plan;
        this.group_id = group_id;
        this.daily_reminder_status = daily_reminder_status;
        this.daily_reminder_time = daily_reminder_time;
        this.daily_reminder_time_string = daily_reminder_time_string;
        this.daily_reminder_address = daily_reminder_address;
        this.daily_reminder_latitude = daily_reminder_latitude;
        this.daily_reminder_longitude = daily_reminder_longitude;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("plan", plan);
        result.put("group_id", group_id);
        result.put("daily_reminder_status", daily_reminder_status);
        result.put("daily_reminder_time", daily_reminder_time);
        result.put("daily_reminder_time_string", daily_reminder_time_string);
        result.put("daily_reminder_address", daily_reminder_address);
        result.put("daily_reminder_latitude", daily_reminder_latitude);
        result.put("daily_reminder_longitude", daily_reminder_longitude);

        return result;
    }

    public String getPlan() {
        return plan;
    }

    public int getGroup_id() {
        return group_id;
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
}
