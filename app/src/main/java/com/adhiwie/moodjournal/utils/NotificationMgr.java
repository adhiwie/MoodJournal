package com.adhiwie.moodjournal.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.adhiwie.moodjournal.R;
import com.adhiwie.moodjournal.system.APILevel;

public class NotificationMgr 
{
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void triggerPriorityNotification(Context context, PendingIntent pi, int id, String title, String message)
	{
		NotificationManager n_manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		String CHANNEL_ID = "MOOD_JOURNAL";

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = "mood journal";
			String description = "Channel for Mood Journal app";
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			channel.enableLights(true);
			channel.enableVibration(true);
			channel.setShowBadge(true);
			n_manager.createNotificationChannel(channel);
		}

		NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_mood)
			.setContentTitle(title)
			.setContentText(message)
			.setAutoCancel(true)
			.setNumber(0)
			.setContentIntent(pi)
			.setVibrate(new long[]{0l})
			.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
			.setColor(context.getResources().getColor(R.color.MediumSeaGreen))
			.setDefaults(Notification.DEFAULT_ALL)
			.setPriority(NotificationCompat.PRIORITY_HIGH);

		Notification n;
		if(new APILevel().getDeviceAPILevel() < 16)
			n = b.getNotification();
		else
		{
			b = b.setPriority(Notification.PRIORITY_HIGH);
			n = b.build();
		}

		n_manager.notify(id, n);
	}
/*
	private int getNotificationIcon() {
	    boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
	    return useWhiteIcon ? R.drawable.ic_launcher_silhouette : R.drawable.ic_launcher;
	}
	*/
}
