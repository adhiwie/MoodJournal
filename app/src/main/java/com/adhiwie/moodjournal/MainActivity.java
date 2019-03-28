package com.adhiwie.moodjournal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.exception.IncompatibleAPIException;
import com.adhiwie.moodjournal.plan.PlanActivity;
import com.adhiwie.moodjournal.plan.PlanMgr;
import com.adhiwie.moodjournal.questionnaire.mood.MoodQuestionnaireActivity;
import com.adhiwie.moodjournal.questionnaire.mood.MoodQuestionnaireMgr;
import com.adhiwie.moodjournal.questionnaire.goalcommitment.GCSActivity;
import com.adhiwie.moodjournal.questionnaire.goalcommitment.GCSMgr;
import com.adhiwie.moodjournal.questionnaire.personality.PersonalityTestActivity;
import com.adhiwie.moodjournal.questionnaire.wellbeing.WellBeingQuestionnaireActivity;
import com.adhiwie.moodjournal.report.MoodReportActivity;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.user.permission.Permission;
import com.adhiwie.moodjournal.user.permission.RuntimePermission;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.Popup;
import com.adhiwie.moodjournal.utils.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity {

    private FrameLayout root_layout;

    private Log log = new Log();

    private final String[] required_permissions = new String[]
            {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getMissingPermissions().size() > 0) {
            setContentView(R.layout.activity_runtime_permission);
        } else {
            setContentView(R.layout.activity_main);
        }

        root_layout = findViewById(R.id.root_layout);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (!new ConsentMgr(getApplicationContext()).isConsentGiven()) {
            startActivity(new Intent(this, ConsentActivity.class));
            this.finish();
            return;
        }

        if (getMissingPermissions().size() > 0) {
            getSupportActionBar().hide();
        } else {
            setLayout();
            check_Consent_GooglePlayService_Permissions_LinkedTask();
        }

        new GooglePlayServices().isGooglePlayServiceAvailable(MainActivity.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_personality:
                startActivity(new Intent(getApplicationContext(), PersonalityTestActivity.class));
                return true;
            case R.id.action_info:
                String message =
					"Your unique id is: " + new UserData(getApplicationContext()).getUuid()
					+ "\n\n"
					+ "Mood Journal app has been developed by researchers at the University "
					+ "of Birmingham (UoB) to collect data for "
					+ "research and teaching purposes."
					+ "\n\n"
					+ "We would love to hear your feedback for us and issues with the app. "
					+ "Please send us an email - axw412@cs.bham.ac.uk."
					+ "\n\n"
					+ "Thank You!";

			    new Popup().showPopup(MainActivity.this, getResources().getString(R.string.info_title), message);
			    return true;

            default:
                return super.onOptionsItemSelected(item);
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
                imageStatus.setImageResource(R.drawable.ic_sleepy);
                break;
            case 1:
                todaysMoodStatus.setText(getResources().getString(R.string.todays_mood_q_stats_tracked));
                trackMoodButton.setVisibility(View.GONE);
                imageStatus.setImageResource(R.drawable.ic_happy);
                break;
            default:
                break;
        }

        planSetup();
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
            check_Consent_GooglePlayService_Permissions_LinkedTask();
        }
    }


    private void check_Consent_GooglePlayService_Permissions_LinkedTask() {

        new Log().e("check_Consent_GooglePlayService_Permissions_LinkedTask is called");

        if (!new GooglePlayServices().isGooglePlayServiceAvailable(MainActivity.this)) {
            new Snackbar(root_layout).shortLength("Error with Google Play Service.");
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


    //app_bar_menu items
//	@Override
//	public boolean onCreateOptionsMenu(Menu app_bar_menu)
//	{
//		getMenuInflater().inflate(R.app_bar_menu.main_menu, app_bar_menu);
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
            plan = getResources().getString(R.string.detailed_plan);
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
                new Snackbar(root_layout).shortLength("Today's mood questionnaire has been completed. Next questionnaire will be available after " + hours_left + " hours " + diff_mins + " mins.");
            } else
                new Snackbar(root_layout).shortLength("Today's mood questionnaire has been completed. Next questionnaire will be available after " + diff_mins + " mins.");
            return;
        }

        startActivity(new Intent(this, MoodQuestionnaireActivity.class));
    }

    public void moodReport(View v) {
        startActivity(new Intent(this, MoodReportActivity.class));
    }

    public void phqTest(View v) {
        startActivity(new Intent(this, WellBeingQuestionnaireActivity.class));
    }

    public void askRuntimePermissions(View view) {
        askMissingPermissions();
    }
}
