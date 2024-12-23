package com.polije.sem3.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.polije.sem3.R;
import com.polije.sem3.model.KulinerModel;
import com.polije.sem3.response.FavoritKulinerResponse;
import com.polije.sem3.response.FavoritPenginapanResponse;
import com.polije.sem3.network.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RekomendasiKulinerAdapter extends RecyclerView.Adapter<RekomendasiKulinerAdapter.RekomendasiKulinerViewHolder> {
    private final ArrayList<KulinerModel> dataList;
    private final OnClickListener tampil;

    @NonNull
    @Override
    public RekomendasiKulinerAdapter.RekomendasiKulinerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_row_kuliner_slide, parent, false);     // layoutfordisplay
        return new RekomendasiKulinerViewHolder(view);
    }

    public RekomendasiKulinerAdapter(ArrayList<KulinerModel> dataList, OnClickListener listener) {
        this.dataList = dataList;
        this.tampil = listener;
    }

    @Override
    public void onBindViewHolder(RekomendasiKulinerAdapter.RekomendasiKulinerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UsersUtil usersUtil = new UsersUtil(holder.itemView.getContext());
        String idPengguna = usersUtil.getId();
        KulinerModel kuliner = dataList.get(position);

        holder.titleTxt.setText(dataList.get(position).getNama());
        Glide.with(holder.itemView.getContext())
                .load(Client.IMG_DATA + getFirstImage(dataList.get(position).getGambar()))
                .into(holder.imgKuliner);
        checkFavoriteStatus(idPengguna, kuliner.getIdKuliner(), holder.imgFavs);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    tampil.onItemClick(position);
                }
            }
        });

        Client.getInstance().cekfavkuliner("cek","kuliner",idPengguna, dataList.get(position).getIdKuliner()).enqueue(new Callback<FavoritKulinerResponse>() {
            @Override
            public void onResponse(Call<FavoritKulinerResponse> call, Response<FavoritKulinerResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("alreadyex")) {
                    holder.imgFavs.setImageResource(R.drawable.favorite_button_danger);
                    holder.imgFavs.setTag("favorited");
                }else {
                    holder.imgFavs.setImageResource(R.drawable.favorite_button_white);
                }
            }

            @Override
            public void onFailure(Call<FavoritKulinerResponse> call, Throwable t) {
                holder.imgFavs.setTag("not_favorited");
                Toast.makeText(holder.itemView.getContext(), "timeout", Toast.LENGTH_SHORT).show();
            }
        });

        holder.imgFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Periksa tag untuk menentukan drawable yang sedang ditampilkan
                if ("favorited".equals(holder.imgFavs.getTag())) {
                    // Jika sudah favorit, ubah ke non-favorit
                    holder.imgFavs.setImageResource(R.drawable.favorite_button_white);
                    holder.imgFavs.setTag("not_favorited");

                    // Tambahkan logika untuk menghapus dari favorit
                    Client.getInstance().deletefavkuliner("hapus", "kuliner", idPengguna, dataList.get(position).getIdKuliner()).enqueue(new Callback<FavoritKulinerResponse>() {
                        @Override
                        public void onResponse(Call<FavoritKulinerResponse> call, Response<FavoritKulinerResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FavoritKulinerResponse> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(), "timeout", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Jika belum favorit, ubah ke favorit
                    holder.imgFavs.setImageResource(R.drawable.favorite_button_danger);
                    holder.imgFavs.setTag("favorited");

                    // Tambahkan logika untuk menambahkan ke favorit
                    Client.getInstance().tambahfavkuliner("tambah", "kuliner", idPengguna, dataList.get(position).getIdKuliner()).enqueue(new Callback<FavoritKulinerResponse>() {
                        @Override
                        public void onResponse(Call<FavoritKulinerResponse> call, Response<FavoritKulinerResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FavoritKulinerResponse> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(), "timeout", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
    private void checkFavoriteStatus(String idPengguna, String idPenginapan, ImageView imgFavs) {
        Client.getInstance().cekfavpenginapan("cek","kuliner",idPengguna, idPenginapan).enqueue(new Callback<FavoritPenginapanResponse>() {
            @Override
            public void onResponse(Call<FavoritPenginapanResponse> call, Response<FavoritPenginapanResponse> response) {
                if (response.body() != null && "alreadyex".equalsIgnoreCase(response.body().getStatus())) {
                    imgFavs.setImageResource(R.drawable.favorite_button_danger);
                }else{
                    imgFavs.setImageResource(R.drawable.favorite_button_white);
                }
            }

            @Override
            public void onFailure(Call<FavoritPenginapanResponse> call, Throwable t) {
                Toast.makeText(imgFavs.getContext(), "timeout", Toast.LENGTH_SHORT).show();
            }
        });
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
        return (dataList != null) ? dataList.size() : 0;
    }

    public class RekomendasiKulinerViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTxt;
        private final ImageView imgKuliner;
        private final ImageView imgFavs;

        public RekomendasiKulinerViewHolder(View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.namaKuliner);
            imgKuliner = itemView.findViewById(R.id.imageViewKuliner);
            imgFavs = itemView.findViewById(R.id.buttonFavs);
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }
}
