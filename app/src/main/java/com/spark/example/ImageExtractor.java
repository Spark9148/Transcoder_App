package com.spark.example;

import android.content.Context;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;

public class ImageExtractor {
    private final Context context;
    private ArrayList<ImageFile> imageFiles; // Use MediaFile list
    private final ArrayList<String> defaultIExtensions = new ArrayList<>();

    public ImageExtractor(Context context) {
        this.context = context;
        defaultIExtensions.add(".jpg");
        defaultIExtensions.add(".jpeg");
        defaultIExtensions.add(".png");
        defaultIExtensions.add(".gif");
        // Add other image extensions as needed
    }

    public void addMoreExtensions(ArrayList<String> e) {
        defaultIExtensions.addAll(e);
    }

    private ArrayList<String> getRootPaths() {
        ArrayList<String> rootPaths = new ArrayList<>();
        File[] rootsStorage = ContextCompat.getExternalFilesDirs(context, null);
        for (File file : rootsStorage) {
            String root = file.getAbsolutePath().replace("/Transcoder/outputs" + context.getPackageName() + "/files", "");
            rootPaths.add(root);
        }
        return rootPaths;
    }

    public ArrayList<ImageFile> listImages() {
        imageFiles = new ArrayList<>();
        ArrayList<String> rootPaths = getRootPaths();
        for (String path : rootPaths) {
            getImages(new File(path));
        }
        return imageFiles;
    }

    private void getImages(File folder) {
        File[] folders1 = folder.listFiles();
        if (folders1 != null) {
            for (File file : folders1) {
                String name = file.getName();
                if (file.isDirectory()) {
                    getImages(file);
                } else {
                    int i = name.lastIndexOf(".");
                    String exe = i == -1 ? "" : name.substring(i);
                    if (defaultIExtensions.contains(exe)) {
                        imageFiles.add(new ImageFile(file.getName(), file.getAbsolutePath(), file.length(), file.lastModified()));
                    }
                }
            }
        }
    }
}
