package com.adhiwie.moodjournal.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PHQModel {

    private String uid;
    private long timestamp;
    private List<Integer> scores;
    private Location location;

    public PHQModel() {}

    public PHQModel(String uid, long timestamp, List<Integer> scores, Location location) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.scores = scores;
        this.location = location;
    }

    public Map toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("timestamp", timestamp);
        result.put("phq_scores", scores);
        result.put("location", location);
        return result;
    }
}
