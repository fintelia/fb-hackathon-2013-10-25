package com.example.photofabricator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

public class DrawActivity2 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity2);

		ImageView image = (ImageView) findViewById(R.id.image_saved);

		ByteArrayInputStream imageStreamClient = new ByteArrayInputStream(
				getIntent().getExtras().getByteArray("draw"));
		Bitmap bmp = BitmapFactory.decodeStream(imageStreamClient);

		image.setImageBitmap(bmp);

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"PhotoFabricator");
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

		String filename = mediaStorageDir.getPath() + File.separator
				+ "DRAW_" + timeStamp + ".jpg";

		try {
			FileOutputStream out = new FileOutputStream(filename);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (FileNotFoundException e) { }
		
		FileUploader.uploadFile(filename);

	}

}