package com.adhiwie.mymoodjournal.system;

import android.os.Build;

public class APILevel {


    private final int device_api_level = Build.VERSION.SDK_INT;


    public int getDeviceAPILevel() {
        return device_api_level;
    }

    public boolean isSuitableForAccessibilityMetaTag() {
        return (device_api_level >= Build.VERSION_CODES.ICE_CREAM_SANDWICH);
    }

    public boolean isSuitableForNLS() {
        return (device_api_level >= Build.VERSION_CODES.KITKAT);
    }


    public boolean isSuitableForAppUsageStats() {
        return (device_api_level >= Build.VERSION_CODES.LOLLIPOP);
    }


    public boolean isSuitableForScreenOn() {
        return (device_api_level < Build.VERSION_CODES.KITKAT_WATCH);
    }


}
