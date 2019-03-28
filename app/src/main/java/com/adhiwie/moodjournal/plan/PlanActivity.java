package com.adhiwie.moodjournal.plan;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.communication.helper.PlanDataTransmission;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.Snackbar;

import org.json.JSONException;

import androidx.appcompat.app.AppCompatActivity;

public class PlanActivity extends AppCompatActivity {

    private int step;
    private String routine;
    private LinearLayout create_plan_layout;
    private ScrollView intro_layout;
    private RelativeLayout step_1_layout;
    private RelativeLayout step_2_layout;
    private RelativeLayout step_3_layout;
    private TextView detailed_plan_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        if (getIntent().getIntExtra("step", 0) == 0) {
            initLayout();
        }

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }

        routine = "arrive at home";

        if (!new PlanMgr(getApplicationContext()).isPlanGiven()) {
            initLayout();
        }
    }

    private void initLayout() {
        step = 0;

        create_plan_layout = (LinearLayout) findViewById(R.id.create_plan_layout);
        intro_layout = (ScrollView) findViewById(R.id.intro_layout);
        step_1_layout = (RelativeLayout) findViewById(R.id.step_1_layout);
        step_2_layout = (RelativeLayout) findViewById(R.id.step_2_layout);
        step_3_layout = (RelativeLayout) findViewById(R.id.step_3_layout);

        intro_layout.setVisibility(View.VISIBLE);
        step_1_layout.setVisibility(View.GONE);
        step_2_layout.setVisibility(View.GONE);
        step_3_layout.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                switch (step) {
                    case 0:
                        PlanMgr planMgr = new PlanMgr(getApplicationContext());
                        if (planMgr.isPlanGiven())
                            this.finish();
                        else
                            new Snackbar(create_plan_layout).shortLength("You need to setup a plan to continue using this app!");
                        break;
                    case 1:
                        initLayout();
                        break;
                    case 2:
                        goToStepOne();
                        break;
                    case 3:
                        goToStepTwo();
                        break;

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCreateButtonClick(View v) {
        goToStepOne();
    }


    private void goToStepOne() {
        step = 1;

        intro_layout.setVisibility(View.GONE);
        step_1_layout.setVisibility(View.VISIBLE);
        step_2_layout.setVisibility(View.GONE);
        step_3_layout.setVisibility(View.GONE);

        detailed_plan_tv = (TextView) findViewById(R.id.step_1_plan_tv);
        final String plan = getResources().getString(R.string.detailed_plan);

        SpannableStringBuilder spannable = new SpannableStringBuilder(plan);

        spannable.setSpan(
                new BackgroundColorSpan(0x223CB371),
                plan.indexOf(routine),
                plan.indexOf(routine) + String.valueOf(routine).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                plan.indexOf(routine),
                plan.indexOf(routine) + String.valueOf(routine).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new BackgroundColorSpan(0x22FF2D00),
                plan.indexOf("track my mood"),
                plan.indexOf("track my mood") + String.valueOf("track my mood").length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                plan.indexOf("track my mood"),
                plan.indexOf("track my mood") + String.valueOf("track my mood").length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        detailed_plan_tv.setText(spannable);

        Button buttonContinue = (Button) findViewById(R.id.step_1_btn);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToStepTwo();
            }
        });

    }

    private void goToStepTwo() {
        step = 2;

        intro_layout.setVisibility(View.GONE);
        step_1_layout.setVisibility(View.GONE);
        step_2_layout.setVisibility(View.VISIBLE);
        step_3_layout.setVisibility(View.GONE);

        final String plan = "then I will track my mood";

        SpannableStringBuilder spannable = new SpannableStringBuilder(plan);

        spannable.setSpan(
                new BackgroundColorSpan(0x22FF2D00),
                plan.indexOf("track my mood"),
                plan.indexOf("track my mood") + String.valueOf("track my mood").length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                plan.indexOf("track my mood"),
                plan.indexOf("track my mood") + String.valueOf("track my mood").length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        TextView then_tv = (TextView) findViewById(R.id.then_tv);
        then_tv.setText(spannable);

        final EditText condition_et = (EditText) findViewById(R.id.condition_et);

        Button buttonContinue = (Button) findViewById(R.id.step_2_btn);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String condition = condition_et.getText().toString();
                goToStepThree(condition);
            }
        });
    }

    private void goToStepThree(String condition) {
        step = 3;

        intro_layout.setVisibility(View.GONE);
        step_1_layout.setVisibility(View.GONE);
        step_2_layout.setVisibility(View.GONE);
        step_3_layout.setVisibility(View.VISIBLE);

        TextView step_3_result_tv = (TextView) findViewById(R.id.step_3_result_tv);
        if(condition.equalsIgnoreCase("arrive at home")) {
            step_3_result_tv.setText(getResources().getString(R.string.create_plan_step_3_message_correct));
        } else {
            step_3_result_tv.setText(getResources().getString(R.string.create_plan_step_3_message_wrong));
        }

        detailed_plan_tv = (TextView) findViewById(R.id.step_3_plan_tv);
        final String plan = getResources().getString(R.string.detailed_plan);

        SpannableStringBuilder spannable = new SpannableStringBuilder(plan);

        spannable.setSpan(
                new BackgroundColorSpan(0x223CB371),
                plan.indexOf(routine),
                plan.indexOf(routine) + String.valueOf(routine).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                plan.indexOf(routine),
                plan.indexOf(routine) + String.valueOf(routine).length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new BackgroundColorSpan(0x22FF2D00),
                plan.indexOf("track my mood"),
                plan.indexOf("track my mood") + String.valueOf("track my mood").length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                plan.indexOf("track my mood"),
                plan.indexOf("track my mood") + String.valueOf("track my mood").length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        detailed_plan_tv.setText(spannable);

        Button buttonFinish = (Button) findViewById(R.id.step_3_btn);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlanData data = new PlanData(new UserData(getApplicationContext()).getUuid(), routine);
                PlanMgr planMgr = new PlanMgr(getApplicationContext());
                planMgr.setPlanGiven();
                planMgr.setPlanRoutineDesc(routine);
                try {
                    planMgr.setPlanDataString(data.toJSONString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                PlanDataTransmission pt = new PlanDataTransmission(getApplicationContext());
                if (!pt.isDataTransmitted()) {
                    pt.transmitData();
                }

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }

}
