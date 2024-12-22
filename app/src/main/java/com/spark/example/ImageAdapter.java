package com.spark.example;

import static android.text.format.Formatter.formatFileSize;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private ArrayList<ImageFile> imageFiles;

    public ImageAdapter(ArrayList<ImageFile> imageFiles) {
        this.imageFiles = imageFiles;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false); // Use item_image layout
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageFile imageFile = imageFiles.get(position);

        holder.titleTextView.setText(imageFile.getTitle());
        String formattedSize = formatFileSize(imageFile.getSize()); // Format size
        holder.sizeTextView.setText(formattedSize); // Set size

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String lastModified = dateFormat.format(new Date(imageFile.getLastModified())); // Format date
        holder.lastModifiedTextView.setText(lastModified); // Set lastmodified

        Glide.with(holder.itemView.getContext())
                .load(imageFile.getUri())
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background) // Optional placeholder
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            File imageFileToOpen = new File(imageFile.getPath());
            Uri contentUri = FileProvider.getUriForFile(
                    holder.itemView.getContext(),
                    "com.spark.example.fileprovider", // Use your authority here
                    imageFileToOpen);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(contentUri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            holder.itemView.getContext().startActivity(intent);
        });
    }

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

    @Override
    public int getItemCount() {
        return imageFiles.size();
    }

    public void refreshData(ArrayList<ImageFile> newImageFiles) {
        this.imageFiles.clear();
        this.imageFiles.addAll(newImageFiles);
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView sizeTextView;
        TextView lastModifiedTextView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.thumbnailimageView);
            titleTextView = itemView.findViewById(R.id.title_TextView);
            sizeTextView = itemView.findViewById(R.id.size_TextView);
            lastModifiedTextView = itemView.findViewById(R.id.LastModified_TextView);
        }
    }
}