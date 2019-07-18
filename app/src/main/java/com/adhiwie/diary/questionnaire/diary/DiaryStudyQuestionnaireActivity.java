package com.adhiwie.diary.questionnaire.diary;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.adhiwie.diary.R;
import com.adhiwie.diary.debug.CustomExceptionHandler;
import com.adhiwie.diary.file.FileMgr;
import com.adhiwie.diary.user.data.UserData;
import com.adhiwie.diary.utils.Log;
import com.adhiwie.diary.utils.Popup;

import org.json.JSONException;

@SuppressLint("NewApi")
public class DiaryStudyQuestionnaireActivity extends AppCompatActivity {

    private long start_time;
    private Resources res;
    private SeekBar sk1;
    private SeekBar sk2;
    private TextView response1;
    private TextView response2;
    private EditText et_tasks_done;
    private EditText et_time_spent;
    private EditText et_tasks_not_done;
    private int a1;
    private int a2;
    private String tasks_done;
    private String time_spent;
    private String tasks_not_done;

    private boolean sk1_thumb;
    private boolean sk2_thumb;


    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_diary_study);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }

        start_time = Calendar.getInstance().getTimeInMillis();
        res = getResources();
        sk1 = (SeekBar) findViewById(R.id.seekbar1);
        sk2 = (SeekBar) findViewById(R.id.seekbar2);

        response1 = (TextView) findViewById(R.id.response1);
        response2 = (TextView) findViewById(R.id.response2);

        et_tasks_done = (EditText) findViewById(R.id.et_tasks_done);
        et_time_spent = (EditText) findViewById(R.id.et_time_spent);
        et_tasks_not_done = (EditText) findViewById(R.id.et_tasks_not_done);

        a1 = 0;
        a2 = 0;

        tasks_done = "";
        time_spent = "";
        tasks_not_done = "";

        sk1_thumb = false;
        sk2_thumb = false;

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

        sk2.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (sk2_thumb == false) {
                    int value = 1 + sk2.getProgress();
                    a2 = value;
                    setResponseLabelSK2(value);
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

        sk2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
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
                a2 = value;
                setResponseLabelSK2(value);
            }
        });


        //remove notification if present
        NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.cancel(6011);
    }

    private void setResponseLabelSK1(int value) {
        String response = res.getString(R.string.diary_test_question1_option4);
        switch (value) {
            case 1:
                response = res.getString(R.string.diary_test_question1_option1);
                break;
            case 2:
                response = res.getString(R.string.diary_test_question1_option2);
                break;
            case 3:
                response = res.getString(R.string.diary_test_question1_option3);
                break;
            case 4:
                response = res.getString(R.string.diary_test_question1_option4);
                break;
            case 5:
                response = res.getString(R.string.diary_test_question1_option5);
                break;
            case 6:
                response = res.getString(R.string.diary_test_question1_option6);
                break;
            case 7:
                response = res.getString(R.string.diary_test_question1_option7);
                break;
            default:
                break;
        }
        response1.setText(response);
    }

    private void setResponseLabelSK2(int value) {
        String response = res.getString(R.string.diary_test_question2_option4);
        switch (value) {
            case 1:
                response = res.getString(R.string.diary_test_question2_option1);
                break;
            case 2:
                response = res.getString(R.string.diary_test_question2_option2);
                break;
            case 3:
                response = res.getString(R.string.diary_test_question2_option3);
                break;
            case 4:
                response = res.getString(R.string.diary_test_question2_option4);
                break;
            case 5:
                response = res.getString(R.string.diary_test_question2_option5);
                break;
            case 6:
                response = res.getString(R.string.diary_test_question2_option6);
                break;
            case 7:
                response = res.getString(R.string.diary_test_question2_option7);
                break;
            default:
                break;
        }
        response2.setText(response);
    }


    public void submit(View v) throws JSONException {
        Popup popup = new Popup();
        if (a1 == 0) {
            popup.showPopup(DiaryStudyQuestionnaireActivity.this, "Quality of work", "Entry missing. You cannot proceed without selecting a value.");
            return;
        }
        if (a2 == 0) {
            popup.showPopup(DiaryStudyQuestionnaireActivity.this, "Productivity", "Entry missing. You cannot proceed without selecting a value.");
            return;
        }

        tasks_done = et_tasks_done.getText().toString();
        time_spent = et_time_spent.getText().toString();
        tasks_not_done = et_tasks_not_done.getText().toString();



        long end_time = Calendar.getInstance().getTimeInMillis();
        Log log = new Log();
        log.e("Start time: " + start_time + ", End time: " + end_time + ", Values-- " + a1 + ", " + a2 + ", " +
                tasks_done + ", " + time_spent + ", " + tasks_not_done);

        int participation_days = new UserData(getApplicationContext()).getParticipationDays();
        Calendar calendar = Calendar.getInstance();
        Date date=calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String report_time=dateFormat.format(date);


        DiaryStudyQuestionnaireData data = new DiaryStudyQuestionnaireData(start_time, end_time, a1, a2, tasks_done, time_spent, tasks_not_done, participation_days, report_time);
        FileMgr fm = new FileMgr(getApplicationContext());
        fm.addData(data);

        DiaryStudyQuestionnaireMgr mgr = new DiaryStudyQuestionnaireMgr(getApplicationContext());
        mgr.updateLastDiaryStudyQuestionnaireTime();
        mgr.saveDailyDiaryStudyReportData(data.toJSONString());

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



