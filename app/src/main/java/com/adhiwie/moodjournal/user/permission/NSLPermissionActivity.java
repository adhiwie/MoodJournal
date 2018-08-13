package com.adhiwie.moodjournal.user.permission;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.adhiwie.moodjournal.MainActivity;
import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.Popup;

public class NSLPermissionActivity extends AppCompatActivity {

	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nsl_permission);
		
		if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) ) 
		{
			Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(getApplicationContext()) );
		}

	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(new Permission(getApplicationContext()).isNSLPermitted())
		{
			new Log().v("Permission granted");
			Intent i = new Intent(this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			finish();
		}
		else
		{
			new Popup().showPopup(NSLPermissionActivity.this, "Permission Required", 
					"Provide the permission to keep the app running. "
					+ "\n\n"
					+ "If you have previously given this permission, please reset it by disabling and enabling it again.");
		}
	}

	public void openSettings(View v)
	{
		Intent i = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
		startActivityForResult(i, 0);
	}
}
