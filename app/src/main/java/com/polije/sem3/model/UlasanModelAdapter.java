package com.polije.sem3.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.polije.sem3.R;

import java.util.ArrayList;

public class UlasanModelAdapter extends RecyclerView.Adapter<UlasanModelAdapter.UlasanModelViewHolder> {
    private ArrayList<UlasanModel> dataList;


    @NonNull
    @Override
    public UlasanModelAdapter.UlasanModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_row_ulasan, parent, false);     // layoutfordisplay
        return new UlasanModelViewHolder(view);
    }

    public UlasanModelAdapter(ArrayList<UlasanModel> dataList) {
        this.dataList = dataList;
    }

    @Override
    public void onBindViewHolder(UlasanModelAdapter.UlasanModelViewHolder holder, int position) {
        holder.namaPengguna.setText(dataList.get(position).getNamaPengguna());
        holder.tanggal.setText(dataList.get(position).getDateTime());
        holder.pesan.setText(dataList.get(position).getUlasan());
        holder.rating.setText("Rating : "+dataList.get(position).getrating());
        holder.ratingBar.setRating(Float.parseFloat(dataList.get(position).getrating()));
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class UlasanModelViewHolder extends RecyclerView.ViewHolder {
        private TextView namaPengguna, tanggal, pesan, rating;
        private RatingBar ratingBar;
        public UlasanModelViewHolder(View itemView) {
            super(itemView);
            namaPengguna = (TextView) itemView.findViewById(R.id.namaPengguna);
            tanggal = (TextView) itemView.findViewById(R.id.tanggalKomen);
            pesan = (TextView) itemView.findViewById(R.id.contentUlasan);
            rating = (TextView) itemView.findViewById(R.id.rating);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
    }
}
