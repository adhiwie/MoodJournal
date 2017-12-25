package com.adhiwie.moodjournal.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.adhiwie.moodjournal.debug.CustomExceptionHandler;
import com.adhiwie.moodjournal.exception.FileNotCreatedException;
import com.adhiwie.moodjournal.utils.Log;


public class LargeFileTransmitter extends AsyncTask<Void, Void, Boolean>
{
	private final Log log = new Log();;
	private final String data_type;
	private final URL url;
	private final File file;

	public LargeFileTransmitter(Context context, String file_location, String data_type) throws FileNotCreatedException, IOException, JSONException 
	{
		if( !(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler) ) 
			Thread.setDefaultUncaughtExceptionHandler( new CustomExceptionHandler(context) );


		file = new File(file_location);
		if( ! file.exists() ) 
			throw new FileNotFoundException("File not found!! Please check if the given file location is correct: " + file_location);

		this.data_type = data_type;
		//this.url =  new URL("http://www.cs.bham.ac.uk/~axm514/mytraces/data/registrar.php");
		this.url = new URL("https://moodjournal-server-adhiwie.c9users.io/registrar.php");
	}




	protected Boolean doInBackground(Void... params) 
	{
		try
		{
			log.e("Total data entries: " + readData().length());

			int start = 0;
			int end = 499;
			
			while(true)
			{
				log.d("****************************************************");
				JSONArray ja = readData(start, end);
				if(ja.length() == 0)
					break;
				ContentValues values = new ContentValues();
				values.put("data_array", ja.toString());
				log.e("Number of entries: " + ja.length());
				String data = values.toString();
				boolean flag = new HttpHelper(url).sendData(data);
				if(flag)
				{
					removeData(start, end);
				}
				else
				{
					return false;
				}
				if(ja.length() < 500)
					break;
			}
		}
		catch(Exception e)
		{
			new Log().e(e.toString());
			return false;
		}
		return true;
	}

	
	
	private JSONArray readData() throws IOException, JSONException
	{
		JSONArray ja = new JSONArray();
		DataFormatter df = new DataFormatter();
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		while( (line = br.readLine()) != null)
		{
			if(line != null)
			{	
				ja.put(df.formatData(data_type, line));
			}
		}	
		br.close();
		fr.close();
		return ja;
	}

	private JSONArray readData(int start, int end) throws IOException, JSONException
	{
		JSONArray ja = new JSONArray();
		DataFormatter df = new DataFormatter();
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		int index = -1;
		while( (line = br.readLine()) != null)
		{
			index++;
			if(index < start || index > end)
				continue;

			if(line != null)
			{	
				ja.put(df.formatData(data_type, line));
			}
		}	
		br.close();
		fr.close();
		return ja;
	}

	private void removeData(int start, int end) throws IOException, JSONException
	{
		StringBuffer output = new StringBuffer();
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		int index = -1;
		while( (line = br.readLine()) != null)
		{
			index++;
			if(index >= start && index <= end)
				continue;

			if(line != null)
			{	
				output.append(line);
				output.append("\n");
			}
		}	
		br.close();
		fr.close();

		file.delete();
		file.createNewFile();
		
		FileWriter fw = new FileWriter(file, true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.append(output.toString());
		bw.close();
		fw.close();
	}
}