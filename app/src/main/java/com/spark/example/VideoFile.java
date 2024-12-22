package com.spark.example;

import android.net.Uri;

import java.io.File;

public class VideoFile {
    public String title,path;
    public long size,lastModified;
    public Uri getUri() {
        return Uri.fromFile(new File(path));}

    public VideoFile(String title, String path, long size, long lastModified) {
        this.title = title;
        this.path = path;
        this.size = size;
        this.lastModified = lastModified;
    }
    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public long getLastModified() {
        return lastModified;
    }

    // Setter methods (if needed)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}