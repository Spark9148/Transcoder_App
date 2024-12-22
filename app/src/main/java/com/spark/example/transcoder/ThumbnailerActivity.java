package com.spark.example.transcoder;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.otaliastudios.transcoder.ThumbnailerListener;
import com.otaliastudios.transcoder.ThumbnailerOptions;
import com.otaliastudios.transcoder.Transcoder;
import com.otaliastudios.transcoder.internal.utils.Logger;
import com.otaliastudios.transcoder.resize.AspectRatioResizer;
import com.otaliastudios.transcoder.resize.FractionResizer;
import com.otaliastudios.transcoder.source.DataSource;
import com.otaliastudios.transcoder.source.TrimDataSource;
import com.otaliastudios.transcoder.source.UriDataSource;
import com.otaliastudios.transcoder.thumbnail.Thumbnail;
import com.otaliastudios.transcoder.thumbnail.UniformThumbnailRequest;
import com.spark.example.R;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import kotlin.collections.ArraysKt;


public class ThumbnailerActivity extends AppCompatActivity implements
        ThumbnailerListener {

    private static final Logger LOG = new Logger("TranscoderActivity");

    private static final int REQUEST_CODE_PICK = 1;
    private static final int PROGRESS_BAR_MAX = 1000;
    private static final String FILE_PROVIDER_AUTHORITY = "com.spark.example.fileprovider";

    private RadioGroup mVideoResolutionGroup;
    private RadioGroup mVideoAspectGroup;
    private RadioGroup mVideoRotationGroup;

    private ProgressBar mProgressView;
    private TextView mButtonView;
    private EditText mTrimStartView;
    private EditText mTrimEndView;
    private ViewGroup mThumbnailsView;
    private long mTranscodeStartTime;
    private File mTranscodeOutputFile;

    private boolean mIsTranscoding;
    private Future<Void> mTranscodeFuture;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.setLogLevel(Logger.LEVEL_VERBOSE);
        setContentView(R.layout.activity_thumbnailer);

        mThumbnailsView = findViewById(R.id.thumbnails);
        mButtonView = findViewById(R.id.button);
        mButtonView.setOnClickListener(v -> {
            if (!mIsTranscoding) {
                startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT)
                        .setType("video/*")
                        .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true), REQUEST_CODE_PICK);
            } else {
                mTranscodeFuture.cancel(true);
            }
        });

        mProgressView = findViewById(R.id.progress);
        mTrimStartView = findViewById(R.id.trim_start);
        mTrimEndView = findViewById(R.id.trim_end);
        mVideoResolutionGroup = findViewById(R.id.resolution);
        mVideoAspectGroup = findViewById(R.id.aspect);
        mVideoRotationGroup = findViewById(R.id.rotation);
        setIsTranscoding(false, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK
                && resultCode == RESULT_OK
                && data != null) {
            if (data.getData() != null) {
                thumbnails(data.getData());
            } else if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                List<Uri> uris = new ArrayList<>();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    uris.add(clipData.getItemAt(i).getUri());
                }
                thumbnails(uris.toArray(new Uri[0]));
            }
        }
    }

    private void thumbnails(@NonNull Uri... uris) {
        LOG.e("Building sources...");
        List<DataSource> sources = ArraysKt.map(uris, uri -> new UriDataSource(this, uri));
        long trimStart = 0, trimEnd = 0;
        try {
            trimStart = Long.parseLong(mTrimStartView.getText().toString());
        } catch (NumberFormatException e) {
            LOG.w("Failed to read trimStart value.", e);
        }
        try {
            trimEnd = Long.parseLong(mTrimEndView.getText().toString());
        } catch (NumberFormatException e) {
            LOG.w("Failed to read trimEnd value.", e);
        }
        trimStart = Math.max(0, trimStart) * 1000000;
        trimEnd = Math.max(0, trimEnd) * 1000000;
        sources.set(0, new TrimDataSource(sources.get(0), trimStart, trimEnd));

        LOG.e("Building options...");
        ThumbnailerOptions.Builder builder = new ThumbnailerOptions.Builder();
        builder.addThumbnailRequest(new UniformThumbnailRequest(8));
        builder.setListener(this);
        for (DataSource source : sources) {
            builder.addDataSource(source);
        }

        //changes

        float aspectRatio;
        int m_id = mVideoResolutionGroup.getCheckedRadioButtonId();
        if(m_id == R.id.aspect_169) aspectRatio = 16F / 9F;
        else if(m_id == R.id.aspect_43) aspectRatio = 4F / 3F;
        else if(m_id == R.id.aspect_square) aspectRatio = 1F;
        else aspectRatio = 0F;


//        switch (id) {
//            case id == R.id.aspect_169: aspectRatio = 16F / 9F; break;
//            case id == R.id.aspect_43: aspectRatio = 4F / 3F; break;
//            case id == R.id.aspect_square: aspectRatio = 1F; break;
//            default: aspectRatio = 0F;
//        }

        if (aspectRatio > 0) {
            builder.addResizer(new AspectRatioResizer(aspectRatio));
        }
        float fraction;
        int f_id= mVideoAspectGroup.getCheckedRadioButtonId();
        if(f_id == R.id.resolution_half) fraction = 0.5F;
        else if(f_id == R.id.resolution_third) fraction = 1F / 3F;
        else fraction = 1F;


//        switch (mVideoResolutionGroup.getCheckedRadioButtonId()) {
//            case resolution_half:fraction = 0.5F; break;
//            case resolution_third:fraction = 1F / 3F;  break;
//            default: fraction = 1F;
//        }

        builder.addResizer(new FractionResizer(fraction));
        int rotation;

        int r_id = mVideoRotationGroup.getCheckedRadioButtonId();
        if(r_id == R.id.rotation_90) rotation = 90;

        else if(r_id == R.id.rotation_180) rotation = 180;
        else if(r_id == R.id.rotation_270) rotation = 270;
        else rotation = 0;

//        switch (mVideoRotationGroup.getCheckedRadioButtonId()) {
//            case rotation_90: rotation = 90; break;
//            case rotation_180: rotation = 180; break;
//            case rotation_270: rotation = 270; break;
//            default: rotation = 0;
//        }
        builder.setRotation(rotation);

        // Launch the transcoding operation.
        LOG.e("Starting transcoding!");
        setIsTranscoding(true, null);
        mTranscodeFuture = builder.thumbnails();
    }

    @Override
    public void onThumbnail(@NotNull Thumbnail thumbnail) {
        float size = TypedValue.applyDimension(COMPLEX_UNIT_DIP, 96, getResources().getDisplayMetrics());
        ImageView view = new ImageView(this);
        view.setLayoutParams(new ViewGroup.LayoutParams((int) size, (int) size));
        view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        view.setBackgroundColor(getResources().getColor(android.R.color.white, getTheme()));
        view.setImageBitmap(thumbnail.getBitmap());
        mThumbnailsView.addView(view);
        double progress = (float) mThumbnailsView.getChildCount() / 8;
        mProgressView.setIndeterminate(false);
        mProgressView.setProgress((int) Math.round(progress * PROGRESS_BAR_MAX));


        try {
            File outputDir = new File(getExternalFilesDir(null), "Output_Thumbnails");
            if (!outputDir.exists()){
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, "thumbnail_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(outputFile);
            thumbnail.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            LOG.i("Thumbnail saved to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            LOG.e("Error saving thumbnail:", e);
        }
    }


    @Override
    public void onThumbnailsCanceled() {
        setIsTranscoding(false, "Operation canceled.");
    }

    @Override
    public void onThumbnailsFailed(@NotNull Throwable exception) {
        setIsTranscoding(false, "Error occurred. " + exception.getMessage());
    }

    @Override
    public void onThumbnailsCompleted(@NotNull List<Thumbnail> thumbnails) {
        setIsTranscoding(false, "Extracted " + thumbnails.size() + " thumbnails.");

        if (!thumbnails.isEmpty()) {
            File outputDir = new File(getExternalFilesDir(null), "thumbnails");
            File[] files = outputDir.listFiles();
            if (files != null && files.length > 0) {
                File firstThumbnail = files[0];
                Uri uri = FileProvider.getUriForFile(ThumbnailerActivity.this,
                        FILE_PROVIDER_AUTHORITY, firstThumbnail);
                startActivity(new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(uri, "image/jpeg")
                        .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
            }
        }
        finish();
        }


    private void setIsTranscoding(boolean isTranscoding, @Nullable String message) {
        mProgressView.setMax(PROGRESS_BAR_MAX);
        mProgressView.setProgress(0);
        if (isTranscoding) {
            mThumbnailsView.removeAllViews();
        }
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        mIsTranscoding = isTranscoding;
        mButtonView.setText(mIsTranscoding ? "Cancel Thumbnails" : "Select Videos & Transcode");
    }
}
