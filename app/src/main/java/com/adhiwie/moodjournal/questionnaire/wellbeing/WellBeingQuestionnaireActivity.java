package com.adhiwie.moodjournal.questionnaire.wellbeing;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.adhiwie.moodjournal.ConsentMgr;
import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.file.FileMgr;
import com.adhiwie.moodjournal.questionnaire.personality.PersonalityTestMgr;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.Popup;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

public class WellBeingQuestionnaireActivity extends AppCompatActivity {

    private final String PHQ8_TEST_RESULT = "PHQ8_TEST_RESULT";

    private long start_time;
    private String[] questions;

    private TextView question;
    private TextView tv_questionnaire_counter;
    private Button control_btn;
    private int response = 0;
    private int total_questions = 8;
    private SharedPref sp;


    private int a1;
    private int a2;
    private int a3;
    private int a4;
    private int a5;
    private int a6;
    private int a7;
    private int a8;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (new WellBeingQuestionnaireMgr(getApplicationContext()).getDailyQuestionnaireCountForToday() == 0) {
            setContentView(R.layout.activity_phq8_questionnaire);
        } else {
            setContentView(R.layout.activity_phq8_questionnaire_results);
        }

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }


        //remove notification if present
        //NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //mgr.cancel(5011);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (!new ConsentMgr(getApplicationContext()).isConsentGiven()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        sp = new SharedPref(getApplicationContext());
        if (new WellBeingQuestionnaireMgr(getApplicationContext()).getDailyQuestionnaireCountForToday() == 0) {
            a1 = -1;
            a2 = -1;
            a3 = -1;
            a4 = -1;
            a5 = -1;
            a6 = -1;
            a7 = -1;
            a8 = -1;

            start_time = Calendar.getInstance().getTimeInMillis();
            control_btn = (Button) findViewById(R.id.control_btn_phq_test);
            tv_questionnaire_counter = (TextView) findViewById(R.id.tv_questionnaire_counter);
            questions = getResources().getStringArray(R.array.phq_test_questions);
            setQuestion();
        } else
            showResults();
    }


    private int getCurrentQuestionNumber() {
        if (a1 == -1)
            return 1;
        else if (a2 == -1)
            return 2;
        else if (a3 == -1)
            return 3;
        else if (a4 == -1)
            return 4;
        else if (a5 == -1)
            return 5;
        else if (a6 == -1)
            return 6;
        else if (a7 == -1)
            return 7;
        else
            return 8;
    }


    private void setQuestion() {
        int q_num = getCurrentQuestionNumber();
        tv_questionnaire_counter.setText("Question " + q_num + " of 8");
        question = (TextView) findViewById(R.id.tv_question);
        question.setText(questions[q_num - 1]);

        response = -1;
        final RadioGroup rg_options = (RadioGroup) findViewById(R.id.rg_options);
        rg_options.clearCheck();
        rg_options.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                int selectedId = rg_options.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(selectedId);
                if (rb != null) {
                    String s = rb.getText().toString();
                    Resources res = getResources();
                    if (s.equals(res.getString(R.string.phq_test_option_not_at_all)))
                        response = 0;
                    else if (s.equals(res.getString(R.string.phq_test_option_several_days)))
                        response = 1;
                    else if (s.equals(res.getString(R.string.phq_test_option_more_than_half_days)))
                        response = 2;
                    else if (s.equals(res.getString(R.string.phq_test_option_nearly_every_day)))
                        response = 3;
                }
            }
        });
    }

    public void onControlBtnClick(View v) {
        if (new WellBeingQuestionnaireMgr(getApplicationContext()).getDailyQuestionnaireCountForToday() == 0) {
            if (response == -1) {
                Popup p = new Popup();
                p.showPopup(WellBeingQuestionnaireActivity.this, "Entry missing!", "Answer the current question to proceed.");
                return;
            }

            int q_num = getCurrentQuestionNumber();
            new Log().v("Question: " + q_num);

            setResponse(q_num, response);

            if (q_num < total_questions - 1) {
                setQuestion();
                control_btn.setText("Next");
            } else if (q_num == total_questions - 1) {
                setQuestion();
                control_btn.setText("Submit");
            } else {
                long end_time = Calendar.getInstance().getTimeInMillis();
                WellBeingQuestionnaireData qd = new WellBeingQuestionnaireData(start_time, end_time,
                        a1, a2, a3, a4, a5, a6, a7, a8);

                FileMgr fm = new FileMgr(getApplicationContext());
                fm.addData(qd);

                int phqScore = a1+a2+a3+a4+a5+a6+a7+a8;
                sp.add(PHQ8_TEST_RESULT, phqScore);

                new WellBeingQuestionnaireMgr(getApplicationContext()).updateLastDailyQuestionnaireDate();

                control_btn.setText("Close");
                setContentView(R.layout.activity_phq8_questionnaire_results);
                showResults();
            }
        } else {
            finish();
        }
    }

    private void showResults() {
        int phqScore = sp.getInt(PHQ8_TEST_RESULT);
        int level = 0;

        TextView tv_total_score = (TextView) findViewById(R.id.tv_total_score);
        TextView tv_phq_results_explained = (TextView) findViewById(R.id.tv_phq_results_explained);
        TextView tv_phq_results_proposed_treatment = (TextView) findViewById(R.id.tv_phq_results_proposed_treatment);
        Button retake_btn = (Button) findViewById(R.id.retake_btn);

        String[] phq_results_severity = getResources().getStringArray(R.array.phq8_results_severity);
        String[] phq8_results_treatment_actions = getResources().getStringArray(R.array.phq8_results_treatment_actions);

        if (phqScore >= 5 && phqScore<=9) {
            level = 1;
        } else if (phqScore >=10 && phqScore <=14) {
            level = 2;
        } else if (phqScore >= 15 && phqScore <= 19) {
            level = 3;
        } else if (phqScore >= 20 && phqScore <= 24) {
            level = 4;
        }

        tv_total_score.setText(Integer.toString(phqScore));
        tv_phq_results_explained.setText(getResources().getString(R.string.phq8_results_explained, phq_results_severity[level]));
        tv_phq_results_proposed_treatment.setText(getResources().getString(R.string.phq8_results_proposed_treatment, phq8_results_treatment_actions[level]));

        retake_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.add(PHQ8_TEST_RESULT, 0);
                new WellBeingQuestionnaireMgr(getApplicationContext()).resetDailyQuestionnaireCountForToday();
                setContentView(R.layout.activity_phq8_questionnaire);
                a1 = -1;
                a2 = -1;
                a3 = -1;
                a4 = -1;
                a5 = -1;
                a6 = -1;
                a7 = -1;
                a8 = -1;

                start_time = Calendar.getInstance().getTimeInMillis();
                control_btn = (Button) findViewById(R.id.control_btn_phq_test);
                tv_questionnaire_counter = (TextView) findViewById(R.id.tv_questionnaire_counter);
                questions = getResources().getStringArray(R.array.phq_test_questions);
                setQuestion();
            }
        });
    }


    private void setResponse(int q_num, int value) {
        switch (q_num) {
            case 1:
                a1 = value;
                break;
            case 2:
                a2 = value;
                break;
            case 3:
                a3 = value;
                break;
            case 4:
                a4 = value;
                break;
            case 5:
                a5 = value;
                break;
            case 6:
                a6 = value;
                break;
            case 7:
                a7 = value;
                break;
            case 8:
                a8 = value;
                break;
            default:
                break;
        }
    }


}
