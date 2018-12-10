package com.adhiwie.moodjournal.communication;

import java.io.IOException;
import java.net.URL;

import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.utils.Log;


public class DataTransmitter extends AsyncTask<Void, Void, Boolean> {
    private final String data;
    private final URL url;

    protected DataTransmitter(Context context, String data_type, String data) throws IOException, JSONException {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler))
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));

        ContentValues values = new ContentValues();
        values.put("data_instance", new DataFormatter().formatData(data_type, data));

        this.data = values.toString();
        //this.url =  new URL("http://www.cs.bham.ac.uk/~axm514/mytraces/data/registrar.php");
        //this.url = new URL("https://adhiwie-research.herokuapp.com/registrar.php");
        //this.url = new URL("https://adhi-study-2.herokuapp.com/registrar.php");
        this.url = new URL("https://adhi-study-3.herokuapp.com/registrar.php");

    }


    protected Boolean doInBackground(Void... params) {
        return new HttpHelper(url).sendData(data);
    }


}