package com.adhiwie.moodjournal;

import java.util.ArrayList;
import java.util.Calendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.exception.IncompatibleAPIException;
import com.adhiwie.moodjournal.questionnaire.mood.MoodQuestionnaireActivity;
import com.adhiwie.moodjournal.questionnaire.mood.MoodQuestionnaireMgr;
import com.adhiwie.moodjournal.questionnaire.personality.PersonalityTestActivity;
import com.adhiwie.moodjournal.questionnaire.wellbeing.WellBeingQuestionnaireActivity;
import com.adhiwie.moodjournal.questionnaire.wellbeing.WellBeingQuestionnaireMgr;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.user.permission.Permission;
import com.adhiwie.moodjournal.user.permission.RuntimePermission;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.Popup;
import com.adhiwie.moodjournal.utils.Time;

//consent given true 
//user id static
//registration time: static
//mood_q count static
//daily_q count static

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class MainActivity extends Activity 
{

	private Log log = new Log();

	private final String[] required_permissions = new String[] 
			{
			Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS, 
			Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
		actionbar_title.setText(getResources().getString(R.string.title_activity_main));

		ShimmerFrameLayout container = (ShimmerFrameLayout) findViewById(R.id.shimmer_action_bar);
		container.setBaseAlpha(0.8f);
		container.setAutoStart(true);

		setContentView(R.layout.activity_main);


		//		ShimmerFrameLayout exit_shimmer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
		//		exit_shimmer.setBaseAlpha(0.8f);
		//		exit_shimmer.setAutoStart(true);


		ShimmerFrameLayout mood_shimmer = (ShimmerFrameLayout) findViewById(R.id.mood_shimmer);
		mood_shimmer.setBaseAlpha(0.8f);
		mood_shimmer.setAutoStart(true);

		ShimmerFrameLayout daily_shimmer = (ShimmerFrameLayout) findViewById(R.id.daily_shimmer);
		daily_shimmer.setBaseAlpha(0.8f);
		daily_shimmer.setAutoStart(true);

		if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) ) 
		{
			Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(getApplicationContext()) );
		}

	}


	@Override
	protected void onStart() 
	{
		super.onStart();
		setLayout();
		new GooglePlayServices().isGoogplePlayServiceAvailable(MainActivity.this);
		if(getMissingPermissions().size() > 0)
		{
			askMissingPermissions();
			return;
		}

		check_Consent_GooglePlayService_Permissions_LinkedTask();
	}


	private void setLayout()
	{
		// set today's mood questionnaire status
		ImageView mood1 = (ImageView) findViewById(R.id.mood_q1);
		ImageView mood2 = (ImageView) findViewById(R.id.mood_q2);
		ImageView mood3 = (ImageView) findViewById(R.id.mood_q3);
		ImageView mood4 = (ImageView) findViewById(R.id.mood_q4);
		MoodQuestionnaireMgr mood_mgr = new MoodQuestionnaireMgr(getApplicationContext());
		int todays_count_mood_q = mood_mgr.getMoodQuestionnaireCountForToday();
		switch (todays_count_mood_q) {
		case 0:
			mood1.setImageResource(R.drawable.answer_gray);
			mood2.setImageResource(R.drawable.answer_gray);
			mood3.setImageResource(R.drawable.answer_gray);
			mood4.setImageResource(R.drawable.answer_gray);
			break;
		case 1:
			mood1.setImageResource(R.drawable.answer_blue);
			mood2.setImageResource(R.drawable.answer_gray);
			mood3.setImageResource(R.drawable.answer_gray);
			mood4.setImageResource(R.drawable.answer_gray);
			break;
		case 2:
			mood1.setImageResource(R.drawable.answer_blue);
			mood2.setImageResource(R.drawable.answer_blue);
			mood3.setImageResource(R.drawable.answer_gray);
			mood4.setImageResource(R.drawable.answer_gray);
			break;
		case 3:
			mood1.setImageResource(R.drawable.answer_blue);
			mood2.setImageResource(R.drawable.answer_blue);
			mood3.setImageResource(R.drawable.answer_blue);
			mood4.setImageResource(R.drawable.answer_gray);
			break;
		case 4:
			mood1.setImageResource(R.drawable.answer_blue);
			mood2.setImageResource(R.drawable.answer_blue);
			mood3.setImageResource(R.drawable.answer_blue);
			mood4.setImageResource(R.drawable.answer_blue);
			break;
		default:
			break;
		}


		// set today's daily questionnaire status
		ImageView daily = (ImageView) findViewById(R.id.daily_q1);
		WellBeingQuestionnaireMgr daily_mgr = new WellBeingQuestionnaireMgr(getApplicationContext());
		int todays_count_daily_q = daily_mgr.getDailyQuestionnaireCountForToday();
		switch (todays_count_daily_q) {
		case 0:
			daily.setImageResource(R.drawable.answer_gray);
			break;
		case 1:
			daily.setImageResource(R.drawable.answer_blue);
		default:
			break;
		}


		// set participation status
		ProgressBar pb_days = (ProgressBar) findViewById(R.id.participation_days_progressbar);
		ProgressBar pb_mood = (ProgressBar) findViewById(R.id.mood_questionnaires_progressbar);
		ProgressBar pb_daily = (ProgressBar) findViewById(R.id.daily_questionnaires_progressbar);
		TextView tv_days = (TextView) findViewById(R.id.participation_days_tv);
		TextView tv_mood = (TextView) findViewById(R.id.mood_questionnaires_tv);
		TextView tv_daily = (TextView) findViewById(R.id.daily_questionnaires_tv);

		int start_date = new UserData(getApplicationContext()).getStartDate(); 
		int current_date = new Time(Calendar.getInstance()).getEpochDays();
		int participation_days = 1 + current_date - start_date;
		pb_days.setProgress(participation_days);
		pb_mood.setProgress(mood_mgr.getMoodQuestionnaireCount());
		pb_daily.setProgress(daily_mgr.getDailyQuestionnaireCount());
		tv_days.setText(participation_days + "/50");
		tv_mood.setText(mood_mgr.getMoodQuestionnaireCount() + "/200");
		tv_daily.setText(daily_mgr.getDailyQuestionnaireCount() + "/50");
	}



	private ArrayList<String> getMissingPermissions()
	{
		ArrayList<String> missing_permissions = new ArrayList<String>();
		try 
		{
			RuntimePermission rp = new RuntimePermission(getApplicationContext());
			missing_permissions = rp.getMissingPermissions(required_permissions);
		} 
		catch (IncompatibleAPIException e) {		}
		return missing_permissions;
	}

	private void askMissingPermissions()
	{
		try 
		{
			RuntimePermission rp = new RuntimePermission(getApplicationContext());
			ArrayList<String> missing_permissions = getMissingPermissions();
			if(missing_permissions.size() > 0)
			{
				String[] p = new String[missing_permissions.size()];
				for(int i = 0; i < missing_permissions.size(); i++)
					p[i] = missing_permissions.get(i);
				rp.askPermissions(MainActivity.this, p);
			}
		} 
		catch (IncompatibleAPIException e) 
		{
			log.e(e.toString());
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) 
	{
		ArrayList<String> missing_permissions = getMissingPermissions();
		if(missing_permissions.size() > 0)
		{
			try 
			{
				RuntimePermission rp = new RuntimePermission(getApplicationContext());

				// check if never asked again is ticked
				boolean never_asked_clicked = false;
				for(String s : permissions)
				{
					System.out.println(s);
					System.out.println(rp.isRationalRequired(MainActivity.this, s));
					if(!rp.isRationalRequired(MainActivity.this, s))
					{
						never_asked_clicked = true;
						break;
					}
				}

				// show popup and direct to the settings page
				if(never_asked_clicked)
				{
					rp.showRational(MainActivity.this);
				}
				// ask again
				else
				{
					askMissingPermissions();
				}
			} 
			catch (IncompatibleAPIException e) 
			{
				log.e(e.toString());
			}
		}
		else
		{
			check_Consent_GooglePlayService_Permissions_LinkedTask();
		}
	}




	private void check_Consent_GooglePlayService_Permissions_LinkedTask()
	{
		if(!new ConsentMgr(getApplicationContext()).isConsentGiven())
		{
			startActivity(new Intent(this, ConsentActivity.class));
			this.finish();
			return;
		}

		if(!new GooglePlayServices().isGoogplePlayServiceAvailable(MainActivity.this))
		{
			Toast.makeText(getApplicationContext(), "Error with Google Play Service.", Toast.LENGTH_SHORT).show();
			return;
		}

		Permission p = new Permission(getApplicationContext());
		if(!p.isAccessibilityPermitted())
		{
			p.startAccessibilityServicePermissionActivityIfRequired();
			this.finish();
			return;
		}
		if(!p.isAppAccessPermitted())
		{
			p.startAppUsagePermissionActivityIfRequired();
			this.finish();
			return;
		}
		if(!p.isNSLPermitted())
		{
			p.startNSLPermissionActivityIfRequired();
			this.finish();
			return;
		}

		try{ 
			new LinkedTasks(getApplicationContext()).checkAllExceptPermission(); 
		} 
		catch(Exception e){}
	}







	//menu items
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle presses on the action bar items
		switch (item.getItemId()) 
		{
		case R.id.menu_info:
			String message = 
					"Your unique id is: " + new UserData(getApplicationContext()).getUuid() 
					+ "\n\n "
					+ "My Traces app has been developed by researchers at the University "
					+ "of Birmingham (UoB) and University College London (UCL) to collect data for "
					+ "research and teaching purposes." 
					+ " \n\n "
					+ "We would love to hear your feedback for us and issues with the app. "
					+ "Please send us an email - a.mehrotra@ucl.ac.uk." 
					+ " \n\n "
					+ "Thank You!";
			
			new Popup().showPopup(MainActivity.this, getResources().getString(R.string.info_title), message);
			return true;
		case R.id.menu_personality_test:
			startActivity(new Intent(getApplicationContext(), PersonalityTestActivity.class));
			return true;
			
//		case R.id.menu_error:
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("Type Your Message");
//
//			// Set up the input
//			final EditText input = new EditText(this);
//			builder.setView(input);
//			builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() { 
//			    @Override
//			    public void onClick(DialogInterface dialog, int which) {
//			        if(input.getText() == null || input.getText().toString().length() == 0)
//			        {
//			        	Toast.makeText(getApplicationContext(), "There was no message to be sent.", Toast.LENGTH_SHORT).show();
//			        	return;
//			        }
//
//			        String m = input.getText().toString();
//			        new FileMgr(getApplicationContext()).addData(new ErrorLogData(m));
//			        
//					try 
//					{
//						new DataTransmitterMgr(getApplicationContext()).uploadFileByDataType(new DataTypes().ERROR_LOG);
//						Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_SHORT).show();
//						dialog.cancel();
//					} 
//					catch (Exception e) {
//						new Log().e(e.toString());
//					}
//					
//			    }
//			});
//			builder.show();
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}




	// button click functions
	public void moodTest(View v) 
	{

		MoodQuestionnaireMgr mgr = new MoodQuestionnaireMgr(getApplicationContext());

//		if(mgr.getMoodQuestionnaireCountForToday() > 3)
//		{
//			Toast.makeText(getApplicationContext(), "Today's mood questionnaires are already completed.", Toast.LENGTH_SHORT).show();
//			return;
//		}

		long current_time = Calendar.getInstance().getTimeInMillis();
		long last_time = mgr.getLastMoodQuestionnaireTime();
		int diff_mins = (int) ((current_time - last_time)/(60*1000));
		if(diff_mins < 3*60)
		{
			int mins_left = 3*60 - diff_mins;
			if(mins_left > 59)
			{
				int hours_left = mins_left/60;
				mins_left = mins_left % 60;
				Toast.makeText(getApplicationContext(), "Next mood questionnaire will be available after " + hours_left + " hours " + mins_left + " mins.", Toast.LENGTH_SHORT).show();
			}
			else
				Toast.makeText(getApplicationContext(), "Next mood questionnaire will be available after " + mins_left + " mins.", Toast.LENGTH_SHORT).show();
			return;
		}

		startActivity(new Intent(this, MoodQuestionnaireActivity.class));
	}


	public void dailyTest(View v) 
	{
		if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 16)
		{
			Toast.makeText(getApplicationContext(), "Today's daily questionnaire will be available after 4pm.", Toast.LENGTH_SHORT).show();
			return;
		}

		WellBeingQuestionnaireMgr mgr = new WellBeingQuestionnaireMgr(getApplicationContext());
		if(mgr.getDailyQuestionnaireCountForToday() > 0)
		{
			Toast.makeText(getApplicationContext(), "Today's daily questionnaire is already completed.", Toast.LENGTH_SHORT).show();
			return;
		}

		startActivity(new Intent(this, WellBeingQuestionnaireActivity.class));
	}

	//	public void exit(View v) throws JSONException
	//	{
	//		this.finish();
	//	}



}
