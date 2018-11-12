package com.adhiwie.moodjournal.questionnaire.goalcommitment;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.communication.helper.GCSDataTransmission;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.plan.PlanMgr;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.Popup;
import com.adhiwie.moodjournal.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;

public class GCSActivity extends AppCompatActivity {

    private final String GCS_RESULT = "GCS_RESULT";
    private final String GCS_ALERT_SHOWN = "GCS_ALERT_SHOWN";
    private final String GCS_QUESTION_NUMBER = "GCS_QUESTION_NUMBER";
    private long start_time;
    private SharedPref sp;
    private String[] questions;
    private int q_num;
    private TextView question;
    private TextView tv_questionnaire_header;
    private Button control_btn;
    private String response = null;
    private int total_questions = 5;
    private final String[] codes = new String[]{"N", "N", "R", "R", "R"};

    private Toolbar mTopToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Drawable background;
//
//        if(Build.VERSION.SDK_INT >= 21)
//            background = getResources().getDrawable(R.drawable.blue_background, null);
//        else
//            background = getResources().getDrawable(R.drawable.blue_background);
//
//
//        ActionBar actionBar = getActionBar();
//        actionBar.setBackgroundDrawable(background);
//        actionBar.setCustomView(R.layout.actionbar_layout);
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayUseLogoEnabled(true);
//        if(Build.VERSION.SDK_INT >= 18)
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
//
//        TextView actionbar_title = (TextView) findViewById(R.id.tvActionBarTitle);
//        actionbar_title.setText(getResources().getString(R.string.title_activity_gcs_test));

