package com.adhiwie.moodjournal.sensor.pull;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.adhiwie.moodjournal.file.DataInterface;
import com.adhiwie.moodjournal.sensor.data.SmsData;
import com.adhiwie.moodjournal.utils.Base64;
import com.adhiwie.moodjournal.utils.Log;

public class SmsSensor {

    private final Log log;
    private final Context context;
    private final Uri uri;

    @SuppressLint("NewApi")
    public SmsSensor(Context context) {
        this.log = new Log();
        this.context = context;
        this.uri = android.provider.Telephony.Sms.CONTENT_URI;
    }


    public ArrayList<DataInterface> getSmsDetails(Calendar c1, Calendar c2) {
        ArrayList<DataInterface> s_data = new ArrayList<DataInterface>();
        try {
            String order = "date DESC";
            String from_date = String.valueOf(c1.getTimeInMillis());
            String to_date = String.valueOf(c2.getTimeInMillis());
            String[] where = {from_date, to_date};

            Cursor cursor = context.getContentResolver().query(uri, null, "date BETWEEN ? AND ?", where, order);

            log.e("Counts: " + cursor.getCount());

            int address = cursor.getColumnIndex("address");
            int date = cursor.getColumnIndex("date");
            int date_sent = cursor.getColumnIndex("date_sent");
            int read = cursor.getColumnIndex("read");
            int type = cursor.getColumnIndex("type");
            int body = cursor.getColumnIndex("body");

            Base64 encoder = new Base64();
            while (cursor.moveToNext()) {
                String address_value = cursor.getString(address);
                long date_value = Long.valueOf(cursor.getString(date));
                long date_sent_value = Long.valueOf(cursor.getString(date_sent));
                boolean is_read = cursor.getInt(read) == 1;
                String type_value = getMessageType(Integer.parseInt(cursor.getString(type)));
                String body_value = cursor.getString(body);
                int body_length = 0;
                if (body_value != null)
                    body_length = body_value.length();

                SmsData sd = new SmsData(encoder.encode(address_value), date_value, date_sent_value, is_read, type_value, body_length);
                s_data.add(sd);
            }
            cursor.close();
        } catch (Exception e) {
            new Log().e(e.toString());
        }

        return s_data;
    }

    private String getMessageType(int code) {
        switch (code) {
            case Telephony.Sms.Inbox.MESSAGE_TYPE_ALL:
                return "ALL";
            case Telephony.Sms.Inbox.MESSAGE_TYPE_DRAFT:
                return "DRAFT";
            case Telephony.Sms.Inbox.MESSAGE_TYPE_FAILED:
                return "FAILED";
            case Telephony.Sms.Inbox.MESSAGE_TYPE_INBOX:
                return "INBOX";
            case Telephony.Sms.Inbox.MESSAGE_TYPE_OUTBOX:
                return "OUTBOX";
            case Telephony.Sms.Inbox.MESSAGE_TYPE_QUEUED:
                return "QUEUED";
            case Telephony.Sms.Inbox.MESSAGE_TYPE_SENT:
                return "SENT";
            default:
                return "UNKNOWN";
        }
    }

}
