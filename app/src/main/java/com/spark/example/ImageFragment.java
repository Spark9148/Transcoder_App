package com.spark.example;

import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class ImageFragment extends Fragment {

    private ImageAdapter imageAdapter;
    private ImageDirectoryObserver observer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewImg);
        imageAdapter = new ImageAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(imageAdapter);

        setupFileObserver();

        return view;
    }

    private void setupFileObserver() {
        File imageDirectory = new File(requireContext().getExternalFilesDir(null), "Output_Thumbnails");
        observer = new ImageDirectoryObserver(imageDirectory, () -> {
            scanForImages(imageDirectory);
            ArrayList<ImageFile> updatedImages = getUpdatedImageFiles();
            imageAdapter.refreshData(updatedImages);
        });
        observer.startWatching();

        ImageExtractor imageExtractor = new ImageExtractor(requireContext());
        ArrayList<ImageFile> imageFiles = imageExtractor.listImages();
        imageAdapter.refreshData(imageFiles);
    }

    private void scanForImages(File imageDirectory) {
        MediaScannerConnection.scanFile(requireContext(),
                new String[]{imageDirectory.getAbsolutePath()}, null,
                (path, uri) -> {
                    requireActivity().runOnUiThread(() -> {
                        ArrayList<ImageFile> updatedImages = getUpdatedImageFiles();
                        imageAdapter.refreshData(updatedImages);
                    });
                });
    }

    private ArrayList<ImageFile> getUpdatedImageFiles() {
        File imageDirectory = new File(requireContext().getExternalFilesDir(null), "Output_Thumbnails");
        ArrayList<ImageFile> updatedImages = new ArrayList<>();
        if (imageDirectory.exists() && imageDirectory.isDirectory()) {
            File[] files = imageDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isImageFile(file)) {
                        updatedImages.add(new ImageFile(file.getName(), file.getAbsolutePath(), file.length(), file.lastModified()));
                    }
                }
            }
        }
        return updatedImages;
    }

    private boolean isImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".gif"); // Add other extensions as needed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (observer != null) {
            observer.stopWatching();
        }
    }
}