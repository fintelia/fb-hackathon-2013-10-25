package com.example.photofabricator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;
import org.jibble.simpleftp.SimpleFTP;

public class Main extends Activity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int numPictures = 5;
	private Uri fileUri;
	private int numPicturesTaken = 0;
	private ArrayList<Uri> uris = new ArrayList<Uri>();
	private static final String FTP_SERVER = "192.168.1.73";
	private static final String FTP_USERNAME = "anonymous";
	private static final String FTP_PASSWORD ="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ArrayList<Intent> intents = new ArrayList<Intent>();
		
		for(int i=0;i<numPictures;i++) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    intents.add(intent);    
		    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
		    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
		    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "PhotoFabricator");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("PhotoFabricator", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            Uri pictureUri = data.getData();
	            uris.add(pictureUri);
	            numPicturesTaken++;
	            if(numPicturesTaken == numPictures) {
            		SimpleFTP ftp = new SimpleFTP();
            		try {
	            		ftp.connect(FTP_SERVER,21,FTP_USERNAME,FTP_PASSWORD);
	            		ftp.bin();
	            		ftp.cwd("/");
		            	for(Uri i : uris) {
			            		ftp.stor(new File(i.toString()));
		            	}
		            	ftp.disconnect();
            		}
            		catch (Exception e) {}
            		Toast.makeText(getBaseContext(), "Completed", Toast.LENGTH_LONG).show();
	            }
	            
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }
	}

}
