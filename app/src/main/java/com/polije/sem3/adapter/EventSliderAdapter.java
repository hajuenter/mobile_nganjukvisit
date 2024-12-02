package com.polije.sem3.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventSliderAdapter extends RecyclerView.Adapter<EventSliderAdapter.EventSliderViewHolder> {
    @NonNull
    @Override
    public EventSliderAdapter.EventSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull EventSliderAdapter.EventSliderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class EventSliderViewHolder extends RecyclerView.ViewHolder{
        public EventSliderViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
