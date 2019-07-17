package com.adhiwie.moodjournal.communication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.exception.FileNotCreatedException;
import com.adhiwie.moodjournal.utils.Log;


public class FileTransmitter extends AsyncTask<Void, Void, Boolean> {
    private final String data;
    private final URL url;
    private final File file;

    public FileTransmitter(Context context, String file_location, String data_type) throws FileNotCreatedException, IOException, JSONException {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler))
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));


        file = new File(file_location);
        if (!file.exists())
            throw new FileNotFoundException("File not found!! Please check if the given file location is correct: " + file_location);


        JSONArray ja = new JSONArray();
        DataFormatter df = new DataFormatter();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        String exception = "";
        while ((line = br.readLine()) != null) {
            if (line != null) {
                try {
                    ja.put(df.formatData(data_type, line));
                } catch (JSONException e) {
                    exception = e.toString();
                    new Log().e(e.toString());
                }
            }
        }
        br.close();
        fr.close();

        if (ja.length() == 0)
            throw new JSONException(exception);

        new Log().e("Sending entries -- " + ja.length());

        ContentValues values = new ContentValues();
        values.put("data_array", ja.toString());

        this.data = values.toString();
        this.url = new URL("https://adhi-study-4.herokuapp.com/registrar.php");
    }


    protected Boolean doInBackground(Void... params) {
        return new HttpHelper(url).sendData(data);
    }

}