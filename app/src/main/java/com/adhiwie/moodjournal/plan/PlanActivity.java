package com.adhiwie.moodjournal.plan;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.communication.helper.PlanDataTransmission;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Toast;

import org.json.JSONException;

public class PlanActivity extends AppCompatActivity {

    private int step;
    private String[] routines;
    private String routine;
    private SharedPref sp;
    private Button control_btn;
    private ScrollView intro_layout;
    private LinearLayout step_1_layout;
    private RelativeLayout step_2_layout;
    private RelativeLayout step_3_layout;
    private ArrayAdapter<String> list_adapter;
    private TextView detailed_plan_tv;

    private Toolbar mTopToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_plan);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (getIntent().getIntExtra("step", 0) == 0) {
            initLayout();
        }

        if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) )
        {
            Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(getApplicationContext()) );
        }

        sp = new SharedPref(getApplicationContext());

        if(!new PlanMgr(getApplicationContext()).isPlanGiven())
        {
            initLayout();
        }
    }

    private void initLayout(){
        step = 0;

        intro_layout = (ScrollView) findViewById(R.id.intro_layout);
        step_1_layout = (LinearLayout) findViewById(R.id.step_1_layout);
        step_2_layout = (RelativeLayout) findViewById(R.id.step_2_layout);
        step_3_layout = (RelativeLayout) findViewById(R.id.step_3_layout);
        control_btn = (Button) findViewById(R.id.create_plan);

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
                            android.widget.Toast.makeText(getApplicationContext(), "You need to set a plan to use this app!", android.widget.Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        initLayout();
                        break;
                    case 2:
                        goToStepOne(2);
                        break;
                    case 3:
                        goToStepTwo(routine);
                        break;

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCreateButtonClick(View v) {
        goToStepOne(2);
    }

    private void goToStepOne(int time_mode) {
        step = 1;

        intro_layout.setVisibility(View.GONE);
        step_1_layout.setVisibility(View.VISIBLE);
        step_2_layout.setVisibility(View.GONE);
        step_3_layout.setVisibility(View.GONE);

        switch (time_mode) {
            case 0:
                routines = getResources().getStringArray(R.array.morning_routines);
                break;
            case 1:
                routines = getResources().getStringArray(R.array.lunch_routines);
                break;
            case 2:
                routines = getResources().getStringArray(R.array.evening_routines);
                break;

        }

        list_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, routines);
        final ListView routine_list = (ListView) findViewById(R.id.routine_list);
        routine_list.setAdapter(list_adapter);
        routine_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                goToStepTwo(routine_list.getItemAtPosition(position).toString().toLowerCase());
            }
        });
    }

    private void goToStepTwo(final String routine) {
        this.routine = routine;
        step = 2;

        intro_layout.setVisibility(View.GONE);
        step_1_layout.setVisibility(View.GONE);
        step_2_layout.setVisibility(View.VISIBLE);
        step_3_layout.setVisibility(View.GONE);

        detailed_plan_tv = (TextView) findViewById(R.id.step_2_plan_tv);
        final String plan = getResources().getString(R.string.detailed_plan, routine);

        SpannableStringBuilder spannable = new SpannableStringBuilder(plan);

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

        detailed_plan_tv.setText(spannable);

        Button buttonContinue = (Button) findViewById(R.id.step_2_btn);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToStepThree(routine);
            }
        });

    }

    private void goToStepThree(final String routine) {
        step = 3;

        intro_layout.setVisibility(View.GONE);
        step_1_layout.setVisibility(View.GONE);
        step_2_layout.setVisibility(View.GONE);
        step_3_layout.setVisibility(View.VISIBLE);

        detailed_plan_tv = (TextView) findViewById(R.id.step_3_plan_tv);
        final String plan = getResources().getString(R.string.detailed_plan, routine);

        SpannableStringBuilder spannable = new SpannableStringBuilder(plan);

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

        detailed_plan_tv.setText(spannable);

        Button buttonContinue = (Button) findViewById(R.id.step_3_btn);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
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
                if(! pt.isDataTransmitted()) {
                    pt.transmitData();
                }

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

}
