package com.spark.example;

import android.os.FileObserver;

import androidx.annotation.Nullable;

import java.io.File;

public class ImageDirectoryObserver extends FileObserver {

    private OnDirectoryChangeListener listener;

    public ImageDirectoryObserver(File directory, OnDirectoryChangeListener listener) {
        super(directory.getAbsolutePath(), FileObserver.CREATE | FileObserver.DELETE | FileObserver.MODIFY);
        this.listener = listener;
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        if (listener != null && (event == FileObserver.CREATE || event == FileObserver.DELETE)) {
            // Only trigger the listener for CREATE and DELETE events
            listener.onDirectoryChanged();
        }
    }

    public interface OnDirectoryChangeListener {
        void onDirectoryChanged();
    }
}
