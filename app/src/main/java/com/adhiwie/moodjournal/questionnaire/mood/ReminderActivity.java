package com.adhiwie.moodjournal.questionnaire.mood;

import android.content.Context;
import android.content.Intent;
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
    }

    public void noBtnOnClick(View view) {
        finish();
    }

    public void yesBtnOnClick(View view) {
        startActivity(new Intent(getApplicationContext(), ReinforcementActivity.class));
        finish();
    }
}
