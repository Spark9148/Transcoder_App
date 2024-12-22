package com.spark.example;

import android.content.Context;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;

public class FilesExtractor {
    private final Context context;
    private ArrayList<VideoFile> videoFiles;
    private final ArrayList<String> defaultVExtensions = new ArrayList<>();

    public FilesExtractor(Context context) {
        this.context = context;
        defaultVExtensions.add(".mp4");
        defaultVExtensions.add(".mkv");
        defaultVExtensions.add(".webm");
        defaultVExtensions.add(".mpg");
        defaultVExtensions.add(".3gp");
        defaultVExtensions.add(".avi");
        defaultVExtensions.add(".tc");
    }
    public void addMoreExtensions(ArrayList<String> e){
        defaultVExtensions.addAll(e);
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
    public ArrayList<VideoFile> listVideos(){
        videoFiles = new ArrayList<>();
        ArrayList<String> rootPaths = getRootPaths();
        for (String path:rootPaths){
            getVideos(new File(path));
        }
        return videoFiles;
    }
    private void getVideos(File folder) {
        File[] folders1 = folder.listFiles();
        if (folders1!=null){
            for (File file :folders1){
                String name = file.getName();
                if (file.isDirectory()){
                    getVideos(file);
                }else {
                    int i = name.lastIndexOf(".");
                    String exe = i == -1 ? "" : name.substring(i);
                    if (defaultVExtensions.contains(exe)) videoFiles.add(new VideoFile(file.getName(),file.getAbsolutePath(),file.length(),file.lastModified()));
                }
            }
        }
    }
}
