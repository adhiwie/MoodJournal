package com.adhiwie.mymoodjournal.questionnaire.mood;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.adhiwie.mymoodjournal.R;
import com.adhiwie.mymoodjournal.debug.CustomExceptionHandler;
import com.adhiwie.mymoodjournal.file.FileMgr;
import com.adhiwie.mymoodjournal.user.data.UserData;
import com.adhiwie.mymoodjournal.utils.Log;
import com.adhiwie.mymoodjournal.utils.Popup;

import org.json.JSONException;

@SuppressLint("NewApi")
public class MoodQuestionnaireActivity extends AppCompatActivity {

    private long start_time;
    private Resources res;
    private SeekBar sk1;
    private TextView response1;
    private EditText et_notes;
    private ImageView iv_mood_level;
    private int a1;
    private String notes;
    private boolean sk1_thumb;


    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mood_questionnaire);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }

        et_notes = (EditText) findViewById(R.id.et_notes);
        iv_mood_level = (ImageView) findViewById(R.id.iv_mood_level);

        start_time = Calendar.getInstance().getTimeInMillis();
        res = getResources();
        sk1 = (SeekBar) findViewById(R.id.seekbar1);

        response1 = (TextView) findViewById(R.id.response1);

        a1 = 0;

        sk1_thumb = false;

        sk1.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (sk1_thumb == false) {
                    int value = 1 + sk1.getProgress();
                    a1 = value;
                    setResponseLabelSK1(value);
                }
                return false;
            }
        });


        sk1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                int value = 1 + seekBar.getProgress();
                a1 = value;
                setResponseLabelSK1(value);
            }
        });

        //remove notification if present
        NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.cancel(6011);
    }

    private void setResponseLabelSK1(int value) {
        String response = res.getString(R.string.mood_test_question1_option3);
        Drawable mood_url = null;
        switch (value) {
            case 1:
                response = res.getString(R.string.mood_test_question1_option1);
                mood_url = getDrawable(R.drawable.ic_mood_1);
                break;
            case 2:
                response = res.getString(R.string.mood_test_question1_option2);
                mood_url = getDrawable(R.drawable.ic_mood_2);
                break;
            case 3:
                response = res.getString(R.string.mood_test_question1_option3);
                mood_url = getDrawable(R.drawable.ic_mood_3);
                break;
            case 4:
                response = res.getString(R.string.mood_test_question1_option4);
                mood_url = getDrawable(R.drawable.ic_mood_4);
                break;
            case 5:
                response = res.getString(R.string.mood_test_question1_option5);
                mood_url = getDrawable(R.drawable.ic_mood_5);
                break;
            default:
                break;
        }
        response1.setText(response);
        iv_mood_level.setBackground(mood_url);
    }


    public void submit(View v) throws JSONException {
        Popup popup = new Popup();
        if (a1 == 0) {
            popup.showPopup(MoodQuestionnaireActivity.this, "Mood Level", "Entry missing. You cannot proceed without selecting a value.");
            return;
        }

        notes = et_notes.getText().toString();

        long end_time = Calendar.getInstance().getTimeInMillis();
        Log log = new Log();
        log.e("Start time: " + start_time + ", End time: " + end_time + ", Values-- " + a1 + ", " + notes);

        int participation_days = new UserData(getApplicationContext()).getParticipationDays();
        Calendar calendar = Calendar.getInstance();
        Date date=calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String report_time=dateFormat.format(date);


        MoodQuestionnaireData data = new MoodQuestionnaireData(start_time, end_time, a1, notes, participation_days, report_time);
        FileMgr fm = new FileMgr(getApplicationContext());
        fm.addData(data);

        MoodQuestionnaireMgr mgr = new MoodQuestionnaireMgr(getApplicationContext());
        mgr.updateLastMoodQuestionnaireTime();
        mgr.saveDailyMoodReportData(data.toJSONString());

		/*for (int i=1; i<=60; i++){

			Calendar start_time_next = Calendar.getInstance();
			start_time_next.setTimeInMillis(start_time);
			start_time_next.add(Calendar.DAY_OF_MONTH, i); //add a day

			Calendar end_time_next = Calendar.getInstance();
			end_time_next.setTimeInMillis(end_time);
			end_time_next.add(Calendar.DAY_OF_MONTH, i); //add a day
			MoodQuestionnaireData d = new MoodQuestionnaireData(start_time_next.getTimeInMillis(), end_time_next.getTimeInMillis(), randomWithRange(1,5), randomWithRange(1,5), randomWithRange(1,5));


			new Log().e("Start: "+start_time_next.getTime()+" - End: "+end_time_next.getTime());
			jsonArray.put(d.toJSONString());
		}*/


        //startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    int randomWithRange(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

}



