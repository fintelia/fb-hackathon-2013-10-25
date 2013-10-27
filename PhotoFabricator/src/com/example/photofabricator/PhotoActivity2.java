package com.example.photofabricator;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoActivity2 extends Activity {

	Bitmap photo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_activity2);
		Bundle extras = (Bundle) getIntent().getExtras().get("photo");
		photo = (Bitmap) extras.get("data");
		Toast.makeText(PhotoActivity2.this, "Test", 3000).show();
		ImageView image = (ImageView) findViewById(R.id.photo_saved);
		image.setImageBitmap(photo);
		
        final Button button = (Button) findViewById(R.id.noBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PhotoActivity2.this, TakePicturesActivity.class);
                startActivity(intent);
            }
        });
        
        final Button button2 = (Button) findViewById(R.id.yesBtn);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Utilities.uploadBitmap("DRAW_", photo);
        		
        		Toast.makeText(PhotoActivity2.this, "File Upload Was Successful!", Toast.LENGTH_LONG).show();
        		
        		Handler handler = new Handler(); 
        	    handler.postDelayed(new Runnable() { 
        	         public void run() { 
        	        	 Intent intent = new Intent(PhotoActivity2.this, Main.class);
        	                startActivity(intent);
        	         } 
        	    }, 3000);
            }
        });
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_activity2, menu);
		return true;
	}

}
