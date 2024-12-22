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

public class VideoFragment extends Fragment {

    private VideoAdapter videoAdapter;
    private VideoDirectoryObserver observer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        videoAdapter = new VideoAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(videoAdapter);

        setupFileObserver();

        return view;}

    private void setupFileObserver() {
        File videoDirectory = new File(requireContext().getExternalFilesDir(null), "Output_Videos");
        observer = new VideoDirectoryObserver(videoDirectory, () -> {
            scanForVideos(videoDirectory);
            ArrayList<VideoFile> updatedVideos = getUpdatedVideoFiles();
            videoAdapter.refreshData(updatedVideos);
        });
        observer.startWatching();

        FilesExtractor filesExtractor = new FilesExtractor(requireContext());
        ArrayList<VideoFile> videoFiles = filesExtractor.listVideos();
        videoAdapter.refreshData(videoFiles);
    }

    private void scanForVideos(File Output_Videos) {
        MediaScannerConnection.scanFile(requireContext(),
                new String[]{Output_Videos.getAbsolutePath()}, null,
                (path, uri) -> {
                    if (isAdded()) { // Check if the fragment is still added
                        requireActivity().runOnUiThread(() -> {
                            ArrayList<VideoFile> updatedVideos = getUpdatedVideoFiles();
                            videoAdapter.refreshData(updatedVideos);
                        });
                    }
                });
    }
    private ArrayList<VideoFile> getUpdatedVideoFiles() {
        File videoDirectory = new File(requireContext().getExternalFilesDir(null), "Output_Videos");
        ArrayList<VideoFile> updatedVideos = new ArrayList<>();
        if (videoDirectory.exists() && videoDirectory.isDirectory()) {
            File[] files = videoDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isVideoFile(file)) {
                        updatedVideos.add(new VideoFile(file.getName(), file.getAbsolutePath(), file.length(), file.length()));
                    }
                }
            }
        }
        return updatedVideos;
    }

    private boolean isVideoFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mkv"); // Add other extensions as needed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (observer != null) {
            observer.stopWatching();
        }
    }
}