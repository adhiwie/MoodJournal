package com.adhiwie.moodjournal.questionnaire.mood;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.file.FileMgr;
import com.adhiwie.moodjournal.plan.PlanMgr;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReminderActivity extends AppCompatActivity {

    private final String REMINDER_OPENED_FOR_TODAY = "REMINDER_OPENED_FOR_TODAY";
    private final String REMINDER_DATE_FOR_TODAY = "REMINDER_DATE_FOR_TODAY";

    private Context context;
    private SharedPref sp;
    private long currentTimeInMillis;
    private String currentTimeInString;
    private long responseTimeInMillis;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true);
            setShowWhenLocked(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                                 WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                                 WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }

        this.context = getApplicationContext();
        this.sp = new SharedPref(context);
        currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        currentTimeInString = simpleDateFormat.format(currentTime);

        final String routine = new PlanMgr(getApplicationContext()).getPlanRoutineDesc();

        TextView detailed_plan_tv = (TextView) findViewById(R.id.step_1_plan_tv);
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
                new BackgroundColorSpan(0x22FF0000),
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


        Button buttonContinue = (Button) findViewById(R.id.done_btn);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current_date = new Time(Calendar.getInstance()).getEpochDays();
                responseTimeInMillis = Calendar.getInstance().getTimeInMillis();
                sp.add(REMINDER_DATE_FOR_TODAY, current_date);
                sp.add(REMINDER_OPENED_FOR_TODAY, true);

                ReminderData data = new ReminderData(currentTimeInMillis, currentTimeInString, responseTimeInMillis);
                FileMgr fm = new FileMgr(context);
                fm.addData(data);
                finish();
            }
        });
    }
}
