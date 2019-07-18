package com.adhiwie.mymoodjournal.sensor.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.adhiwie.mymoodjournal.file.DataInterface;
import com.adhiwie.mymoodjournal.utils.DataTypes;

public class LocationData implements DataInterface {

    private final double lat;
    private final double lon;
    private final long time;
    private final String provider;
    private final float accuracy;


    public LocationData(double lat, double lon, long time, String provider, float accuracy) {
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.provider = provider;
        this.accuracy = accuracy;
    }


    public String toJSONString() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("latitude", lat);
        json.put("longitude", lon);
        json.put("time", time);
        json.put("provider", provider);
        json.put("accuracy", String.valueOf(accuracy));
        return json.toString();
    }


    public String getDataType() {
        return new DataTypes().LOCATION;
    }


    public static LocationData jsonStringToObject(String jsonString) throws JSONException {
        if (jsonString == null)
            return null;

        JSONObject json = new JSONObject(jsonString);
        double lat = json.getDouble("latitude");
        double lon = json.getDouble("longitude");
        long time = json.getLong("time");
        String provider = json.getString("provider");
        float accuracy = Float.parseFloat(json.getString("accuracy"));

        return new LocationData(lat, lon, time, provider, accuracy);

    }


    public double getLat() {
        return lat;
    }


    public double getLon() {
        return lon;
    }


    public long getTime() {
        return time;
    }


    public String getProvider() {
        return provider;
    }


    public float getAccuracy() {
        return accuracy;
    }


}
