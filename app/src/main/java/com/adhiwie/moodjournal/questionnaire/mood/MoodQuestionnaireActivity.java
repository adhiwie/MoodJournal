package com.adhiwie.moodjournal.questionnaire.mood;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.file.FileMgr;
import com.adhiwie.moodjournal.utils.DataTypes;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.Popup;
import com.adhiwie.moodjournal.utils.SharedPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("NewApi")
public class MoodQuestionnaireActivity extends AppCompatActivity {

	private long start_time;
	private Resources res;
	private SeekBar sk1;
	private SeekBar sk2;
	private SeekBar sk3;
	private TextView response1;
	private TextView response2;
	private TextView response3;
	private int a1;
	private int a2;
	private int a3;

	private boolean sk1_thumb;
	private boolean sk2_thumb;
	private boolean sk3_thumb;

	private Toolbar mTopToolbar;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		Drawable background;
//
//		if(Build.VERSION.SDK_INT >= 21)
//			background = getResources().getDrawable(R.drawable.blue_background, null);
//		else
//			background = getResources().getDrawable(R.drawable.blue_background);
//
//
//		ActionBar actionBar = getActionBar();
//		actionBar.setBackgroundDrawable(background);
//		actionBar.setCustomView(R.layout.actionbar_layout);
//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
//		actionBar.setDisplayHomeAsUpEnabled(false);
//		actionBar.setDisplayUseLogoEnabled(true);
//
//		TextView actionbar_title = (TextView) findViewById(R.id.tvActionBarTitle);
//		actionbar_title.setText(getResources().getString(R.string.title_activity_mood_questionnaire));
		
		setContentView(R.layout.activity_mood_questionnaire);

		mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mTopToolbar);
		
		if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) ) 
		{
			Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(getApplicationContext()) );
		}
		
		start_time = Calendar.getInstance().getTimeInMillis();
		res = getResources();
		sk1 = (SeekBar) findViewById(R.id.seekbar1);
		sk2 = (SeekBar) findViewById(R.id.seekbar2);
		sk3 = (SeekBar) findViewById(R.id.seekbar3);
		
		response1 = (TextView) findViewById(R.id.response1);
		response2 = (TextView) findViewById(R.id.response2);
		response3 = (TextView) findViewById(R.id.response3);

		a1 = 0;
		a2 = 0;
		a3 = 0;

		sk1_thumb = false;
		sk2_thumb = false;
		sk3_thumb = false;
		
		sk1.setOnTouchListener(new OnTouchListener() 
		{	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(sk1_thumb == false)
				{
					int value = 1+sk1.getProgress();
					a1 = value;
					setResponseLabelSK1(value);
				}
				return false;
			}
		});
		
		sk2.setOnTouchListener(new OnTouchListener() 
		{	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(sk2_thumb == false)
				{
					int value = 1+sk2.getProgress();
					a2 = value;
					setResponseLabelSK2(value);
				}
				return false;
			}
		});
		
		sk3.setOnTouchListener(new OnTouchListener() 
		{	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(sk3_thumb == false)
				{
					int value = 1+sk3.getProgress();
					a3 = value;
					setResponseLabelSK3(value);
				}
				return false;
			}
		});
		
		sk1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
		{
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) 
			{
				int value = 1+seekBar.getProgress();
				a1 = value;
				setResponseLabelSK1(value);
			}
		});
		
		sk2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
		{
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) 
			{
				int value = 1+seekBar.getProgress();
				a2 = value;
				setResponseLabelSK2(value);
			}
		});
		
		sk3.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
		{
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) 
			{
				int value = 1+seekBar.getProgress();
				a3 = value;
				setResponseLabelSK3(value);
			}
		});
		
		
		//remove notification if present
		NotificationManager mgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		mgr.cancel(6011);
	}

	private void setResponseLabelSK1(int value)
	{
		String response = res.getString(R.string.mood_test_question1_option3);
		switch (value) {
		case 1:
			response = res.getString(R.string.mood_test_question1_option1);
			break;
		case 2:
			response = res.getString(R.string.mood_test_question1_option2);
			break;
		case 3:
			response = res.getString(R.string.mood_test_question1_option3);
			break;
		case 4:
			response = res.getString(R.string.mood_test_question1_option4);
			break;
		case 5:
			response = res.getString(R.string.mood_test_question1_option5);
			break;
		default:
			break;
		}
		response1.setText(response);
	}

	private void setResponseLabelSK2(int value)
	{
		String response = res.getString(R.string.mood_test_question2_option3);
		switch (value) {
		case 1:
			response = res.getString(R.string.mood_test_question2_option1);
			break;
		case 2:
			response = res.getString(R.string.mood_test_question2_option2);
			break;
		case 3:
			response = res.getString(R.string.mood_test_question2_option3);
			break;
		case 4:
			response = res.getString(R.string.mood_test_question2_option4);
			break;
		case 5:
			response = res.getString(R.string.mood_test_question2_option5);
			break;
		default:
			break;
		}
		response2.setText(response);
	}

	private void setResponseLabelSK3(int value)
	{
		String response = res.getString(R.string.mood_test_question3_option3);
		switch (value) {
		case 1:
			response = res.getString(R.string.mood_test_question3_option1);
			break;
		case 2:
			response = res.getString(R.string.mood_test_question3_option2);
			break;
		case 3:
			response = res.getString(R.string.mood_test_question3_option3);
			break;
		case 4:
			response = res.getString(R.string.mood_test_question3_option4);
			break;
		case 5:
			response = res.getString(R.string.mood_test_question3_option5);
			break;
		default:
			break;
		}
		response3.setText(response);
	}
	
	
	public void submit(View v) throws JSONException {
		Popup popup = new Popup();
		if(a1 == 0)
		{
			popup.showPopup(MoodQuestionnaireActivity.this, "Stress Level", "Entry missing. You cannot proceed without selecting a value.");
			return;
		}
		if(a2 == 0)
		{
			popup.showPopup(MoodQuestionnaireActivity.this, "Activeness Level", "Entry missing. You cannot proceed without selecting a value.");
			return;
		}
		if(a3 == 0)
		{
			popup.showPopup(MoodQuestionnaireActivity.this, "Happiness Level", "Entry missing. You cannot proceed without selecting a value.");
			return;
		}
		
		long end_time = Calendar.getInstance().getTimeInMillis();
		Log log = new Log();
		log.e("Start time: " + start_time + ", End time: " + end_time +", Values-- " + a1 + ", " + a2 + ", " + a3);

		MoodQuestionnaireData data = new MoodQuestionnaireData(start_time, end_time, a1, a2, a3);
		FileMgr fm = new FileMgr(getApplicationContext());
		fm.addData(data);

		MoodQuestionnaireMgr mgr = new MoodQuestionnaireMgr(getApplicationContext());
		mgr.updateLastMoodQuestionnaireTime();
		mgr.saveDailyMoodReportData(data.toJSONString());

		SharedPref sharedPref = new SharedPref(getApplicationContext());
		String moodData = sharedPref.getString("DAILY_MOOD_REPORT");

		JSONArray jsonArray;

		if (moodData == null) {
			jsonArray = new JSONArray();
		} else {
			jsonArray = new JSONArray(moodData);
		}

		jsonArray.put(data.toJSONString());
		//sharedPref.add("DAILY_MOOD_REPORT", jsonArray.toString());

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

		sharedPref.add("DAILY_MOOD_REPORT", jsonArray.toString());

		new Log().e(sharedPref.getString("DAILY_MOOD_REPORT"));


		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	int randomWithRange(int min, int max)
	{
		int range = (max - min) + 1;
		return (int)(Math.random() * range) + min;
	}
	
}



