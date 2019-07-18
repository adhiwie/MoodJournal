package com.adhiwie.diary.file;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;

import com.adhiwie.diary.debug.CustomExceptionHandler;
import com.adhiwie.diary.user.data.UserData;
import com.adhiwie.diary.utils.Log;
import com.adhiwie.diary.utils.SharedPref;

public class FileMgr {


    private final SharedPref sp;
    private final String uuid;
    private final DataFormatter df;

    public FileMgr(Context context) {
        this.sp = new SharedPref(context);
        this.uuid = new UserData(context).getUuid();
        this.df = new DataFormatter(context);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(context));
        }
    }


    public void addData(DataInterface o) {
        String file_lock_key = getFileLockKey(o);
        String file_path = getFilePath(o);

        try {
            JSONObject json = df.createJSONObjectForDataEntry(o.getDataType(), o.toJSONString());

            boolean lock = sp.getBoolean(file_lock_key);
            int counter = 0;
            while (lock) {
                if (counter > 20)
                    break;
                counter++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    new Log().e(e.toString());
                }
                lock = sp.getBoolean(file_lock_key);
            }

            sp.add(file_lock_key, true);
            FileOperations fo = new FileOperations(file_path);
            fo.append(json.toString() + "\n");
            sp.add(file_lock_key, false);
        } catch (Exception e) {
            sp.add(file_lock_key, false);
            new Log().e(e.toString());
        }
    }

    public void addData(ArrayList<DataInterface> data, boolean clear) {
        if (data == null || data.size() == 0)
            return;

        String file_lock_key = getFileLockKey(data.get(0));
        String file_path = getFilePath(data.get(0));
        try {
            boolean lock = sp.getBoolean(file_lock_key);
            int counter = 0;
            while (lock) {
                if (counter > 20)
                    break;
                counter++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    new Log().e(e.toString());
                }
                lock = sp.getBoolean(file_lock_key);
            }


            sp.add(file_lock_key, true);
            FileOperations fo = new FileOperations(file_path);
            if (clear)
                fo.clear();
            for (DataInterface di : data) {
                JSONObject json = df.createJSONObjectForDataEntry(di.getDataType(), di.toJSONString());
                fo.append(json.toString());
                fo.append("\n");
            }
            sp.add(file_lock_key, false);
        } catch (Exception e) {
            sp.add(file_lock_key, false);
            new Log().e(e.toString());
        }
    }


    private String getFileLockKey(DataInterface object) {
        return "file_lock_key_" + object.getDataType();
    }

    private String getFilePath(DataInterface object) {
        FilePaths paths = new FilePaths(uuid);
        return paths.getFilePath(object.getDataType());
    }


}
