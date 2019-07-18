package com.adhiwie.mymoodjournal.debug;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;

import com.adhiwie.mymoodjournal.communication.helper.DataTransmitterMgr;
import com.adhiwie.mymoodjournal.file.FileMgr;
import com.adhiwie.mymoodjournal.sensor.data.ErrorLogData;
import com.adhiwie.mymoodjournal.utils.DataTypes;
import com.adhiwie.mymoodjournal.utils.Log;

public class CustomExceptionHandler implements UncaughtExceptionHandler {
    private UncaughtExceptionHandler defaultUEH;
    private final Context context;

    public CustomExceptionHandler(Context context) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();

        new Log().e("CustomExceptionHandler: an uncaught exception detected! " + stacktrace);


        new FileMgr(context).addData(new ErrorLogData(stacktrace));

        try {
            new DataTransmitterMgr(context).uploadFileByDataType(new DataTypes().ERROR_LOG);
        } catch (Exception exp) {
            new Log().e("CustomExceptionHandler: " + exp.toString());
        }

        defaultUEH.uncaughtException(t, e);
    }

}