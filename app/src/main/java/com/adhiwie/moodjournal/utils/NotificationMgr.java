package com.adhiwie.moodjournal.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.system.APILevel;

public class NotificationMgr 
{

//	@SuppressWarnings("deprecation")
//	@SuppressLint("NewApi")
//	public void triggerNotification(Context context, PendingIntent pi, int id, String title, String message)
//	{
//		Notification.Builder b = new Notification.Builder(context)
//		.setSmallIcon(R.drawable.ic_launcher)
//		.setContentTitle(title)
//		.setContentText(message)
//		.setAutoCancel(true)
//		.setNumber(0)
//		.setContentIntent(pi)
//		.setDefaults(Notification.DEFAULT_ALL);
//
//		Notification n;
//		if(new APILevel().getDeviceAPILevel() < 16)
//			n = b.getNotification();
//		else
//			n = b.build();
//
//
//		NotificationManager n_manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//		n_manager.notify(id, n);
//	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void triggerPriorityNotification(Context context, PendingIntent pi, int id, String title, String message)
	{
		Notification.Builder b = new Notification.Builder(context)
		.setSmallIcon(R.drawable.ic_mood)
		.setContentTitle(title)
		.setContentText(message)
		.setAutoCancel(true)
		.setNumber(0)
		.setContentIntent(pi)
		.setVibrate(new long[]{0l})
		.setStyle(new Notification.BigTextStyle().bigText(message))
		.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);

		Notification n;
		if(new APILevel().getDeviceAPILevel() < 16)
			n = b.getNotification();
		else
		{
			b = b.setPriority(Notification.PRIORITY_DEFAULT);
			n = b.build();
		}

		NotificationManager n_manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		n_manager.notify(id, n);
	}
/*
	private int getNotificationIcon() {
	    boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
	    return useWhiteIcon ? R.drawable.ic_launcher_silhouette : R.drawable.ic_launcher;
	}
	*/
}
