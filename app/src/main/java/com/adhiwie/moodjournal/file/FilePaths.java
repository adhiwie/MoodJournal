package com.adhiwie.moodjournal.file;

import java.io.File;

import android.os.Environment;

public class FilePaths {


    private final String uuid;
    private final String dir_path;

    public FilePaths(String uuid) {
        this.uuid = uuid;
        this.dir_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MoodJournal";
        File dir = new File(dir_path);
        if (!dir.exists())
            dir.mkdirs();
    }


    public final String getFilePath(String data_type) {
        return dir_path + "/" + data_type + "_" + uuid + ".txt";
    }


    public final String getUploaderFilePath(String data_type) {
        return dir_path + "/" + data_type + "_" + uuid + "_uploading.txt";
    }


}
