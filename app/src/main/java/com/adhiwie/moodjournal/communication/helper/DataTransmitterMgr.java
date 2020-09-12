package com.adhiwie.moodjournal.communication.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;

import android.content.Context;

import com.adhiwie.moodjournal.communication.FileTransmitter;
import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.exception.FileNotCreatedException;
import com.adhiwie.moodjournal.file.DataInterface;
import com.adhiwie.moodjournal.file.FileMgr;
import com.adhiwie.moodjournal.file.FileOperations;
import com.adhiwie.moodjournal.file.FilePaths;
import com.adhiwie.moodjournal.sensor.pull.CallSensor;
import com.adhiwie.moodjournal.sensor.pull.PhoneUsageSensor;
import com.adhiwie.moodjournal.sensor.pull.SmsSensor;
import com.adhiwie.moodjournal.user.data.UserData;
import com.adhiwie.moodjournal.utils.DataTypes;
import com.adhiwie.moodjournal.utils.Log;
import com.adhiwie.moodjournal.utils.SharedPref;
import com.adhiwie.moodjournal.utils.Time;

public class DataTransmitterMgr {


    private final Log log = new Log();
    private final Context context;
    private final SharedPref sp;
    private final String uuid;

    private final String LAST_FILE_UPLOAD_DATE(String data_type) {
        return "LAST_FILE_UPLOAD_DATE_NEW" + data_type.toUpperCase(Locale.getDefault());
    }


    public DataTransmitterMgr(Context context) {
        this.uuid = new UserData(context).getUuid();
        this.context = context;
        this.sp = new SharedPref(context);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));
        }
    }


    public void transmitAllData() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                new Log().v("Thread created.");
                transmitPushSensorData();
                //transmitPullSensorData();
                transmitQuestionnaireData();
                transmitPendingDataIfAny();
            }
        };
        thread.start();
    }


    private void transmitPushSensorData() {
        //transmitData(new DataTypes().ACCESSIBILITY_EVENT);
        transmitData(new DataTypes().ACTIVITY);
        //transmitData(new DataTypes().LOCATION);
        //transmitData(new DataTypes().NOTIFICATION);
        transmitData(new DataTypes().NETWORK);
        transmitData(new DataTypes().POWER_CONNECTIVITY);
        transmitData(new DataTypes().RINGER_MODE);
    }

    private void transmitPullSensorData() {
        transmitData(new DataTypes().CALL);
        transmitData(new DataTypes().PHONE_USAGE);
        transmitData(new DataTypes().SMS);
    }

    private void transmitQuestionnaireData() {
        transmitData(new DataTypes().PHQ8_QUESTIONNAIRE);
        transmitData(new DataTypes().MOOD_QUESTIONNAIRE);
        transmitData(new DataTypes().REMINDER);
    }

    private void transmitPendingDataIfAny() {
        try {
            PersonalityTestDataTransmission pt = new PersonalityTestDataTransmission(context);
            if (pt.isDataAvailable() && !pt.isDataTransmitted())
                pt.transmitData();

            GCSDataTransmission gt = new GCSDataTransmission(context);
            if (gt.isDataAvailable() && !gt.isDataTransmitted())
                gt.transmitData();

            SRBAIDataTransmission st = new SRBAIDataTransmission(context);
            if (st.isDataAvailable() && !st.isDataTransmitted())
                st.transmitData();
        } catch (Exception e) {
            new Log().e(e.toString());
        }
    }


    private void transmitData(String data_type) {
        try {
            log.v("Trying to upload files for data_type - " + data_type);

            int last_upload_date = sp.getInt(LAST_FILE_UPLOAD_DATE(data_type));
            if (last_upload_date == 0) {
                sp.add(LAST_FILE_UPLOAD_DATE(data_type), (new UserData(context).getStartDate()));
                return;
            } else if (last_upload_date < new Time(Calendar.getInstance()).getEpochDays()) {
                uploadFileByDataType(data_type);
            }
        } catch (Exception e) {
            log.e(e.toString());
        }
    }


    public void uploadFileByDataType(final String data_type) throws IOException, FileNotCreatedException, JSONException {
        if (data_type.equals(new DataTypes().PHONE_USAGE) ||
                data_type.equals(new DataTypes().CALL) ||
                data_type.equals(new DataTypes().SMS)) {
            int last_upload_date = sp.getInt(LAST_FILE_UPLOAD_DATE(data_type));
            Calendar start_time = Calendar.getInstance();
            start_time.setTimeInMillis(new Time(last_upload_date).getMillis());
            start_time.set(Calendar.HOUR_OF_DAY, 0);
            start_time.set(Calendar.MINUTE, 0);
            start_time.set(Calendar.SECOND, 0);
            start_time.set(Calendar.MILLISECOND, 1);

            int current_date = new Time(Calendar.getInstance()).getEpochDays();
            Calendar end_time = Calendar.getInstance();
            end_time.setTimeInMillis(new Time(current_date - 1).getMillis());
            end_time.set(Calendar.HOUR_OF_DAY, 23);
            end_time.set(Calendar.MINUTE, 59);
            end_time.set(Calendar.SECOND, 59);
            end_time.set(Calendar.MILLISECOND, 999);

            log.d("Start time - " + start_time.getTime());
            log.d("End time - " + end_time.getTime());

            if (data_type.equals(new DataTypes().CALL)) {
                ArrayList<DataInterface> data = new CallSensor(context).getCallDetails(start_time, end_time);
                FileMgr fm = new FileMgr(context);
                fm.addData(data, true);
            } else if (data_type.equals(new DataTypes().PHONE_USAGE)) {
                ArrayList<DataInterface> data = new PhoneUsageSensor(context).getPhoneUsageEvents(start_time, end_time);
                FileMgr fm = new FileMgr(context);
                fm.addData(data, true);
            } else if (data_type.equals(new DataTypes().SMS)) {
                ArrayList<DataInterface> data = new SmsSensor(context).getSmsDetails(start_time, end_time);
                FileMgr fm = new FileMgr(context);
                fm.addData(data, true);
            }
        }

        FilePaths fp = new FilePaths(uuid);
        final String file_path = fp.getFilePath(data_type);
        final String uploading_file_path = fp.getUploaderFilePath(data_type);

        final FileOperations source = new FileOperations(file_path);
        final FileOperations destination = new FileOperations(uploading_file_path);
        destination.clear();
        destination.write(source.read(true));
        source.clear();


//		if(data_type.equals(new DataTypes().ACCESSIBILITY_EVENT))
//		{
//			new LargeFileTransmitter(context, uploading_file_path, data_type)
//			{
//				protected void onPostExecute(Boolean result)
//				{
//					try
//					{
//						if(result)
//						{
//							destination.clear();
//							sp.add(LAST_FILE_UPLOAD_DATE(data_type), new Time(Calendar.getInstance()).getEpochDays());
//						}
//						else
//							source.append(destination.read(true));
//					}
//					catch (IOException e)
//					{
//						log.e(e.toString());
//					}
//				};
//			}.execute();
//		}
//		else
//		{

        new FileTransmitter(context, uploading_file_path, data_type) {
            protected void onPostExecute(Boolean result) {
                try {
                    if (result) {
                        destination.clear();
                        sp.add(LAST_FILE_UPLOAD_DATE(data_type), new Time(Calendar.getInstance()).getEpochDays());
                    } else
                        source.append(destination.read(true));
                } catch (IOException e) {
                    log.e(e.toString());
                }
            }

            ;
        }.execute();
//		}

    }


}