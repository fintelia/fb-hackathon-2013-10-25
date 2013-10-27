package com.example.photofabricator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jibble.simpleftp.SimpleFTP;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class Utilities {

	private static final String FTP_SERVER = "192.168.1.75";
	private static final String FTP_USERNAME = "anonymous";
	private static final String FTP_PASSWORD = "";

	
	public static void uploadViaHTTP(String path) {
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;

		String pathToOurFile = path;
		String urlServer = "http://192.168.1.75/upload.php";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;

		try
		{
		FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

		URL url = new URL(urlServer);
		connection = (HttpURLConnection) url.openConnection();

		// Allow Inputs & Outputs
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);

		// Enable POST method
		connection.setRequestMethod("POST");

		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

		outputStream = new DataOutputStream( connection.getOutputStream() );
		outputStream.writeBytes(twoHyphens + boundary + lineEnd);
		outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
		outputStream.writeBytes(lineEnd);

		bytesAvailable = fileInputStream.available();
		bufferSize = Math.min(bytesAvailable, maxBufferSize);
		buffer = new byte[bufferSize];

		// Read file
		bytesRead = fileInputStream.read(buffer, 0, bufferSize);

		while (bytesRead > 0)
		{
		outputStream.write(buffer, 0, bufferSize);
		bytesAvailable = fileInputStream.available();
		bufferSize = Math.min(bytesAvailable, maxBufferSize);
		bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		}

		outputStream.writeBytes(lineEnd);
		outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

		// Responses from the server (code and message)
		int serverResponseCode = connection.getResponseCode();
		String serverResponseMessage = connection.getResponseMessage();
		
		Log.w("Server response", ((Integer) serverResponseCode).toString() + " " + serverResponseMessage);

		fileInputStream.close();
		outputStream.flush();
		outputStream.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void uploadViaFTP(String path) {
		SimpleFTP ftp = new SimpleFTP();
		try {
			ftp.connect(FTP_SERVER, 21, FTP_USERNAME, FTP_PASSWORD);
			ftp.bin();
			ftp.cwd("/");
			ftp.stor(new File(path));
			ftp.disconnect();
		} catch (IOException e) {
		}
	}
	public static void uploadFile(String path) {
		uploadViaHTTP(path);
	}
	
	public static String makeFilename(String prefix) {
    	File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"PhotoFabricator");
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		String filename = mediaStorageDir.getPath() + File.separator
				+ prefix + timeStamp + ".png";
		
		return filename;
	}
	
	public static void saveBitmapToFile(Bitmap bmp, String filename) {
		try {
			FileOutputStream out = new FileOutputStream(filename);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (FileNotFoundException e) { }
	}
	
	public static void uploadBitmap(String prefix, Bitmap bmp) {
    	String filename = Utilities.makeFilename(prefix);
    	Utilities.saveBitmapToFile(bmp, filename);	
		Utilities.uploadFile(filename);
	}
}