        sp = new SharedPref(getApplicationContext());
        if(new GCSMgr(getApplicationContext()).isGCSDone() == false)
            setContentView(R.layout.activity_gcs);
        else
            setContentView(R.layout.activity_gcs_results);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) )
        {
            Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(getApplicationContext()) );
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if( new GCSMgr(getApplicationContext()).isGCSDone())
                    finish();
                else
                    android.widget.Toast.makeText(getApplicationContext(), "You need to complete goal commitment questionnaire to continue using this app!", android.widget.Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        sp = new SharedPref(getApplicationContext());
        if(new GCSMgr(getApplicationContext()).isGCSDone() == false)
        {
            tv_questionnaire_header = (TextView) findViewById(R.id.tv_questionnaire_header);
            control_btn = (Button) findViewById(R.id.control_btn_test);
            questions = getResources().getStringArray(R.array.gcs_questions);
            q_num = getCurrentQuestionNumber();
            setQuestion(q_num);
        }
        else
            showResults();
    }

    private int getCurrentQuestionNumber() {
        int q_num = sp.getInt(GCS_QUESTION_NUMBER);
        if(q_num == 0)
        {
            q_num = 1;
            setCurrentQuestionNumber(q_num);
        }
        return q_num;
    }


    private void setCurrentQuestionNumber(int num) {
        sp.add(GCS_QUESTION_NUMBER, num);
        q_num = num;
    }

    private void setQuestion(int num) {
        start_time = Calendar.getInstance().getTimeInMillis();
        tv_questionnaire_header.setText("Question " + num + " of 5");
        question = (TextView) findViewById(R.id.tv_question);
        question.setText(questions[num-1]);

        response = null;
        final RadioGroup rg_options = (RadioGroup) findViewById(R.id.rg_options);
        rg_options.clearCheck();
        rg_options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id)
            {
                int selectedId = rg_options.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(selectedId);
                if(rb != null)
                    response = rb.getText().toString();
                else
                    response = null;
                new Log().v("Selected response: " + response);
            }
        });
    }

    public void onControlBtnClick(View v) {
        if(response == null) {
            Toast.makeText(getApplicationContext(), "Answer the current question to proceed!", Toast.LENGTH_SHORT).show();
            return;
        }

        int q_num = getCurrentQuestionNumber();

        if(q_num == 1 && sp.getBoolean(GCS_ALERT_SHOWN) == false) {
            String title = "Important";
            String message = getResources().getString(R.string.gcs_info_string);
            Popup popup = new Popup();
            popup.showPopup(GCSActivity.this, title, message);
            sp.add(GCS_ALERT_SHOWN, true);
            return;
        }

        saveResponse(q_num, response);

        if(q_num < total_questions - 1) {
            q_num = q_num + 1;
            setCurrentQuestionNumber(q_num);
            setQuestion(q_num);
            control_btn.setText("Next");
        }
        else if(q_num == total_questions - 1) {
            q_num = q_num + 1;
            setCurrentQuestionNumber(q_num);
            setQuestion(q_num);
            control_btn.setText("See results");
        }
        else {
            computeAndSaveResult();
            setCurrentQuestionNumber(0);
            new GCSMgr(getApplicationContext()).gcsCompleted();


            setContentView(R.layout.activity_gcs_results);
            showResults();
        }
    }

    private void saveResponse(int q_num, String response) {
        try {
            long current_time = Calendar.getInstance().getTimeInMillis();
            JSONObject jo = new JSONObject();
            jo.put("question", questions[q_num-1]);
            jo.put("score", getScore(q_num, response));
            jo.put("time_taken", current_time - start_time);

            new GCSMgr(getApplicationContext()).storeGCSResponse(q_num, jo.toString());
            new Log().e("q num: " + q_num + ", response data: " + jo.toString());
        }
        catch(JSONException e) {
            new Log().e(e.toString());
        }
    }

    private int getScore(int q_num, String response) {
        new Log().e("Q_num: " + q_num + ", Response: " + response);
        String code = codes[q_num-1];
        Resources res = getResources();

        if(code.equals("R")) {
            if(response.equals(res.getString(R.string.five_scale_option_disagree)))
                return 5;

            if(response.equals(res.getString(R.string.five_scale_option_somewhat_disagree)))
                return 4;

            if(response.equals(res.getString(R.string.five_scale_option_neither_agree_nor_disagree)))
                return 3;

            if(response.equals(res.getString(R.string.five_scale_option_somewhat_agree)))
                return 2;

            if(response.equals(res.getString(R.string.five_scale_option_agree)))
                return 1;
        }
        else {
            if(response.equals(res.getString(R.string.five_scale_option_disagree)))
                return 1;

            if(response.equals(res.getString(R.string.five_scale_option_somewhat_disagree)))
                return 2;

            if(response.equals(res.getString(R.string.five_scale_option_neither_agree_nor_disagree)))
                return 3;

            if(response.equals(res.getString(R.string.five_scale_option_somewhat_agree)))
                return 4;

            if(response.equals(res.getString(R.string.five_scale_option_agree)))
                return 5;
        }
        throw new NullPointerException();
    }

    private void computeAndSaveResult() {
        int total_score = 0;
        GCSMgr gm = new GCSMgr(getApplicationContext());
        for(int i = 1; i <= 5; i++) {
            try {
                String s = gm.getGCSResponse(i);
                JSONObject jo = new JSONObject(s);
                int score = jo.getInt("score");
                total_score += score;
            }
            catch(JSONException e) {
                new Log().e(e.toString());
            }
        }

        sp.add(GCS_RESULT, total_score);
    }

    private void showResults()
    {
        computeAndSaveResult();
        GCSDataTransmission gt = new GCSDataTransmission(getApplicationContext());
        if(! gt.isDataTransmitted())
        {
            gt.transmitData();
        }

        int total_scores = sp.getInt(GCS_RESULT);

        double gcs_percentage = (double) total_scores / 25 * 100;
        DecimalFormat df = new DecimalFormat("###");

        String template = getResources().getString(R.string.gcs_results_explained, total_scores);

        TextView tv_total_scores = (TextView) findViewById(R.id.tv_total_score);
        TextView tv_results_explained = (TextView) findViewById(R.id.tv_gcs_results_explained);

        tv_total_scores.setText(String.format("%s%%", df.format(gcs_percentage)));
        tv_results_explained.setText(template);

        TextView plan_label = (TextView) findViewById(R.id.plan_label);
        String routine = new PlanMgr(getApplicationContext()).getPlanRoutineDesc();
        String plan;
        SpannableStringBuilder spannable;

        plan = getResources().getString(R.string.gcs_results_plan, routine);
        spannable = new SpannableStringBuilder(plan);

        spannable.setSpan(
                new BackgroundColorSpan(0x223CB371),
                plan.indexOf(routine),
                plan.indexOf(routine)+String.valueOf(routine).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                plan.indexOf(routine),
                plan.indexOf(routine)+String.valueOf(routine).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new BackgroundColorSpan(0x223CB371),
                plan.indexOf("track my mood"),
                plan.indexOf("track my mood")+String.valueOf("track my mood").length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                plan.indexOf("track my mood"),
                plan.indexOf("track my mood")+String.valueOf("track my mood").length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        plan_label.setText(spannable);

        Button done = (Button) findViewById(R.id.done_btn_results);
        done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}
