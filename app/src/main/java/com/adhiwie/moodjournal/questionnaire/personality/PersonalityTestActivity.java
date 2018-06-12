package com.adhiwie.moodjournal.questionnaire.personality;

import java.util.Arrays;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.communication.helper.PersonalityTestDataTransmission;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.Popup;
import com.adhiwie.moodjournal.utils.SharedPref;

public class PersonalityTestActivity extends AppCompatActivity
{
	private Toolbar mTopToolbar;
	private final String PERSONALITY_TEST_RESULT = "PERSONALITY_TEST_RESULT";
	private final String PERSONALITY_TEST_ALERT_SHOWN = "PERSONALITY_TEST_ALERT_SHOWN";
	private final String PERSONALITY_TEST_QUESTION_NUMBER = "PERSONALITY_TEST_QUESTION_NUMBER";
	private long start_time;
	private SharedPref sp;
	private String[] questions;
	private int q_num;
	private TextView question;
	private TextView tv_questionnaire_header;
	private Button control_btn;
	private String response = null;
	private int total_questions = 50;
	private final int[] codes = new int[]{1, -2, 3, -4, 5, -1, 2, -3, 4, -5, 1, -2, 3, -4, 5, -1, 2, -3, 4, -5, 1, -2, 3, -4, 5, -1, 2, -3, -4, -5, 1, -2, 3, -4, 5, -1, 2, -3, -4, 5, 1, 2, 3, -4, 5, -1, 2, 3, -4, 5};

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
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
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setDisplayUseLogoEnabled(true);
//		if(Build.VERSION.SDK_INT >= 18)
//			actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
//
//		TextView actionbar_title = (TextView) findViewById(R.id.tvActionBarTitle);
//		actionbar_title.setText(getResources().getString(R.string.title_activity_personality_test));

		sp = new SharedPref(getApplicationContext());
		if(new PersonalityTestMgr(getApplicationContext()).getPersonalityTestStatus() == false)
			setContentView(R.layout.activity_personality_test);
		else
			setContentView(R.layout.activity_personality_test_results);

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
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStart() 
	{
		super.onStart();

		sp = new SharedPref(getApplicationContext());
		if(new PersonalityTestMgr(getApplicationContext()).getPersonalityTestStatus() == false)
		{
			tv_questionnaire_header = (TextView) findViewById(R.id.tv_questionnaire_header);
			control_btn = (Button) findViewById(R.id.control_btn_test);
			questions = getResources().getStringArray(R.array.personality_test_questions);
			q_num = getCurrentQuestionNumber();
			setQuestion(q_num);
		}
		else
			showResults();
	}

	private int getCurrentQuestionNumber()
	{
		int q_num = sp.getInt(PERSONALITY_TEST_QUESTION_NUMBER);
		if(q_num == 0)
		{
			q_num = 1;
			setCurrentQuestionNumber(q_num);
		}
		return q_num;
	}


	private void setCurrentQuestionNumber(int num)
	{
		sp.add(PERSONALITY_TEST_QUESTION_NUMBER, num);
		q_num = num;
	}


