package com.adhiwie.moodjournal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.exception.IncompatibleAPIException;
import com.adhiwie.moodjournal.plan.PlanActivity;
import com.adhiwie.moodjournal.plan.PlanMgr;
import com.adhiwie.moodjournal.questionnaire.mood.MoodQuestionnaireActivity;
import com.adhiwie.moodjournal.questionnaire.mood.MoodQuestionnaireMgr;
import com.adhiwie.moodjournal.questionnaire.goalcommitment.GCSActivity;
import com.adhiwie.moodjournal.questionnaire.goalcommitment.GCSMgr;
import com.adhiwie.moodjournal.report.MoodReportActivity;
import com.adhiwie.moodjournal.user.permission.Permission;
import com.adhiwie.moodjournal.user.permission.RuntimePermission;
import com.adhiwie.moodjournal.utils.Log;

import java.util.ArrayList;
import java.util.Calendar;

//consent given true 
//user id static
//registration time: static
//mood_q count static
//daily_q count static

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity {

    private Log log = new Log();
    private Toolbar mTopToolbar;
    private RelativeLayout runtimePermissionLayout;

    private final String[] required_permissions = new String[]
            {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//		mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
//		setSupportActionBar(mTopToolbar);
//		getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        setLayout();

        if (!new ConsentMgr(getApplicationContext()).isConsentGiven()) {
            startActivity(new Intent(this, ConsentActivity.class));
            this.finish();
            return;
        }

        new GooglePlayServices().isGooglePlayServiceAvailable(MainActivity.this);
        runtimePermissionLayout = (RelativeLayout) findViewById(R.id.runtime_permission_layout);

        if (getMissingPermissions().size() > 0) {
            runtimePermissionLayout.setVisibility(View.VISIBLE);
        } else {
            runtimePermissionLayout.setVisibility(View.GONE);
            check_Consent_GooglePlayService_Permissions_LinkedTask();
        }

    }


    private void setLayout() {
        // set today's mood questionnaire status
        TextView todaysMoodStatus = (TextView) findViewById(R.id.todays_mood_status_tv);
        Button trackMoodButton = (Button) findViewById(R.id.track_mood_btn);
        ImageView imageStatus = (ImageView) findViewById(R.id.image_status);

        MoodQuestionnaireMgr mood_mgr = new MoodQuestionnaireMgr(getApplicationContext());
        int todays_count_mood_q = mood_mgr.getMoodQuestionnaireCountForToday();
        switch (todays_count_mood_q) {
            case 0:
                todaysMoodStatus.setText(getResources().getString(R.string.todays_mood_q_stats_empty_state));
                trackMoodButton.setVisibility(View.VISIBLE);
                imageStatus.setImageResource(R.drawable.ic_suffer);
                break;
            case 1:
                todaysMoodStatus.setText(getResources().getString(R.string.todays_mood_q_stats_tracked));
                trackMoodButton.setVisibility(View.GONE);
                imageStatus.setImageResource(R.drawable.ic_smile);
                break;
            default:
                break;
        }

        planSetup();

/*
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
*/

        // set participation status
//		ProgressBar pb_days = (ProgressBar) findViewById(R.id.participation_days_progressbar);
//		ProgressBar pb_mood = (ProgressBar) findViewById(R.id.mood_questionnaires_progressbar);
//		//ProgressBar pb_daily = (ProgressBar) findViewById(R.id.daily_questionnaires_progressbar);
//		TextView tv_days = (TextView) findViewById(R.id.participation_days_tv);
//		TextView tv_mood = (TextView) findViewById(R.id.mood_questionnaires_tv);
//		//TextView tv_daily = (TextView) findViewById(R.id.daily_questionnaires_tv);
//
//		int start_date = new UserData(getApplicationContext()).getStartDate();
//		int current_date = new Time(Calendar.getInstance()).getEpochDays();
//		int participation_days = 1 + current_date - start_date;
//		pb_days.setProgress(participation_days);
//		pb_mood.setProgress(mood_mgr.getMoodQuestionnaireCount());
//		//pb_daily.setProgress(daily_mgr.getDailyQuestionnaireCount());
//		tv_days.setText(participation_days + "/30");
//		tv_mood.setText(mood_mgr.getMoodQuestionnaireCount() + "/30");
//		//tv_daily.setText(daily_mgr.getDailyQuestionnaireCount() + "/50");
    }


    private ArrayList<String> getMissingPermissions() {
        ArrayList<String> missing_permissions = new ArrayList<String>();
        try {
            RuntimePermission rp = new RuntimePermission(getApplicationContext());
            missing_permissions = rp.getMissingPermissions(required_permissions);
        } catch (IncompatibleAPIException e) {
        }

        new Log().e("Missing permissions : " + missing_permissions.size());
        return missing_permissions;
    }

    private void askMissingPermissions() {
        try {
            RuntimePermission rp = new RuntimePermission(getApplicationContext());
            ArrayList<String> missing_permissions = getMissingPermissions();
            if (missing_permissions.size() > 0) {
                String[] p = new String[missing_permissions.size()];
                for (int i = 0; i < missing_permissions.size(); i++)
                    p[i] = missing_permissions.get(i);
                rp.askPermissions(MainActivity.this, p);
            }
        } catch (IncompatibleAPIException e) {
            log.e(e.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        ArrayList<String> missing_permissions = getMissingPermissions();
        if (missing_permissions.size() > 0) {
            try {
                RuntimePermission rp = new RuntimePermission(getApplicationContext());

                // check if never asked again is ticked
                boolean never_asked_clicked = false;
                for (String s : permissions) {
                    System.out.println(s);
                    System.out.println(rp.isRationalRequired(MainActivity.this, s));
                    if (!rp.isRationalRequired(MainActivity.this, s)) {
                        never_asked_clicked = true;
                        break;
                    }
                }

                // show popup and direct to the settings page
                if (never_asked_clicked) {
                    rp.showRational(MainActivity.this);
                }
                // ask again
                else {
                    askMissingPermissions();
                }
            } catch (IncompatibleAPIException e) {
                log.e(e.toString());
            }
        } else {
            new Log().e("check_Consent_GooglePlayService_Permissions_LinkedTask is called from onRequestPermissionsResult");
            runtimePermissionLayout.setVisibility(View.GONE);
            check_Consent_GooglePlayService_Permissions_LinkedTask();
        }
    }


    private void check_Consent_GooglePlayService_Permissions_LinkedTask() {

        new Log().e("check_Consent_GooglePlayService_Permissions_LinkedTask is called");

        if (!new GooglePlayServices().isGooglePlayServiceAvailable(MainActivity.this)) {
            Toast.makeText(getApplicationContext(), "Error with Google Play Service.", Toast.LENGTH_SHORT).show();
            return;
        }


        Permission p = new Permission(getApplicationContext());
//		if(!p.isAccessibilityPermitted())
//		{
//			p.startAccessibilityServicePermissionActivityIfRequired();
//			this.finish();
//			return;
//		}
//		if(!p.isAppAccessPermitted())
//		{
//			p.startAppUsagePermissionActivityIfRequired();
//			this.finish();
//			return;
//		}
        if (!p.isNSLPermitted()) {
            p.startNSLPermissionActivityIfRequired();
            this.finish();
            return;
        }

        if (!new PlanMgr(getApplicationContext()).isPlanGiven()) {
            startActivity(new Intent(this, PlanActivity.class));
            this.finish();
            return;
        }

        if (!new GCSMgr(getApplicationContext()).isGCSDone()) {
            startActivity(new Intent(this, GCSActivity.class));
            this.finish();
            return;
        }

        try {
            new LinkedTasks(getApplicationContext()).checkAllExceptPermission();
        } catch (Exception e) {
        }

    }


    //menu items
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		getMenuInflater().inflate(R.menu.main_menu, menu);
//		return true;
//	}


//	@Override
//	public boolean onOptionsItemSelected(MenuItem item)
//	{
//		// Handle presses on the action bar items
//		switch (item.getItemId())
//		{
//		case R.id.menu_info:
//			String message =
//					"Your unique id is: " + new UserData(getApplicationContext()).getUuid()
//					+ "\n\n"
//					+ "Mood Journal app has been developed by researchers at the University "
//					+ "of Birmingham (UoB) to collect data for "
//					+ "research and teaching purposes."
//					+ "\n\n"
//					+ "We would love to hear your feedback for us and issues with the app. "
//					+ "Please send us an email - axw412@cs.bham.ac.uk."
//					+ "\n\n"
//					+ "Thank You!";
//
//			new Popup().showPopup(MainActivity.this, getResources().getString(R.string.info_title), message);
//			return true;
//		case R.id.menu_personality_test:
//			startActivity(new Intent(getApplicationContext(), PersonalityTestActivity.class));
//			return true;
//
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
//
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}

    private void planSetup() {
        TextView plan_label = (TextView) findViewById(R.id.plan_label);
        String routine = new PlanMgr(getApplicationContext()).getPlanRoutineDesc();
        String plan;
        SpannableStringBuilder spannable;

        if (routine != null) {
            plan = getResources().getString(R.string.detailed_plan, routine);
            spannable = new SpannableStringBuilder(plan);

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
                    new BackgroundColorSpan(0x223CB371),
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

            plan_label.setText(spannable);

        } else {
            plan = "No plan created";
            plan_label.setText(plan);
        }
    }

    public void changePlan(View v) {
        Intent intent = new Intent(this, PlanActivity.class);
        int step_num = 0;
        intent.putExtra("step", step_num);
        startActivity(intent);
    }

    // button click functions
    public void moodTest(View v) {

        MoodQuestionnaireMgr mgr = new MoodQuestionnaireMgr(getApplicationContext());

        if (mgr.getMoodQuestionnaireCountForToday() > 0) {
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.set(Calendar.HOUR_OF_DAY, 0);
            tomorrow.set(Calendar.MINUTE, 0);
            tomorrow.set(Calendar.SECOND, 0);
            tomorrow.set(Calendar.MILLISECOND, 0);
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
            long tomorrow_time = tomorrow.getTimeInMillis();
            long current_time = Calendar.getInstance().getTimeInMillis();
            int diff_mins = (int) ((tomorrow_time - current_time) / (60 * 1000));
            if (diff_mins > 59) {
                int hours_left = diff_mins / 60;
                diff_mins = diff_mins % 60;
                Toast.makeText(getApplicationContext(), "Today's mood questionnaire has been completed. Next questionnaire will be available after " + hours_left + " hours " + diff_mins + " mins.", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(), "Today's mood questionnaire has been completed. Next questionnaire will be available after " + diff_mins + " mins.", Toast.LENGTH_LONG).show();
            return;
        }

        startActivity(new Intent(this, MoodQuestionnaireActivity.class));
    }

    public void moodReport(View v) {
        startActivity(new Intent(this, MoodReportActivity.class));
    }

    public void askRuntimePermissions(View view) {
        askMissingPermissions();
    }


//	public void dailyTest(View v)
//	{
//		if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 16)
//		{
//			Toast.makeText(getApplicationContext(), "Today's daily questionnaire will be available after 4pm.", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		WellBeingQuestionnaireMgr mgr = new WellBeingQuestionnaireMgr(getApplicationContext());
//		if(mgr.getDailyQuestionnaireCountForToday() > 0)
//		{
//			Toast.makeText(getApplicationContext(), "Today's daily questionnaire is already completed.", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		startActivity(new Intent(this, WellBeingQuestionnaireActivity.class));
//	}
//
//	//	public void exit(View v) throws JSONException
//	//	{
//	//		this.finish();
//	//	}


}
