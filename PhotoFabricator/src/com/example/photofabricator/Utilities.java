package com.example.photofabricator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jibble.simpleftp.SimpleFTP;

import android.graphics.Bitmap;
import android.os.Environment;

public class Utilities {

	private static final String FTP_SERVER = "192.168.1.73";
	private static final String FTP_USERNAME = "anonymous";
	private static final String FTP_PASSWORD = "";

	public static void uploadFile(String path) {
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
