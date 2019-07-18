package com.adhiwie.diary.communication;

import java.io.IOException;
import java.net.URL;

import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.adhiwie.diary.debug.CustomExceptionHandler;


public class DataTransmitter extends AsyncTask<Void, Void, Boolean> {
    private final String data;
    private final URL url;

    protected DataTransmitter(Context context, String data_type, String data) throws IOException, JSONException {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler))
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));

        ContentValues values = new ContentValues();
        values.put("data_instance", new DataFormatter().formatData(data_type, data));

        this.data = values.toString();
        this.url = new URL("https://ben-study.herokuapp.com/registrar.php");

    }


    protected Boolean doInBackground(Void... params) {
        return new HttpHelper(url).sendData(data);
    }


}