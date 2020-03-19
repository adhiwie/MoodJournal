package com.adhiwie.moodjournal.utils;

import android.content.Context;

public class Toast {

    private final Context context;

    public Toast(Context context) {
        this.context = context;
    }

    public void shortLength(String message) {
        android.widget.Toast.makeText(this.context, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    public void longLength(String message) {
        android.widget.Toast.makeText(this.context, message, android.widget.Toast.LENGTH_LONG).show();
    }

}
