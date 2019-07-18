package com.adhiwie.mymoodjournal.utils;

import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPref {

    private final SharedPreferences sp;
    private final String sp_name = "MOOD_JOURNAL";


    public SharedPref(Context context) {
        this.sp = context.getSharedPreferences(sp_name, 0);
    }

    public void remove(String key) {
        Editor ed = sp.edit();
        ed.remove(key);
        ed.commit();
    }

    public void add(String key, String value) {
        Editor ed = sp.edit();
        ed.putString(key, value);
        ed.commit();
    }

    public void add(String key, float value) {
        Editor ed = sp.edit();
        ed.putFloat(key, value);
        ed.commit();
    }

    public void add(String key, int value) {
        Editor ed = sp.edit();
        ed.putInt(key, value);
        ed.commit();
    }

    public void add(String key, boolean value) {
        Editor ed = sp.edit();
        ed.putBoolean(key, value);
        ed.commit();
    }

    public void add(String key, long value) {
        Editor ed = sp.edit();
        ed.putLong(key, value);
        ed.commit();
    }

    public void add(String key, Set<String> value) {
        Editor ed = sp.edit();
        ed.putStringSet(key, value);
        ed.commit();
    }

    public Set<String> getStringSet(String key) {
        return sp.getStringSet(key, null);
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public float getFloat(String key) {
        return sp.getFloat(key, 0);
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public long getLong(String key) {
        return sp.getLong(key, 0);
    }

    public Map<String, ?> getAll() {
        return sp.getAll();
    }

}
