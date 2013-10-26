package com.example.photofabricator;

import java.io.ByteArrayInputStream;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;


public class Activity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity2);

        ImageView image = (ImageView) findViewById(R.id.image_saved);

        ByteArrayInputStream imageStreamClient = new ByteArrayInputStream(
                getIntent().getExtras().getByteArray("draw"));
        image.setImageBitmap(BitmapFactory.decodeStream(imageStreamClient));
    }

}