package com.adhiwie.moodjournal.communication.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.adhiwie.moodjournal.communication.DataTransmitter;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.questionnaire.personality.PersonalityTestMgr;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.DataTypes;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;

public class PersonalityTestDataTransmission {

    private final String PERSONALITY_TEST_RESULT_TRANSMITTED = "PERSONALITY_TEST_RESULT_TRANSMITTED";
    private final String PERSONALITY_TEST_RESULT_AVAILABLE = "PERSONALITY_TEST_RESULT_AVAILABLE";
    private final Context context;
    private final SharedPref sp;


    public PersonalityTestDataTransmission(Context context) {
        this.context = context;
        this.sp = new SharedPref(context);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));
        }
    }


    public boolean isDataAvailable() {
        return sp.getBoolean(PERSONALITY_TEST_RESULT_AVAILABLE);
    }

    public boolean isDataTransmitted() {
        return sp.getBoolean(PERSONALITY_TEST_RESULT_TRANSMITTED);
    }


    public void transmitData() {
        sp.add(PERSONALITY_TEST_RESULT_AVAILABLE, true);
        JSONArray data = new JSONArray();
        try {
            JSONObject uuid = new JSONObject();
            uuid.put("uuid", new UserData(context).getUuid());
            data.put(uuid);
            PersonalityTestMgr pm = new PersonalityTestMgr(context);
            for (int i = 1; i <= 50; i++) {
                try {
                    String s = pm.getPersonalityTestResponse(i);
                    JSONObject jo = new JSONObject(s);
                    data.put(jo);
                } catch (JSONException e) {
                    new Log().e(e.toString());
                }
            }


            new DataTransmitter(this.context, new DataTypes().PERSONALITY_TEST, data.toString()) {
                @Override
                protected void onPostExecute(Boolean result) {
                    new Log().e("Personality test data transmission result: " + result);
                    if (result)
                        sp.add(PERSONALITY_TEST_RESULT_TRANSMITTED, true);
                }

                ;
            }.execute();

        } catch (Exception e) {
            sp.add(PERSONALITY_TEST_RESULT_TRANSMITTED, false);
        }
    }


}
