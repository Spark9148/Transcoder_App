//package com.spark.example;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.media.MediaScannerConnection;
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.spark.example.transcoder.ThumbnailerActivity;
//import com.spark.example.transcoder.TranscoderActivity;
//
//import java.io.File;
//import java.util.ArrayList;
//
//public class MainActivity extends AppCompatActivity {
//
//
//    private VideoDirectoryObserver observer;
//    private VideoAdapter videoAdapter;
//    private ImageAdapter imageAdapter;
//    private ImageDirectoryObserver imageObserver;
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        FilesExtractor filesExtractor = new FilesExtractor(MainActivity.this);
//        ImageExtractor imageExtractor = new ImageExtractor(MainActivity.this);
//// call if need to add more extensions
//// filesExtractor.addMoreExtensions(moreExtensions);
//        ArrayList<VideoFile> videoFiles = filesExtractor.listVideos();
//        ArrayList<ImageFile> imageFiles = imageExtractor.listImages();
//// now these video files we can show in RecyclerView
//
//
//        // Initialize RecyclerView
//        RecyclerView recyclerView = findViewById(R.id.recyclerView); // Assuming you have a RecyclerView in your layout
////        VideoAdapter videoAdapter = new VideoAdapter(videoFiles);
//        videoAdapter = new VideoAdapter(videoFiles);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(videoAdapter);
//
//        RecyclerView recyclerViewimg = findViewById(R.id.recyclerViewImg); // Assuming you have a RecyclerView in your layout
//        imageAdapter = new ImageAdapter(imageFiles);
//        recyclerViewimg.setLayoutManager(new LinearLayoutManager(this));
//        recyclerViewimg.setAdapter(imageAdapter);
//
//
////        SyncScrollListener listener1 = new SyncScrollListener(recyclerViewimg);
////        SyncScrollListener listener2 = new SyncScrollListener(recyclerView);
////
////        recyclerView.addOnScrollListener(listener1);
////        recyclerViewimg.addOnScrollListener(listener2);
//
//
//        FloatingActionButton thumbnailerFab = findViewById(R.id.thumbnailer);
//        FloatingActionButton transcoderFab = findViewById(R.id.transcoder);
//
//        thumbnailerFab.setOnClickListener(view -> {
//            startActivity(new Intent(MainActivity.this, ThumbnailerActivity.class));
//        });
//
//        transcoderFab.setOnClickListener(view -> {
//            startActivity(new Intent(MainActivity.this, TranscoderActivity.class));
//        });
//
//
//        //FileObserver
//        File videoDirectory = new File(getExternalFilesDir(null), "Output_Videos"); // Adjust path if needed
//        observer = new VideoDirectoryObserver(videoDirectory, () -> {
//            // This callback is executed when the directory changes
//            // Rescan for video files and update the adapter
//            scanForVideos(videoDirectory);
//            ArrayList<VideoFile> updatedVideos = getUpdatedVideoFiles();
//            videoAdapter.refreshData(updatedVideos);
//        });
//        observer.startWatching();
//
//        File imageDirectory = new File(getExternalFilesDir(null), "Output_Thumbnails"); // Adjust path if needed
//        ImageDirectoryObserver imageObserver = new ImageDirectoryObserver(imageDirectory, () -> {
//            // This callback is executed when the directory changes
//            scanForImages(imageDirectory); // Call scanForImages to refresh the image list
//        });
//        imageObserver.startWatching();
//    }
//    private void scanForVideos(File Output_Videos) {
//        MediaScannerConnection.scanFile(this,
//                new String[] { Output_Videos.getAbsolutePath() },null,
//                (path, uri) -> {
//                    // This callback is executed when the scanning is complete
//                    // You can now call getUpdatedVideoFiles() to get the updated list
//                    runOnUiThread(() -> {
//                        ArrayList<VideoFile> updatedVideos = getUpdatedVideoFiles();
//                        videoAdapter.refreshData(updatedVideos);
//                    });
//                });
//    }
//
//    private void scanForImages(File imageDirectory) {
//        MediaScannerConnection.scanFile(this,
//                new String[] { imageDirectory.getAbsolutePath() }, null,
//                (path, uri) -> {
//                    runOnUiThread(() ->{
//                        // Get updated files and refresh adapter AFTER scan is complete
//                        ArrayList<ImageFile> updatedImages = getUpdatedImageFiles();
//                        imageAdapter.refreshData(updatedImages);
//                    });
//                });
//    }
//
//    private ArrayList<ImageFile> getUpdatedImageFiles() {
//        File imageDirectory = new File(getExternalFilesDir(null), "Output_Thumbnails"); // Adjust path if needed
//        ArrayList<ImageFile> updatedImages = new ArrayList<>();
//        if (imageDirectory.exists() && imageDirectory.isDirectory()) {
//            File[] files = imageDirectory.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    if (file.isFile() && isImageFile(file)) {
//                        updatedImages.add(new ImageFile(file.getName(), file.getAbsolutePath(), file.length(), file.lastModified()));
//                    }
//                }
//            }
//        }
//        return updatedImages;
//    }
//    private boolean isImageFile(File file) {
//        String fileName = file.getName().toLowerCase();
//        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".gif"); // Add other image extensions as needed
//    }
//
//    private ArrayList<VideoFile> getUpdatedVideoFiles() {
//        File videoDirectory = new File(getExternalFilesDir(null), "Output_Videos"); // Adjust path if needed
//        ArrayList<VideoFile> updatedVideos = new ArrayList<>();
//        if (videoDirectory.exists() && videoDirectory.isDirectory()) {
//            File[] files = videoDirectory.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    if (file.isFile() && isVideoFile(file)) { // Check if it's a video file
//                        // Create a VideoFile object and add it to the list
//                        updatedVideos.add(new VideoFile(file.getName(), file.getAbsolutePath(), file.length(), file.length()));
//                    }
//                }
//            }
//        }
//        return updatedVideos;
//    }
//    private boolean isVideoFile(File file) {
//        String fileName = file.getName().toLowerCase();
//        return fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mkv");// ... add other video extensions
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (observer != null) {
//            observer.stopWatching();
//        }
//        if (imageObserver != null) { // Add this to stop the image observer
//            imageObserver.stopWatching();
//        }
//    }
//
//}
//
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