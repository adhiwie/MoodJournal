package com.adhiwie.moodjournal;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.adhiwie.moodjournal.communication.helper.RegistrationDataTransmission;
import com.adhiwie.moodjournal.communication.helper.RegistrationDataTransmission.RegisterationResultListener;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.Popup;
import com.adhiwie.moodjournal.utils.SharedPref;

public class ConsentActivity extends Activity {


	private String pwd = null;
	private String r_code = null; 
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
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
		actionbar_title.setText(getResources().getString(R.string.title_activity_consent));

		ShimmerFrameLayout container = (ShimmerFrameLayout) findViewById(R.id.shimmer_action_bar);
		container.setBaseAlpha(0.8f);
		container.setAutoStart(true);

		setContentView(R.layout.activity_consent);


		ShimmerFrameLayout container1 = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
		container1.setBaseAlpha(0.8f);
		container1.setAutoStart(true);

		if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) ) 
		{
			Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(getApplicationContext()) );
		}

	}

	public void iAgreeBtnClick(View v) 
	{
		try
		{
//			addShortcutIcon(MainActivity.class, getResources().getString(R.string.app_name));
			EditText et = (EditText) findViewById(R.id.email_address);
			if (pwd == null && (et.getText() == null || et.getText().toString().length() == 0)) 
			{
				pwd = "hidden";
				new Popup().showPopup(getApplicationContext(), "Moto 360 Smart-Watch", 
						"You must enter a password to enter the lottery for Moto 360 Smart-Watch. "
						+ "Leave it blank if you do not want to participate.");
				return;
			} 
			else 
			{
				pwd = et.getText().toString();
			}
			
/*

			EditText et_r_code = (EditText) findViewById(R.id.referral_code);
			if (r_code == null && (et_r_code.getText() == null || et_r_code.getText().toString().length() == 0)) 
			{
				r_code = "hidden";
//				new Popup().showPopup(getApplicationContext(), "Referral Code", 
//						"Did someone suggested this app to you? If yes, please enter his/her unique code to increase "
//						+ "his/her chances to win the Moto 360 smart watch.");
//				return;
			} 
			else 
			{
				r_code = et_r_code.getText().toString();
			}
*/

			
			UserData ud = new UserData(getApplicationContext());
			ud.setPassword(pwd);
//			ud.setReferralCode(r_code);

			RegistrationDataTransmission rdt = new RegistrationDataTransmission(getApplicationContext());
			rdt.registerNow( new RegisterationResultListener() {

				@Override
				public void onResultAvailable(boolean result) {
					if(result == false)
					{
						Toast.makeText(getApplicationContext(), "Unable to register you. Check the network connectivity on your device.", Toast.LENGTH_SHORT).show();
						return;
					}
					Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
					new ConsentMgr(getApplicationContext()).setConsentGiven();
					startActivity(new Intent(getApplicationContext(), MainActivity.class));
					finish();
				}
			});
		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), "Oops.. Something went wrong.", Toast.LENGTH_SHORT).show();
			new Log().e(e.toString());
		}
	}



	private void addShortcutIcon(Class<MainActivity> launcher_activity_class, String title)
	{
		SharedPref sp = new SharedPref(getApplicationContext());
		String ICON_PLACED_STATUS = "ICON_PLACED_STATUS";
		if(!sp.getBoolean(ICON_PLACED_STATUS))
		{
			Intent shortcutIntent = new Intent(this, launcher_activity_class);
			//shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			Intent addIntent = new Intent();
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
			addIntent.putExtra("duplicate", false);
			addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			sendBroadcast(addIntent);
			sp.add(ICON_PLACED_STATUS, true);
		}

	}

	

}
