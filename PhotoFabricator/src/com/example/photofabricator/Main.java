package com.example.photofabricator;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


public class Main extends Activity {

	private static final int CAPTURE_IMAGE = 100;
	private static final int SELECT_PHOTO = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (getIntent().hasExtra("retake")) {
			takePicture();
		}
		else if (getIntent().hasExtra("gallery")) {
			getGalleryPicture();
		}

		final Button button = (Button) findViewById(R.id.takePicturesButton);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				takePicture();
			}
		});

		final Button button2 = (Button) findViewById(R.id.drawPictureButton);
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Main.this, DrawActivity1.class);
				startActivity(intent);
			}
		});

		final Button button3 = (Button) findViewById(R.id.galleryButton);
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getGalleryPicture();
			}
		});

	}

	public void takePicture() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
	}

	public void getGalleryPicture() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE) {
			Intent intent = new Intent(Main.this, PhotoActivity2.class);
			intent.putExtra("photo", data.getExtras());
			startActivity(intent);
		} else if (requestCode == SELECT_PHOTO) {
			Intent intent = new Intent(Main.this, PhotoActivity2.class);
			intent.putExtra("photo", data);
			intent.putExtra("fromGallery", true);
			startActivity(intent);
            
		}
	}

}
