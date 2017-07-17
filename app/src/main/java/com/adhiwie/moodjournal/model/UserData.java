package com.adhiwie.moodjournal.model;

import java.util.HashMap;
import java.util.Map;


public class UserData {

    private String emailAddress;
    private int groupId;
    private int dailyReminderStatus;
    private long dailyReminderTime;

    public UserData() {

    }

    public UserData(String emailAddress, int groupId, int dailyReminderStatus, long dailyReminderTime) {
        this.emailAddress = emailAddress;
        this.groupId = groupId;
        this.dailyReminderStatus = dailyReminderStatus;
        this.dailyReminderTime = dailyReminderTime;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email_address", emailAddress);
        result.put("group_id", groupId);
        result.put("daily_reminder_status", dailyReminderStatus);
        result.put("daily_reminder_time", dailyReminderTime);

        return result;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getDailyReminderStatus() {
        return dailyReminderStatus;
    }

    public long getDailyReminderTime() { return dailyReminderTime;}
}
