package com.adhiwie.moodjournal.user.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.adhiwie.moodjournal.communication.DataGetter;
import com.adhiwie.moodjournal.communication.DataTransmitter;
import com.adhiwie.moodjournal.system.APILevel;
import com.adhiwie.moodjournal.utils.Base64;
import com.adhiwie.moodjournal.utils.DataTypes;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Time;

public class UserData 
{

	private final Context context;
	private final SharedPref sp;
	private final String USER_GROUP_ID = "USER_GROUP_ID";

	public UserData(Context context)
	{
		this.context = context;
		this.sp = new SharedPref(context);
	}

	

	public String toJSONString() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("uuid", getUuid());
		json.put("email", getEmail());
		json.put("referral_code", getReferralCode());
		json.put("reg_time_millis", getRegTimeMillis());
		json.put("reg_timezone", getRegTimezone());
		json.put("device_api_level", getDeviceApiLevel());
		json.put("group_id", getGroupId());
		//json.put("contact_list", getContactList());
		//json.put("app_list", getAppList());
		return json.toString();
	}

	
	
	// getters
	public String getUuid() 
	{
		String USER_ID_KEY = "USER_ID_KEY";
		String uuid = sp.getString(USER_ID_KEY);
		if(uuid == null)
		{
			uuid = createShortUUID();
			sp.add(USER_ID_KEY, uuid);
		}
		return uuid;
		//return "1iynrpo6ceek95";
	}
	
	private String createShortUUID() 
	{
		UUID uuid = UUID.randomUUID();
		long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
		return Long.toString(l, Character.MAX_RADIX) + new Random().nextInt(9);
	}


	public long getRegTimeMillis() 
	{
		String USER_REGISTRATION_TIME_KEY = "USER_REGISTRATION_TIME_KEY";
		long time = sp.getLong(USER_REGISTRATION_TIME_KEY);
		if(time == 0)
		{
			time = Calendar.getInstance().getTimeInMillis();
			new Log().e("Registration time: " + time);
			sp.add(USER_REGISTRATION_TIME_KEY, time);
		}
		return time;
		//long time = 1454781428160L - (1*24*60*60*1000);
		//return time;
	}
	
	
	public String getRegTimezone() {
		return Calendar.getInstance().getTimeZone().getDisplayName();
	}
		
	public int getDeviceApiLevel() {
		return new APILevel().getDeviceAPILevel();
	}


	private final String USER_EMAIL_ADDRESS = "USER_EMAIL_ADDRESS";
	private String getEmail()
	{
		return sp.getString(USER_EMAIL_ADDRESS);
	}
	
	public void setEmail(String email)
	{
		sp.add(USER_EMAIL_ADDRESS, email);
	}


	private final String USER_REFERRAL_CODE = "USER_REFERRAL_CODE";
	private String getReferralCode()
	{
		return sp.getString(USER_REFERRAL_CODE);
	}
	
	public void setReferralCode(String r_code)
	{
		sp.add(USER_REFERRAL_CODE, r_code);
	}


	public int getStartDate()
	{
		Time t = new Time(getRegTimeMillis());
		return t.getEpochDays();
	}

	private JSONArray getContactList() throws JSONException
	{
		ContentResolver cr = context.getContentResolver();
		Cursor c = cr.query(Phone.CONTENT_URI, null, null, null, null);

        int name_index = c.getColumnIndex(Phone.DISPLAY_NAME);
        int number_Index = c.getColumnIndex(Phone.NUMBER);
		
		Base64 encoder = new Base64();
		JSONArray contacts = new JSONArray();
		while (c.moveToNext()) 
		{
			String name = c.getString(name_index);
			String number = c.getString(number_Index);
			if(name != null && !name.isEmpty() && number != null && !number.isEmpty())
			{	
				JSONObject json = new JSONObject();
				json.put("name", encoder.encode(name));
				json.put("number", encoder.encode(number));
				contacts.put(json);
			}
		}
		c.close();
		return contacts;
	}
	
	private JSONArray getAppList()
	{
		final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
		main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> installed_app_list =  this.context.getPackageManager().queryIntentActivities(main_intent, 0);
		
		JSONArray apps = new JSONArray();
		for(ResolveInfo ri : installed_app_list)
		{
			if(isSystemPackage(ri))
				continue;
			apps.put(ri.activityInfo.applicationInfo.packageName);
		}
		new Log().v("Number of apps installed: " + apps.length() );
	    return apps; 
	}
	
	private boolean isSystemPackage(ResolveInfo resolveInfo) {
	    return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
	}

	public int getGroupId() {
		return sp.getInt(USER_GROUP_ID);
	}

	public void setGroupId(int groupId) {
		sp.add(USER_GROUP_ID, groupId);
	}

	public void updateGroupId(String uuid) throws IOException {
		new DataGetter(this.context, uuid)
		{
			@Override
			protected void onPostExecute(String result)
			{
				if (result != "")
					setGroupId(Integer.valueOf(result));
				new Log().e("Group id : "+result);
			};
		}.execute();
	}
}
