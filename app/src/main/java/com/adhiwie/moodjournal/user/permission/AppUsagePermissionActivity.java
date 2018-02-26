package com.adhiwie.moodjournal.user.permission;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.Popup;

public class AppUsagePermissionActivity extends Activity {

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		actionbar_title.setText(getResources().getString(R.string.title_activity_app_usage_permission));
		
		setContentView(R.layout.activity_app_usage_permission);
		
		if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) ) 
		{
			Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(getApplicationContext()) );
		}

	}




	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(new Permission(getApplicationContext()).isAppAccessPermitted())
		{
			new Log().v("Permission granted");
			Intent i = new Intent(this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			finish();
		}
		else
		{
			new Popup().showPopup(AppUsagePermissionActivity.this, "Permission Required", 
					"Provide the permission to keep the app running. "
							+ "\n\n"
							+ "If you have previously given this permission, please reset it by diabling and enabling it again.");
		}
	}


	@SuppressLint("NewApi")
	public void openSettings(View v)
	{

		try
		{
			Intent i = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
			startActivityForResult(i, 0);
		}
		catch(Exception e)
		{
			try
			{
			Intent intent = new Intent(); 
			intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$UsageAccessSettingsActivity")); 
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			startActivity(intent);
			}
			catch(Exception e2)
			{
				new Log().e(e2.toString());
			}
		}
	}

}