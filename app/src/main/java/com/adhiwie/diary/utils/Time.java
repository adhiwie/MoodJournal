package com.adhiwie.diary.utils;

import java.util.Calendar;

public class Time implements Comparable<Time> {

    public static int DAY_OF_WEEK = Calendar.DAY_OF_WEEK;
    public static int HOURS = Calendar.HOUR_OF_DAY;
    public static int MINUTES = Calendar.MINUTE;
    public static int SECONDS = Calendar.SECOND;
    public static int MILISECONDS = Calendar.MILLISECOND;


    private final long millis_in_a_day = 24 * 60 * 60 * 1000;
    private int day = 0;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private int milliseconds = 0;
    private long timeMillis = 0;
    private int days_from_epoch = 0;

    public Time(Calendar c) {
        this.day = c.get(Calendar.DAY_OF_WEEK);
        this.hours = c.get(Calendar.HOUR_OF_DAY);
        this.minutes = c.get(Calendar.MINUTE);
        this.seconds = c.get(Calendar.SECOND);
        this.milliseconds = c.get(Calendar.MILLISECOND);
        this.timeMillis = c.getTimeInMillis();
        this.days_from_epoch = (int) (timeMillis / millis_in_a_day);
    }

    public Time(long timeMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeMillis);
        this.day = c.get(Calendar.DAY_OF_WEEK);
        this.hours = c.get(Calendar.HOUR_OF_DAY);
        this.minutes = c.get(Calendar.MINUTE);
        this.seconds = c.get(Calendar.SECOND);
        this.milliseconds = c.get(Calendar.MILLISECOND);
        this.timeMillis = timeMillis;
        this.days_from_epoch = (int) (timeMillis / millis_in_a_day);
    }


    public Time(int epoch_days) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(epoch_days * millis_in_a_day);
        this.day = c.get(Calendar.DAY_OF_WEEK);
        this.hours = c.get(Calendar.HOUR_OF_DAY);
        this.minutes = c.get(Calendar.MINUTE);
        this.seconds = c.get(Calendar.SECOND);
        this.milliseconds = c.get(Calendar.MILLISECOND);
        this.timeMillis = c.getTimeInMillis();
        this.days_from_epoch = epoch_days;
    }


    public static Time getCurrentTime() {
        return new Time(Calendar.getInstance());
    }

    public long getCurrentTimeInMilliseconds() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public int get(int unit) {
        switch (unit) {
            case (Calendar.DAY_OF_WEEK):
                return day;
            case (Calendar.HOUR_OF_DAY):
                return hours;
            case (Calendar.MINUTE):
                return minutes;
            case (Calendar.SECOND):
                return seconds;
            case (Calendar.MILLISECOND):
                return milliseconds;
            default:
                return -1;
        }
    }

    public long getMillis() {
        return this.timeMillis;
    }

    public String toString() {
        String string = String.valueOf(this.timeMillis);
        return string;
    }

    public static Time stringToTime(String string) {
        if (string == null)
            return null;
        long timeMillis = Long.parseLong(string);
        return new Time(timeMillis);
    }

    @Override
    public int compareTo(Time another) {
        if (this.timeMillis > another.timeMillis)
            return 1;
        else
            return -1;
    }

    public int getEpochDays() {
        return days_from_epoch;
    }

}
