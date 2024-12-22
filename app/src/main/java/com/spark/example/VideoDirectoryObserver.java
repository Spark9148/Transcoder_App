package com.spark.example;

import android.os.FileObserver;

import androidx.annotation.Nullable;

import java.io.File;

public class VideoDirectoryObserver extends FileObserver {

    private OnDirectoryChangeListener listener;

    public VideoDirectoryObserver(File directory, OnDirectoryChangeListener listener) {
        super(directory.getAbsolutePath(), FileObserver.CREATE | FileObserver.DELETE | FileObserver.MODIFY);
        this.listener = listener;
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        if (listener != null) {
            listener.onDirectoryChanged();
        }
    }

    public interface OnDirectoryChangeListener{
        void onDirectoryChanged();
    }
}
