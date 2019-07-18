package com.adhiwie.mymoodjournal.sensor.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.mymoodjournal.file.DataInterface;
import com.adhiwie.mymoodjournal.utils.DataTypes;

public class SmsData implements DataInterface {

    private final String address;
    private final long time;
    private final long time_sent;
    private final boolean is_read;
    private final String type;
    private final int body_length;

    public SmsData(String address, long date, long date_sent, boolean is_read,
                   String type, int body_length) {
        this.address = address;
        this.time = date;
        this.time_sent = date_sent;
        this.is_read = is_read;
        this.type = type;
        this.body_length = body_length;
    }


    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("address", address);
        json.put("date", time);
        json.put("date_sent", time_sent);
        json.put("is_read", is_read);
        json.put("type", type);
        json.put("body_length", body_length);
        return json.toString();
    }


    public String getDataType() {
        return new DataTypes().SMS;
    }


    public static SmsData jsonStringToObject(String json_string) throws JSONException {
        JSONObject json = new JSONObject(json_string);
        String address = json.getString("address");
        long date = json.getLong("date");
        long date_sent = json.getLong("date_sent");
        boolean is_read = json.getBoolean("is_read");
        String type = json.getString("type");
        int body_length = json.getInt("body_length");
        return new SmsData(address, date, date_sent, is_read, type, body_length);
    }

}
