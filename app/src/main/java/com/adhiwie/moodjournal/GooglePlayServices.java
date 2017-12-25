package com.adhiwie.moodjournal;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GooglePlayServices {

	public boolean isGoogplePlayServiceAvailable(final Activity activity)
	{
		boolean status = false;
		int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		switch (code) 
		{
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED: 
			try 
			{
				Dialog d = GooglePlayServicesUtil.getErrorDialog(code, activity, 0);
				d.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) 
					{
						isGoogplePlayServiceAvailable(activity);
					}
				});
				d.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case ConnectionResult.API_UNAVAILABLE: 
			try 
			{
				Dialog d = GooglePlayServicesUtil.getErrorDialog(code, activity, 0);
				d.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
						isGoogplePlayServiceAvailable(activity);
					}
				});
				d.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case ConnectionResult.SUCCESS: 
			status = true;
			break;
		}
		return status;
	}
}