	private void setQuestion(int num)
	{
		start_time = Calendar.getInstance().getTimeInMillis();
		tv_questionnaire_header.setText("Question " + num + " of 50");
		question = (TextView) findViewById(R.id.tv_question);
		question.setText(questions[num-1]);

		response = null;
		final RadioGroup rg_options = (RadioGroup) findViewById(R.id.rg_options);
		rg_options.clearCheck();
		rg_options.setOnCheckedChangeListener(new OnCheckedChangeListener() 
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

	public void onControlBtnClick(View v)
	{
		if(response == null)
		{
			Toast.makeText(getApplicationContext(), "Answer the current question to proceed!", Toast.LENGTH_SHORT).show();
			return;
		}

		int q_num = getCurrentQuestionNumber();

		if(q_num == 1 && sp.getBoolean(PERSONALITY_TEST_ALERT_SHOWN) == false)
		{
			String title = "Important";
			String message = getResources().getString(R.string.personality_test_info_string);
			Popup popup = new Popup();
			popup.showPopup(PersonalityTestActivity.this, title, message);
			sp.add(PERSONALITY_TEST_ALERT_SHOWN, true);
			return;
		}

		saveResponse(q_num, response);

		if(q_num < total_questions - 1)
		{
			q_num = q_num + 1;
			setCurrentQuestionNumber(q_num);
			setQuestion(q_num);
			control_btn.setText("Next");
		}
		else if(q_num == total_questions - 1)
		{
			q_num = q_num + 1;
			setCurrentQuestionNumber(q_num);
			setQuestion(q_num);
			control_btn.setText("See results");
		}
		else
		{
			computeAndSaveResult();
			setCurrentQuestionNumber(0);
			new PersonalityTestMgr(getApplicationContext()).personalityTestCompleted();


			setContentView(R.layout.activity_personality_test_results);
			showResults();
		}
	}






	private void saveResponse(int q_num, String response) 
	{
		try
		{
			long current_time = Calendar.getInstance().getTimeInMillis();
			JSONObject jo = new JSONObject();
			jo.put("question", questions[q_num-1]);
			jo.put("trait_code", getTraitCode(q_num));
			jo.put("score", getScore(q_num, response));
			jo.put("time_taken", current_time - start_time);

			new PersonalityTestMgr(getApplicationContext()).storePersonalityTestResponse(q_num, jo.toString());
			new Log().e("q num: " + q_num + ", response data: " + jo.toString());
		}
		catch(JSONException e)
		{
			new Log().e(e.toString());
		}
	}


	private int getTraitCode(int q_num)
	{
		new Log().e("Q_num: " + q_num);
		int Extraversion = 1;
		int Agreeableness = 2;
		int Conscientiousness = 3;
		int Neuroticism = 4;
		int Openness = 5;

		int code = codes[q_num-1];
		switch (Math.abs(code)) 
		{
		case 1:
			return Extraversion;
		case 2:
			return Agreeableness;
		case 3:
			return Conscientiousness;
		case 4:
			return Neuroticism;
		case 5:
			return Openness;

		default:
			throw new NullPointerException();
		}
	}


	private int getScore(int q_num, String response)
	{
		new Log().e("Q_num: " + q_num + ", Response: " + response);
		int code = codes[q_num-1];
		Resources res = getResources();

		if(code < 0)
		{
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
		else
		{
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


	private void computeAndSaveResult()
	{
		int[] scores = new int[5];
		Arrays.fill(scores, 0);
		PersonalityTestMgr pm = new PersonalityTestMgr(getApplicationContext());
		for(int i = 1; i <= 50; i++)
		{
			try
			{
				String s = pm.getPersonalityTestResponse(i);
				JSONObject jo = new JSONObject(s);
				int code = jo.getInt("trait_code");
				int score = jo.getInt("score");
				switch (code) 
				{
				case 1:
					scores[0] += score;
					break;
				case 2:
					scores[1] += score;
					break;
				case 3:
					scores[2] += score;
					break;
				case 4:
					scores[3] += score;
					break;
				case 5:
					scores[4] += score;
					break;

				default:
					break;
				}
			}
			catch(JSONException e)
			{
				new Log().e(e.toString());
			}
		}

		JSONArray ja = new JSONArray();
		for(int i = 0; i < 5; i++)
			ja.put(scores[i]);
		sp.add(PERSONALITY_TEST_RESULT, ja.toString());
	}

	private void showResults()
	{
		computeAndSaveResult();
		PersonalityTestDataTransmission pt = new PersonalityTestDataTransmission(getApplicationContext());
		if(! pt.isDataTransmitted())
		{
			pt.transmitData();
		}

		int[] scores = new int[5];
		Arrays.fill(scores, 0);
		try 
		{
			String s = sp.getString(PERSONALITY_TEST_RESULT);
			new Log().e("PERSONALITY_TEST_RESULT: " + s);
			JSONArray ja = new JSONArray( s );
			for(int i = 0; i < 5; i++)
			{
				scores[i] = ja.getInt(i);
			}
		} 
		catch (JSONException e) 
		{
			new Log().e(e.toString());
		}


		int Extraversion = 1;
		int Agreeableness = 2;
		int Conscientiousness = 3;
		int Neuroticism = 4;
		int Openness = 5;

		TextView tv_score_extraversion = (TextView) findViewById(R.id.tv_score_extraversion);
		TextView tv_score_agreeableness = (TextView) findViewById(R.id.tv_score_agreeableness);
		TextView tv_score_conscientiousness = (TextView) findViewById(R.id.tv_score_conscientiousness);
		TextView tv_score_neuroticism = (TextView) findViewById(R.id.tv_score_neuroticism);
		TextView tv_score_openness = (TextView) findViewById(R.id.tv_score_openness);

		tv_score_extraversion.setText("" + scores[Extraversion - 1]);
		tv_score_agreeableness.setText("" + scores[Agreeableness - 1]);
		tv_score_conscientiousness.setText("" + scores[Conscientiousness - 1]);
		tv_score_neuroticism.setText("" + scores[Neuroticism - 1]);
		tv_score_openness.setText("" + scores[Openness - 1]);

		Button done = (Button) findViewById(R.id.done_btn_personality_test_results);
		done.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				finish();
			}
		});
	}

}
