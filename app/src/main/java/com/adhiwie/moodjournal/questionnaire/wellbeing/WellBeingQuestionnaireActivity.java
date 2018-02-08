package com.adhiwie.moodjournal.questionnaire.wellbeing;

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
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.file.FileMgr;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.Popup;

public class WellBeingQuestionnaireActivity extends Activity {

	private long start_time;
	private String[] questions;

	private TextView question;
	private TextView tv_questionnaire_counter;
	private Button control_btn;
	private int response = 0;
	private int total_questions = 8;


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
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		Drawable background;

		if(Build.VERSION.SDK_INT >= 21)
			background = getResources().getDrawable(R.drawable.blue_background, null);
		else
			background = getResources().getDrawable(R.drawable.blue_background);


		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(background);
		actionBar.setCustomView(R.layout.actionbar_layout);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayUseLogoEnabled(true);

		TextView actionbar_title = (TextView) findViewById(R.id.tvActionBarTitle);
		actionbar_title.setText(getResources().getString(R.string.title_activity_wellbeing_questionnaire));
		
		setContentView(R.layout.activity_phq9_questionnaire);
		
		if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) ) 
		{
			Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(getApplicationContext()) );
		}

		a1 = 0;
		a2 = 0;
		a3 = 0;
		a4 = 0;
		a5 = 0;
		a6 = 0;
		a7 = 0;
		a8 = 0;

		start_time = Calendar.getInstance().getTimeInMillis();
		tv_questionnaire_counter = (TextView) findViewById(R.id.tv_questionnaire_counter);
		control_btn = (Button) findViewById(R.id.control_btn_phq_test);
		questions = getResources().getStringArray(R.array.phq_test_questions);
		setQuestion();
		

		//remove notification if present
		NotificationManager mgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		mgr.cancel(5011);
	}



	private int getCurrentQuestionNumber()
	{
		if(a1 == 0)
			return 1;
		else if(a2 == 0)
			return 2;
		else if(a3 == 0)
			return 3;
		else if(a4 == 0)
			return 4;
		else if(a5 == 0)
			return 5;
		else if(a6 == 0)
			return 6;
		else if(a7 == 0)
			return 7;
		else
			return 8;
	}


	private void setQuestion()
	{
		int q_num = getCurrentQuestionNumber();
		tv_questionnaire_counter.setText("Question " + q_num + " of 8");
		question = (TextView) findViewById(R.id.tv_question);
		question.setText(questions[q_num-1]);

		response = 0;
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
				{
					String s = rb.getText().toString();
					Resources res = getResources();
					if(s.equals(res.getString(R.string.phq_test_option_not_at_all)))
						response = 1;
					else if(s.equals(res.getString(R.string.phq_test_option_several_days)))
						response = 2;
					else if(s.equals(res.getString(R.string.phq_test_option_more_than_half_days)))
						response = 3;
					else if(s.equals(res.getString(R.string.phq_test_option_nearly_every_day)))
						response = 4;
				}
				else
					response = 0;
			}
		}); 
	}

	public void onControlBtnClick(View v)
	{
		if(response == 0)
		{
			Popup p = new Popup();
			p.showPopup(WellBeingQuestionnaireActivity.this, "Entry missing!", "Answer the current question to proceed.");
			return;
		}

		int q_num = getCurrentQuestionNumber();
		new Log().v("Question: " + q_num);
		
		setResponse(q_num, response);

		if(q_num <  total_questions-1)
		{
			setQuestion();
			control_btn.setText("Next");
		}
		else if(q_num ==  total_questions-1)
		{
			setQuestion();
			control_btn.setText("Submit");
		}
		else
		{
			long end_time = Calendar.getInstance().getTimeInMillis();
			WellBeingQuestionnaireData qd = new WellBeingQuestionnaireData(start_time, end_time, 
					a1, a2, a3, a4, a5, a6, a7, a8);
			
			FileMgr fm = new FileMgr(getApplicationContext());
			fm.addData(qd);
			new WellBeingQuestionnaireMgr(getApplicationContext()).updateLastDailyQuestionnaireDate();
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}






	private void setResponse(int q_num, int value) 
	{
		switch (q_num) 
		{
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
