package com.adhiwie.moodjournal.model;

import java.util.HashMap;
import java.util.Map;


public class UserData {

    public String emailAddress;
    public int groupId;
    public int dailyReminder;

    public UserData() {

    }

    public UserData(String emailAddress, int groupId, int dailyReminder) {
        this.emailAddress = emailAddress;
        this.groupId = groupId;
        this.dailyReminder = dailyReminder;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email_address", emailAddress);
        result.put("group_id", groupId);
        result.put("daily_reminder_status", dailyReminder);

        return result;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getDailyReminder() {
        return  dailyReminder;
    }
}
