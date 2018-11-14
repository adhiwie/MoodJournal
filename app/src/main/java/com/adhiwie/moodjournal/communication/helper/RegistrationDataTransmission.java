package com.adhiwie.moodjournal.communication.helper;

import org.json.JSONException;

import android.content.Context;

import com.adhiwie.moodjournal.communication.DataTransmitter;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.DataTypes;
import com.adhiwie.moodjournal.utils.Log;

public class RegistrationDataTransmission {

    private final Context context;
    private final String data;

    public RegistrationDataTransmission(Context context) throws JSONException {
        this.context = context;
        this.data = new UserData(context).toJSONString();
        new Log().d(data);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));
        }
    }


    public void registerNow(final RegisterationResultListener listener) {
        try {
            new DataTransmitter(this.context, new DataTypes().REGISTRATION, data) {
                @Override
                protected void onPostExecute(Boolean result) {
                    listener.onResultAvailable(result);
                }

                ;
            }.execute();
        } catch (Exception e) {
            listener.onResultAvailable(false);
        }
    }

    public interface RegisterationResultListener {
        public void onResultAvailable(boolean result);
    }


}
