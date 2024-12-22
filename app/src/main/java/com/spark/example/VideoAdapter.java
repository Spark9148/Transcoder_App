package com.spark.example;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private ArrayList<VideoFile> videoFiles;
    private String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        } else if (sizeInBytes < 1048576) {
            return (sizeInBytes / 1024) + " KB";
        } else if (sizeInBytes < 1073741824) {
            return (sizeInBytes / 1048576) + " MB";
        } else {
            return (sizeInBytes / 1073741824) + " GB";
        }
    }

    public VideoAdapter(ArrayList<VideoFile> videoFiles) {
        this.videoFiles = videoFiles;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoFile videoFile = videoFiles.get(position);
        Glide.with(holder.itemView.getContext())
                .load(videoFile.getUri()) // VideoFile has a getUri() method
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background) // Optional placeholder image
                .into(holder.thumbnailImageView);

        holder.titleTextView.setText(videoFile.getTitle());
        String formattedSize = formatFileSize(videoFile.getSize());
        holder.sizeTextView.setText(formattedSize); // Set the formatted size

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String lastModified = dateFormat.format(new Date(videoFile.getLastModified()));
        holder.lastModifiedTextView.setText(lastModified);

        holder.itemView.setOnClickListener(v -> {
            File videoFileToPlay = new File(videoFile.getPath()); // Get the actualFile object
            Uri contentUri = FileProvider.getUriForFile(
                    holder.itemView.getContext(),
                    "com.spark.example.fileprovider", // Use your authority name here
                    videoFileToPlay);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(contentUri, "video/*");intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            holder.itemView.getContext().startActivity(intent);
        });
    }
    public void refreshData(ArrayList<VideoFile> newVideoFiles) {
        this.videoFiles.clear();
        this.videoFiles.addAll(newVideoFiles);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView titleTextView;
        TextView sizeTextView;
        TextView lastModifiedTextView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            sizeTextView = itemView.findViewById(R.id.sizeTextView);
            lastModifiedTextView = itemView.findViewById(R.id.LastModifiedTextView);
        }
    }
}