package com.adhiwie.mymoodjournal.utils;

import android.view.View;

public class Snackbar {

    private final View view;

    public Snackbar(View view) {
        this.view = view;
    }

    public void shortLength(String message) {
        com.google.android.material.snackbar.Snackbar.make(view, message, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
    }

    public void longLength(String message) {
        com.google.android.material.snackbar.Snackbar.make(view, message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show();
    }

}
