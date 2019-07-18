package com.adhiwie.mymoodjournal.communication.helper;

import android.content.Context;

import com.adhiwie.mymoodjournal.communication.DataTransmitter;
import com.adhiwie.mymoodjournal.debug.CustomExceptionHandler;
import com.adhiwie.mymoodjournal.questionnaire.goalcommitment.GCSMgr;
import com.adhiwie.mymoodjournal.user.data.UserData;
import com.adhiwie.mymoodjournal.utils.DataTypes;
import com.adhiwie.mymoodjournal.utils.Log;
import com.adhiwie.mymoodjournal.utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GCSDataTransmission {

    private final String GCS_RESULT = "GCS_RESULT";
    private final String GCS_RESULT_TRANSMITTED = "GCS_RESULT_TRANSMITTED";
    private final String GCS_RESULT_AVAILABLE = "GCS_RESULT_AVAILABLE";
    private final Context context;
    private final SharedPref sp;

    public GCSDataTransmission(Context context) {
        this.context = context;
        this.sp = new SharedPref(context);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));
        }
    }

    public boolean isDataAvailable() {
        return sp.getBoolean(GCS_RESULT_AVAILABLE);
    }

    public boolean isDataTransmitted() {
        return sp.getBoolean(GCS_RESULT_TRANSMITTED);
    }

    public void transmitData() {
        sp.add(GCS_RESULT_AVAILABLE, true);
        JSONObject json = new JSONObject();
        try {
            int total_scores = sp.getInt(GCS_RESULT);

            JSONArray data = new JSONArray();

            GCSMgr gm = new GCSMgr(context);
            for (int i = 1; i <= 5; i++) {
                try {
                    String s = gm.getGCSResponse(i);
                    JSONObject jo = new JSONObject(s);
                    data.put(jo);
                } catch (JSONException e) {
                    new Log().e(e.toString());
                }
            }

            json.put("uuid", new UserData(context).getUuid());
            json.put("goal_commitment_score", total_scores);
            json.put("answers", data);

            new DataTransmitter(this.context, new DataTypes().GOAL_COMMITMENT_SCALE, json.toString()) {
                @Override
                protected void onPostExecute(Boolean result) {
                    new Log().e("GCS data transmission result: " + result);
                    if (result)
                        sp.add(GCS_RESULT_TRANSMITTED, true);
                }

                ;
            }.execute();

        } catch (Exception e) {
            sp.add(GCS_RESULT_TRANSMITTED, false);
        }
    }


}
