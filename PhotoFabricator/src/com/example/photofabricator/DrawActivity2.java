package com.example.photofabricator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class DrawActivity2 extends Activity {

	public Bitmap bmp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity2);

		final Button button = (Button) findViewById(R.id.yesButton);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Utilities.uploadBitmap("DRAW_", bmp);

				Toast.makeText(DrawActivity2.this,
						"File Upload Was Successful!", Toast.LENGTH_LONG)
						.show();

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						Intent intent = new Intent(DrawActivity2.this,
								Main.class);
						startActivity(intent);
					}
				}, 3000);
			}
		});

		final Button button2 = (Button) findViewById(R.id.noButton);
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DrawActivity2.this,
						DrawActivity1.class);
				startActivity(intent);
			}
		});
		
		final Button button3 = (Button) findViewById(R.id.cancelButton);
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(DrawActivity2.this,
						Main.class);
				startActivity(intent);
			}
		});

		ImageView image = (ImageView) findViewById(R.id.image_saved);

		ByteArrayInputStream imageStreamClient = new ByteArrayInputStream(
				getIntent().getExtras().getByteArray("draw"));
		bmp = BitmapFactory.decodeStream(imageStreamClient);

		image.setImageBitmap(bmp);

	}

	@Override
	public void onBackPressed() {
	}

}