package com.adhiwie.moodjournal.questionnaire.posttest;

import android.content.res.Resources;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.communication.helper.SRBAIDataTransmission;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.Popup;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SRBAIActivity extends AppCompatActivity {

    private final String SRBAI_RESULT = "SRBAI_RESULT";
    private final String SRBAI_ALERT_SHOWN = "SRBAI_ALERT_SHOWN";
    private final String SRBAI_QUESTION_NUMBER = "SRBAI_QUESTION_NUMBER";
    private long start_time;
    private SharedPref sp;
    private String[] questions;
    private int q_num;
    private TextView question;
    private TextView tv_questionnaire_header;
    private Button control_btn;
    private String response = null;
    private int total_questions = 4;
    private SRBAIMgr srbaiMgr;
    private LinearLayout root_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = new SharedPref(getApplicationContext());
        srbaiMgr = new SRBAIMgr(getApplicationContext());

        if (!srbaiMgr.isSRBAIDone())
            setContentView(R.layout.activity_srbai);
        else
            setContentView(R.layout.activity_srbai_results);

        root_layout = findViewById(R.id.root_layout);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (new SRBAIMgr(getApplicationContext()).isSRBAIDone())
                    this.finish();
                else
                    new Snackbar(root_layout).shortLength("You need to complete SRBAI questionnaire to continue using this app!");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        sp = new SharedPref(getApplicationContext());
        if (!srbaiMgr.isSRBAIDone()) {
            tv_questionnaire_header = (TextView) findViewById(R.id.tv_questionnaire_header);
            control_btn = (Button) findViewById(R.id.control_btn_test);
            questions = getResources().getStringArray(R.array.srbai_questions);
            q_num = getCurrentQuestionNumber();
            setQuestion(q_num);
        } else
            showResults();
    }

    private int getCurrentQuestionNumber() {
        int q_num = sp.getInt(SRBAI_QUESTION_NUMBER);
        if (q_num == 0) {
            q_num = 1;
            setCurrentQuestionNumber(q_num);
        }
        return q_num;
    }


    private void setCurrentQuestionNumber(int num) {
        sp.add(SRBAI_QUESTION_NUMBER, num);
        q_num = num;
    }

    private void setQuestion(int num) {
        start_time = Calendar.getInstance().getTimeInMillis();
        tv_questionnaire_header.setText("Question " + num + " of 4");
        question = (TextView) findViewById(R.id.tv_question);
        question.setText(questions[num - 1]);

        response = null;
        final RadioGroup rg_options = (RadioGroup) findViewById(R.id.rg_options);
        rg_options.clearCheck();
        rg_options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                int selectedId = rg_options.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(selectedId);
                if (rb != null)
                    response = rb.getText().toString();
                else
                    response = null;
                new Log().v("Selected response: " + response);
            }
        });
    }

    public void onControlBtnClick(View v) {
        if (response == null) {
            new Popup().showPopup(SRBAIActivity.this, "Error","Answer the current question to proceed!");
            return;
        }

        int q_num = getCurrentQuestionNumber();

        if (q_num == 1 && sp.getBoolean(SRBAI_ALERT_SHOWN) == false) {
            String title = "Important";
            String message = getResources().getString(R.string.srbai_info_string);
            Popup popup = new Popup();
            popup.showPopup(SRBAIActivity.this, title, message);
            sp.add(SRBAI_ALERT_SHOWN, true);
            return;
        }

        saveResponse(q_num, response);

        if (q_num < total_questions - 1) {
            q_num = q_num + 1;
            setCurrentQuestionNumber(q_num);
            setQuestion(q_num);
            control_btn.setText("Next");
        } else if (q_num == total_questions - 1) {
            q_num = q_num + 1;
            setCurrentQuestionNumber(q_num);
            setQuestion(q_num);
            control_btn.setText("See results");
        } else {
            computeAndSaveResult();
            setCurrentQuestionNumber(0);
            srbaiMgr.completeSRBAI();

            setContentView(R.layout.activity_srbai_results);
            showResults();
        }
    }

    private void saveResponse(int q_num, String response) {
        try {
            int participation_days = new UserData(getApplicationContext()).getParticipationDays();
            Calendar calendar = Calendar.getInstance();
            Date date=calendar.getTime();
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String report_time=dateFormat.format(date);

            long current_time = Calendar.getInstance().getTimeInMillis();
            JSONObject jo = new JSONObject();
            jo.put("question", questions[q_num - 1]);
            jo.put("score", getScore(q_num, response));
            jo.put("time_taken", current_time - start_time);
            jo.put("participation_days", participation_days);
            jo.put("report_time", report_time);

            srbaiMgr.storeSRBAIResponse(q_num, jo.toString());
            new Log().e("q num: " + q_num + ", response data: " + jo.toString());
        } catch (JSONException e) {
            new Log().e(e.toString());
        }
    }

    private int getScore(int q_num, String response) {
        new Log().e("Q_num: " + q_num + ", Response: " + response);
        Resources res = getResources();

        if (response.equals(res.getString(R.string.seven_scale_option_strongly_disagree)))
            return 1;

        if (response.equals(res.getString(R.string.seven_scale_option_disagree)))
            return 2;

        if (response.equals(res.getString(R.string.seven_scale_option_somewhat_disagree)))
            return 3;

        if (response.equals(res.getString(R.string.seven_scale_option_neither_agree_nor_disagree)))
            return 4;

        if (response.equals(res.getString(R.string.seven_scale_option_somewhat_agree)))
            return 5;

        if (response.equals(res.getString(R.string.seven_scale_option_agree)))
            return 6;

        if (response.equals(res.getString(R.string.seven_scale_option_strongly_agree)))
            return 7;
        throw new NullPointerException();
    }

    private void computeAndSaveResult() {
        int total_score = 0;
        for (int i = 1; i <= 4; i++) {
            try {
                String s = srbaiMgr.getSRBAIResponse(i);
                JSONObject jo = new JSONObject(s);
                int score = jo.getInt("score");
                total_score += score;
            } catch (JSONException e) {
                new Log().e(e.toString());
            }
        }

        sp.add(SRBAI_RESULT, total_score);
    }

    private void showResults() {
        computeAndSaveResult();
        SRBAIDataTransmission st = new SRBAIDataTransmission(getApplicationContext());
        if (!st.isDataTransmitted()) {
            st.transmitData();
        }

        int total_scores = sp.getInt(SRBAI_RESULT);

        TextView tv_total_scores = (TextView) findViewById(R.id.tv_total_score);
        tv_total_scores.setText(String.format("%d", total_scores));

        Button done = (Button) findViewById(R.id.done_btn_results);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }
}
