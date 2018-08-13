package com.adhiwie.moodjournal.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.adhiwie.moodjournal.utils.Log;

public class HttpHelper {
	

	private final URL url;
	
	public HttpHelper(URL url) 
	{
		this.url = url;
	}

	public boolean sendData(String data)
	{
		HttpURLConnection con = null;
		try 
		{
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("POST");
			
			OutputStream out = new BufferedOutputStream(con.getOutputStream());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8")); 
			writer.write(data);
			writer.flush();
			writer.close();

			int response_code = con.getResponseCode();
			new Log().v("HttpHelper Response Code -- " + response_code);
			return response_code == HttpURLConnection.HTTP_OK;
		}
		catch(IOException e)
		{
			new Log().e(e.toString());
		}
		finally 
		{
			if(con != null)
				con.disconnect();
		}
		return false;
	}

	public String getData()
	{
		String result = "";
		HttpURLConnection con = null;
		try
		{
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setReadTimeout(10000);
			con.setConnectTimeout(10000);
			con.connect();

			InputStream in = con.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			int response_code = con.getResponseCode();
			if(response_code == HttpURLConnection.HTTP_OK)
				result = reader.readLine();
			reader.close();

		} catch (IOException e) {
			new Log().e(e.toString());
		}
		finally
		{
			if(con != null)
				con.disconnect();
		}
		return result;

	}
}
