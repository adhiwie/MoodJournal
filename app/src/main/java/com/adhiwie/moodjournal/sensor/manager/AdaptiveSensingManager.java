package com.adhiwie.moodjournal.sensor.manager;

import java.util.Calendar;

import org.json.JSONException;

import android.content.Context;

import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.sensor.data.ActivityData;
import com.adhiwie.moodjournal.sensor.data.LocationData;
import com.adhiwie.moodjournal.sensor.push.ActivityType;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Time;

public class AdaptiveSensingManager {


    private final SharedPref sp;
    private final Context context;
    private final Log log;

    public final static double LOCATION_FREQUENCY_SLOW = 30; // in minutes
    public final static double LOCATION_FREQUENCY_FAST = 5; // in minutes

    public AdaptiveSensingManager(Context context) throws JSONException {
        this.sp = new SharedPref(context);
        this.context = context;
        this.log = new Log();

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));
        }
    }


    public void onNewActivity(ActivityData ad) throws JSONException {
        log.d("Adaptive Location Sensing...");

        if (ad.getActivity().equals(new ActivityType().ACTIVITY_UNKNOWN))
            return; // we cannot make any decision

        log.d("Activity is interesting...");

        addNewActivity(ad.getActivity());

//		if(!isLocationSubscribedToday())
//		{
//			new LocationManager(context).requestLocationUpdates(LOCATION_FREQUENCY_SLOW);
//			setLocationSubscribedToday();
//			setLastSubscriptionTime(Calendar.getInstance().getTimeInMillis());
//			return;
//		}
//
//		LocationManager lm = new LocationManager(context);
//
//		if(isLocationSensingRequired())
//		{
//			lm.requestLocationUpdates(LOCATION_FREQUENCY_FAST);
//		}
//		else if(shouldLocationSensingBeRemoved() || new Time(Calendar.getInstance()).get(Time.HOURS) < 6)
//		{
//			lm.removeContinuousUpdates();
//		}
//		else
//		{
//			LocationData ld = lm.getCurrentLocation();
//			long time_since_last_location_registered = Calendar.getInstance().getTimeInMillis();
//			if(ld != null)
//				time_since_last_location_registered -= ld.getTime();
//
//
//			if(timeSinceLastSubscription() > LOCATION_FREQUENCY_FAST && (time_since_last_location_registered > LOCATION_FREQUENCY_SLOW))
//			{
//				lm.requestLocationUpdates(LOCATION_FREQUENCY_SLOW);
//				setLastSubscriptionTime(Calendar.getInstance().getTimeInMillis());
//			}
//		}
    }


    private final String M1 = "MOBILITY_VALUE_1";
    private final String M2 = "MOBILITY_VALUE_2";
    private final String M3 = "MOBILITY_VALUE_3";

    private void addNewActivity(String activity) {
        boolean is_dynamic = new ActivityType().isDynamic(activity);

        sp.add(M3, sp.getBoolean(M2));
        sp.add(M2, sp.getBoolean(M1));
        sp.add(M1, is_dynamic);

        updateLastActivityTime();
    }

    private final String AS_LAST_ACTIVITY_TIME = "AS_LAST_ACTIVITY_TIME";

    private void updateLastActivityTime() {
        sp.add(AS_LAST_ACTIVITY_TIME, Calendar.getInstance().getTimeInMillis());
    }

    private long getTimeSinceLastActivity() {
        long last_time = sp.getLong(AS_LAST_ACTIVITY_TIME);
        return Calendar.getInstance().getTimeInMillis() - last_time;
    }

    private boolean isLocationSensingRequired() {
        boolean m3 = sp.getBoolean(M3);
        boolean m2 = sp.getBoolean(M2);
        boolean m1 = sp.getBoolean(M1);

        if (getTimeSinceLastActivity() > 10 * 60 * 1000)
            return m1;

        return m3 && m2 && m1;
    }


    private boolean shouldLocationSensingBeRemoved() {
        boolean m3 = sp.getBoolean(M3);
        boolean m2 = sp.getBoolean(M2);
        boolean m1 = sp.getBoolean(M1);

        if (getTimeSinceLastActivity() > 10 * 60 * 1000)
            return !m1;

        return !(m1 || m2 || m3);
    }


    private final String Adaptive_Location_Sensing_Start_Date = "Adaptive_Location_Sensing_Start_Date";

    protected boolean isLocationSubscribedToday() {
        return sp.getInt(Adaptive_Location_Sensing_Start_Date) == new Time(Calendar.getInstance()).getEpochDays();
    }

    protected void setLocationSubscribedToday() {
        sp.add(Adaptive_Location_Sensing_Start_Date, new Time(Calendar.getInstance()).getEpochDays());
    }

    private final String ALS_Last_Subscription_Time = "ALS_Last_Subscription_Time";

    private long timeSinceLastSubscription() {
        return Calendar.getInstance().getTimeInMillis() - sp.getLong(ALS_Last_Subscription_Time);
    }

    private void setLastSubscriptionTime(long time) {
        sp.add(ALS_Last_Subscription_Time, time);
    }
}
