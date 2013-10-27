package com.example.photofabricator;

import java.io.FileNotFoundException;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class PhotoActivity2 extends Activity {

	/**
	 * Boolean that tells me how to treat a transparent pixel (Should it be
	 * black?)
	 */
	private static final boolean TRANSPARENT_IS_BLACK = false;
	private double breakingPoint = 0.8;

	Bitmap photo;
	Bitmap binarizedImage;
	ImageView binImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_activity2);

		if (getIntent().hasExtra("fromGallery")) {
			Intent imageReturnedIntent = (Intent) getIntent().getExtras().get(
					"photo");
			Uri selectedImage = imageReturnedIntent.getData();
			try {
				photo = decodeUri(selectedImage);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		else {
			Bundle extras = (Bundle) getIntent().getExtras().get("photo");
			photo = (Bitmap) extras.get("data");
		}

		binarizeImage();

		ImageView image = (ImageView) findViewById(R.id.photo_saved);
		binImage = (ImageView) findViewById(R.id.binary_view);
		image.setImageBitmap(photo);
		reshow();

		final Button button = (Button) findViewById(R.id.noBtn);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PhotoActivity2.this, Main.class);
				intent.putExtra("retake", true);
				startActivity(intent);
			}
		});

		final Button button2 = (Button) findViewById(R.id.yesBtn);
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Utilities.uploadBitmap("DRAW_", binarizedImage);

				Toast.makeText(PhotoActivity2.this,
						"File Upload Was Successful!", Toast.LENGTH_LONG)
						.show();

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						Intent intent = new Intent(PhotoActivity2.this,
								Main.class);
						startActivity(intent);
					}
				}, 3000);
			}
		});

		final Button button3 = (Button) findViewById(R.id.cancelBtn);
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PhotoActivity2.this, Main.class);
				startActivity(intent);
			}
		});
		
		final Button button4 = (Button) findViewById(R.id.galleryBtn);
		button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PhotoActivity2.this, Main.class);
				intent.putExtra("gallery", true);
				startActivity(intent);
			}
		});

		final SeekBar sk = (SeekBar) findViewById(R.id.seekBar1);
		sk.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				breakingPoint = (double) seekBar.getProgress() / 100;
				binarizeImage(); // Using new settings
				reshow();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
		});

		CheckBox negColors = (CheckBox) findViewById(R.id.negColors);

		negColors
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						negateColors();
						reshow();
					}
				});

	}

	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(
				getContentResolver().openInputStream(selectedImage), null, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 140;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(
				getContentResolver().openInputStream(selectedImage), null, o2);

	}

	public void binarizeImage() {
		binarizedImage = photo.copy(Bitmap.Config.ARGB_8888, true);
		for (int i = 0; i < binarizedImage.getWidth(); i++) {
			for (int c = 0; c < binarizedImage.getHeight(); c++) {
				int pixel = binarizedImage.getPixel(i, c);
				if (shouldBeBlack(pixel))
					binarizedImage.setPixel(i, c, Color.BLACK);
				else
					binarizedImage.setPixel(i, c, Color.WHITE);
			}
		}
	}

	public void reshow() {
		binImage.setImageBitmap(binarizedImage);
	}

	public void negateColors() {
		for (int i = 0; i < binarizedImage.getWidth(); i++) {
			for (int c = 0; c < binarizedImage.getHeight(); c++) {
				int pixel = binarizedImage.getPixel(i, c);
				if (pixel == Color.BLACK)
					binarizedImage.setPixel(i, c, Color.WHITE);
				else
					binarizedImage.setPixel(i, c, Color.BLACK);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_activity2, menu);
		return true;
	}

	/**
	 * @param pixel
	 *            the pixel that we need to decide on
	 * @return boolean indicating whether this pixel should be black
	 */
	private boolean shouldBeBlack(int pixel) {
		int alpha = Color.alpha(pixel);
		int redValue = Color.red(pixel);
		int blueValue = Color.blue(pixel);
		int greenValue = Color.green(pixel);
		if (alpha == 0x00) // if this pixel is transparent let me use
							// TRASNPARENT_IS_BLACK
			return TRANSPARENT_IS_BLACK;
		// distance from the white extreme
		double distanceFromWhite = Math.sqrt(Math.pow(0xff - redValue, 2)
				+ Math.pow(0xff - blueValue, 2)
				+ Math.pow(0xff - greenValue, 2));
		// distance from the black extreme //this should not be computed and
		// might be as well a function of distanceFromWhite and the whole
		// distance
		double distanceFromBlack = Math.sqrt(Math.pow(0x00 - redValue, 2)
				+ Math.pow(0x00 - blueValue, 2)
				+ Math.pow(0x00 - greenValue, 2));
		// distance between the extremes //this is a constant that should not be
		// computed :p
		double distance = distanceFromBlack + distanceFromWhite;
		// distance between the extremes
		return ((distanceFromWhite / distance) > breakingPoint);
	}

	@Override
	public void onBackPressed() {
	}
}
