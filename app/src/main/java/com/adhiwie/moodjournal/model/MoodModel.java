package com.adhiwie.moodjournal.model;

import java.util.HashMap;
import java.util.Map;

public class MoodModel {
    private String uid;
    private int happiness;
    private int stress;
    private int arousal;
    private long timestamp;
    private Location location;

    public MoodModel() {}

    public MoodModel(String uid, int happiness, int stress, int arousal, long timestamp, Location location) {
        this.uid = uid;
        this.happiness = happiness;
        this.stress = stress;
        this.arousal = arousal;
        this.timestamp = timestamp;
        this.location = location;
    }

    public Map toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("happiness", happiness);
        result.put("stress", stress);
        result.put("arousal", arousal);
        result.put("timestamp", timestamp);
        result.put("location", location);
        return result;
    }
}
