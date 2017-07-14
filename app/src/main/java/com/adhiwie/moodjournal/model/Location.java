package com.adhiwie.moodjournal.model;

public class Location {
    private double longitude;
    private double latitude;
    private String address;

    public Location(double longitude, double latitude, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getAddress() {
        return address;
    }
}
