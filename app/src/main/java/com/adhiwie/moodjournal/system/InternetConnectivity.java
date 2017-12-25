package com.adhiwie.moodjournal.system;

import java.io.IOException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class InternetConnectivity extends AsyncTask<Void, Void, Boolean>
{
	private final Context context;
	
	public InternetConnectivity(Context context) 
	{
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Void... arg0) 
	{
		return isInternetAvailable(context); 
	}
	
	private boolean isInternetAvailable(Context context) 
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		
		//check for network state 
		if( ni != null && ni.isConnected() )
		{
			// if not connected to wifi
			if(ni.getType() != ConnectivityManager.TYPE_WIFI)
				return true;
			
			//if connected to wifi
			return isOnline();
			
		}
		return false;
	}
		

	private boolean isOnline() {

	    Runtime runtime = Runtime.getRuntime();
	    try {

	        Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
	        int     exitValue = ipProcess.waitFor();
	        return (exitValue == 0);

	    } catch (IOException e)          { e.printStackTrace(); } 
	      catch (InterruptedException e) { e.printStackTrace(); }

	    return false;
	}

}
