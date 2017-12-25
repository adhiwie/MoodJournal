package com.adhiwie.moodjournal.sensor.pull;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import com.adhiwie.moodjournal.file.DataInterface;
import com.adhiwie.moodjournal.sensor.data.CallData;
import com.adhiwie.moodjournal.utils.Base64;
import com.adhiwie.moodjournal.utils.Log;

public class CallSensor {

	private final Context context;

	public CallSensor(Context context) {
		this.context = context;
	}


	public ArrayList<DataInterface> getCallDetails(Calendar c1, Calendar c2) 
	{
		ArrayList<DataInterface> c_data = new ArrayList<DataInterface>();
		try
		{
			String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
			String fromDate = String.valueOf(c1.getTimeInMillis());
			String toDate = String.valueOf(c2.getTimeInMillis());
			String[] whereValue = {fromDate,toDate};


			Uri uri = CallLog.Calls.CONTENT_URI;
			Cursor cursor = context.getContentResolver().query(uri, null, android.provider.CallLog.Calls.DATE+" BETWEEN ? AND ?", whereValue, strOrder);

			int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
			int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
			int date = cursor.getColumnIndex(CallLog.Calls.DATE);
			int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);       

			Base64 encoder = new Base64();
			while (cursor.moveToNext()) 
			{
				String number_value = cursor.getString(number);
				long date_value = Long.valueOf(cursor.getString(date));
				long duration_value = Long.parseLong(cursor.getString(duration));
				String type_value = "UNKNOWN";
				int type_code = Integer.parseInt(cursor.getString(type));
				switch (type_code) 
				{
				case CallLog.Calls.OUTGOING_TYPE:
					type_value = "OUTGOING";
					break;
				case CallLog.Calls.INCOMING_TYPE:
					type_value = "INCOMING";
					break;

				case CallLog.Calls.MISSED_TYPE:
					type_value = "MISSED";
					break;
				}

				CallData cd = new CallData(encoder.encode(number_value), duration_value, date_value, type_value);
				c_data.add(cd);
			}
			cursor.close();
		}
		catch(Exception e)
		{
			new Log().e(e.toString());
		}
		return c_data;
	}
}
