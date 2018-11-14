package com.adhiwie.moodjournal.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperations {
    private final File file;

    public FileOperations(String path) throws IOException {
        this.file = new File(path);
        if (!file.exists())
            file.createNewFile();
    }

    public void write(String data) throws IOException {
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(data);
        bw.close();
        fw.close();
    }


    public String read(boolean with_break) throws IOException {
        StringBuffer output = new StringBuffer();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        while ((line = br.readLine()) != null) {
            output.append(line);
            if (with_break)
                output.append("\n");
        }
        br.close();
        fr.close();
        return output.toString();
    }


    public void append(String data) throws IOException {
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.append(data);
        bw.close();
        fw.close();
    }

    public void clear() throws IOException {
        file.delete();
        file.createNewFile();
    }


}
