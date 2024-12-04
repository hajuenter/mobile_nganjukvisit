package com.polije.sem3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.polije.sem3.R;
import com.polije.sem3.model.EventModel;
import com.polije.sem3.network.Client;

import java.util.ArrayList;

public class ViewpagerAdapter extends RecyclerView.Adapter<ViewpagerAdapter.ViewHolder> {
    ArrayList<EventModel> datalist;

    public ViewpagerAdapter(ArrayList<EventModel> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public ViewpagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewpager_event, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewpagerAdapter.ViewHolder holder, int position) {
        holder.txtTitle.setText(datalist.get(position).getNama());
        holder.txtLokasi.setText(datalist.get(position).getLokasi());
        holder.txtJadwal.setText(datalist.get(position).getHari() + ", " + EventModelAdapter.convertToDate1(datalist.get(position).getTanggaldanwaktu()));
        Glide.with(holder.itemView.getContext())
                .load(Client.IMG_DATA + getFirstImage(datalist.get(position).getGambar()))
                .into(holder.imgView);

    }
    private String getFirstImage(String gambar) {
        // Cek jika ada koma (berarti ada lebih dari satu gambar)
        if (gambar.contains(",")) {
            // Pisahkan string gambar berdasarkan koma dan ambil gambar pertama
            String[] images = gambar.split(",");
            return images[0].trim(); // Mengembalikan gambar pertama setelah dipangkas spasi
        } else {
            // Jika hanya ada satu gambar, kembalikan nama gambar tersebut
            return gambar.trim();
        }
    }
    @Override
    public int getItemCount() {
        return (datalist != null) ? datalist.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtTitle;
        private final TextView txtLokasi;
        private final TextView txtJadwal;
        private final ImageView imgView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.judulEvent);
            txtLokasi = itemView.findViewById(R.id.lokasiEvent);
            txtJadwal = itemView.findViewById(R.id.jadwalEvent);
            imgView = itemView.findViewById(R.id.imageViewevent);
        }
    }
}
