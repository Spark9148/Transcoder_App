package com.spark.example;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spark.example.transcoder.ThumbnailerActivity;
import com.spark.example.transcoder.TranscoderActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) { // Check if it's the initial creation
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new VideoFragment())
                    .commit();
        }

        // Get the app-specific external files directory
        File externalFilesDir = getExternalFilesDir(null);

        // Create Output_Thumbnails directory if it doesn't exist
        File thumbnailsDir = new File(externalFilesDir, "Output_Thumbnails");if (!thumbnailsDir.exists()) {
            thumbnailsDir.mkdirs();
        }

        // Create Output_Videos directory if it doesn't exist
        File videosDir = new File(externalFilesDir, "Output_Videos");
        if (!videosDir.exists()) {
            videosDir.mkdirs();
        }

        Button videoButton = findViewById(R.id.VideoButton);
        Button imageButton = findViewById(R.id.ImageButton);

        videoButton.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new VideoFragment())
                    .commit();
        });

        imageButton.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ImageFragment())
                    .commit();
        });

        FloatingActionButton thumbnailerFab = findViewById(R.id.thumbnailer);
        FloatingActionButton transcoderFab = findViewById(R.id.transcoder);

        thumbnailerFab.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ThumbnailerActivity.class));
        });

        transcoderFab.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, TranscoderActivity.class));
        });
    }
}
