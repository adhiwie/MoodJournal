package com.adhiwie.mymoodjournal.sensor.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.mymoodjournal.file.DataInterface;
import com.adhiwie.mymoodjournal.utils.DataTypes;

public class NotificationData implements DataInterface {
    private int n_id;
    private String tag;
    private String key;
    private int priority;
    private String title;
    private long arrivalTime;
    private long removalTime;
    private int clicked;
    private boolean led;
    private boolean vibrate;
    private boolean sound;
    private boolean unique_sound;
    private String app_name;
    private String app_package;


    public NotificationData(int n_id, String tag, String key, int priority,
                            String title, long arrivalTime, long removalTime,
                            int clicked, boolean led, boolean vibrate, boolean sound, boolean unique_sound,
                            String app_name, String app_package) {
        this.n_id = n_id;
        this.tag = tag;
        this.key = key;
        this.priority = priority;
        this.title = title;
        this.arrivalTime = arrivalTime;
        this.removalTime = removalTime;
        this.clicked = clicked;
        this.led = led;
        this.vibrate = vibrate;
        this.sound = sound;
        this.unique_sound = unique_sound;
        this.app_name = app_name;
        this.app_package = app_package;
    }


    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("n_id", n_id);
        json.put("tag", tag == null ? "null" : tag);
        json.put("key", key == null ? "null" : key);
        json.put("priority", priority);
        json.put("title", title);
        json.put("arrival_time", arrivalTime);
        json.put("removal_time", removalTime);
        json.put("clicked", clicked);
        json.put("led", led);
        json.put("vibrate", vibrate);
        json.put("sound", sound);
        json.put("unique_sound", unique_sound);
        json.put("app_name", app_name);
        json.put("app_package", app_package);
        return json.toString();
    }


    public String getDataType() {
        return new DataTypes().NOTIFICATION;
    }

    public static NotificationData jsonStringToDataObject(String string) throws JSONException {
        if (string == null)
            return null;

        JSONObject json = new JSONObject(string);
        String title = "title";
        try {
            if (json.getString("title") != null)
                title = json.getString("title");
        } catch (JSONException e) {
        }

        NotificationData n_data = new NotificationData(
                json.getInt("n_id"),
                json.getString("tag"),
                json.getString("key"),
                json.getInt("priority"),
                title,
                json.getLong("arrival_time"),
                json.getLong("removal_time"),
                json.getInt("clicked"),
                json.getBoolean("led"),
                json.getBoolean("vibrate"),
                json.getBoolean("sound"),
                json.getBoolean("unique_sound"),
                json.getString("app_name"),
                json.getString("app_package")
        );
        return n_data;
    }


    public int getN_id() {
        return n_id;
    }


    public String getTag() {
        return tag;
    }


    public String getKey() {
        return key;
    }


    public int getPriority() {
        return priority;
    }


    public String getTitle() {
        return title;
    }


    public long getArrivalTime() {
        return arrivalTime;
    }


    public long getRemovalTime() {
        return removalTime;
    }


    /**
     * @return (int) -1 refers to click from other device, 0 for dismissal, and 1 for acceptance
     */
    public int getClicked() {
        return clicked;
    }


    public boolean isLed() {
        return led;
    }


    public boolean isVibrate() {
        return vibrate;
    }


    public boolean isSound() {
        return sound;
    }


    public boolean isUnique_sound() {
        return unique_sound;
    }


    public String getAppName() {
        return app_name;
    }


    public String getAppPackageName() {
        return app_package;
    }


    public void setN_id(int n_id) {
        this.n_id = n_id;
    }


    public void setTag(String tag) {
        this.tag = tag;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public void setPriority(int priority) {
        this.priority = priority;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }


    public void setRemovalTime(long removalTime) {
        this.removalTime = removalTime;
    }


    public void setClicked(int clicked) {
        this.clicked = clicked;
    }


    public void setLed(boolean led) {
        this.led = led;
    }


    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }


    public void setSound(boolean sound) {
        this.sound = sound;
    }


    public void setUnique_sound(boolean unique_sound) {
        this.unique_sound = unique_sound;
    }


    public void setAppName(String app_name) {
        this.app_name = app_name;
    }


    public void setAppPackage(String app_package) {
        this.app_package = app_package;
    }


}
