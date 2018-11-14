package com.adhiwie.moodjournal.communication;

import android.content.Context;
import android.os.AsyncTask;

import com.adhiwie.moodjournal.debug.CustomExceptionHandler;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;


public class DataGetter extends AsyncTask<Void, Void, String> {
    private final URL url;

    protected DataGetter(Context context, String uuid) throws IOException {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler))
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));

        this.url = new URL("https://adhi-study-2.herokuapp.com/query.php?uuid=" + uuid);

    }


    protected String doInBackground(Void... params) {
        return new HttpHelper(url).getData();
    }


}