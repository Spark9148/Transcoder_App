package com.spark.example;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SyncScrollListener extends RecyclerView.OnScrollListener {

    private RecyclerView otherRecyclerView;

    public SyncScrollListener(RecyclerView otherRecyclerView) {
        this.otherRecyclerView = otherRecyclerView;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (otherRecyclerView != null) {
            otherRecyclerView.removeOnScrollListener(this);
            otherRecyclerView.scrollBy(dx, dy);
            otherRecyclerView.addOnScrollListener(this);
        }
    }
}