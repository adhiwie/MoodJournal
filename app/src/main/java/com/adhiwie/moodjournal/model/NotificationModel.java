package com.adhiwie.moodjournal.model;

import java.util.HashMap;
import java.util.Map;

public class NotificationModel {
    private String uid;
    private long timestamp;

    public NotificationModel() {}

    public NotificationModel(String uid, long timestamp) {
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public Map toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("timestamp", timestamp);
        return result;
    }
}
