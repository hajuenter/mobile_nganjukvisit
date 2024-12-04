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
import com.polije.sem3.model.PenginapanModel;
import com.polije.sem3.response.FavoritPenginapanResponse;
import com.polije.sem3.response.FavoritWisataResponse;
import com.polije.sem3.network.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PenginapanModelAdapter extends RecyclerView.Adapter<PenginapanModelAdapter.PenginapanModelViewHolder>{
    private final ArrayList<PenginapanModel> dataList;
    private final OnClickListener tampil;

    @NonNull
    @Override
    public PenginapanModelAdapter.PenginapanModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_row_penginapan, parent, false);     // layoutfordisplay
        return new PenginapanModelViewHolder(view);
    }

    public PenginapanModelAdapter(ArrayList<PenginapanModel> datalist, OnClickListener listener) {
        this.dataList = datalist;
        this.tampil = listener;
    }

    @Override
    public void onBindViewHolder(PenginapanModelViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UsersUtil usersUtil = new UsersUtil(holder.itemView.getContext());
        String idPengguna = usersUtil.getId();

        holder.txtTitle.setText(dataList.get(position).getJudulPenginapan());
        holder.txtDesc.setText(dataList.get(position).getDeskripsi());
        Glide.with(holder.itemView.getContext())
                .load(Client.IMG_DATA + getFirstImage(dataList.get(position).getGambar()))
                .into(holder.imgView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampil.onItemClick(position);
            }
        });

        Client.getInstance().cekfavpenginapan("cek","penginapan",idPengguna, dataList.get(position).getIdPenginapan()).enqueue(new Callback<FavoritPenginapanResponse>() {
            @Override
            public void onResponse(Call<FavoritPenginapanResponse> call, Response<FavoritPenginapanResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("alreadyex")) {
                    holder.imgFavs.setImageResource(R.drawable.favorite_button_danger);
                    holder.imgFavs.setTag("favorited");
                }else {
                    holder.imgFavs.setImageResource(R.drawable.favorite_button_white);
                    holder.imgFavs.setTag("not_favorited");}
            }
            @Override
            public void onFailure(Call<FavoritPenginapanResponse> call, Throwable t) {
                Toast.makeText(holder.itemView.getContext(), "timeout", Toast.LENGTH_SHORT).show();
            }
        });

        holder.imgFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentTag = (String) holder.imgFavs.getTag(); // Ambil status saat ini

                if (currentTag.equals("favorited")) {
                    // Hapus favorit jika status saat ini 'favorite'
                    holder.imgFavs.setImageResource(R.drawable.favorite_button_white);
                    holder.imgFavs.setTag("not_favorited"); // Update tag

                    Client.getInstance().deletefavwisata("hapus", "penginapan", idPengguna, dataList.get(position).getIdPenginapan()).enqueue(new Callback<FavoritWisataResponse>() {
                        @Override
                        public void onResponse(Call<FavoritWisataResponse> call, Response<FavoritWisataResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FavoritWisataResponse> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(), "timeout", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Tambahkan ke favorit jika status saat ini 'not_favorite'
                    holder.imgFavs.setImageResource(R.drawable.favorite_button_danger);
                    holder.imgFavs.setTag("favorited"); // Update tag

                    Client.getInstance().tambahfavwisata("tambah", "penginapan", idPengguna, dataList.get(position).getIdPenginapan()).enqueue(new Callback<FavoritWisataResponse>() {
                        @Override
                        public void onResponse(Call<FavoritWisataResponse> call, Response<FavoritWisataResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FavoritWisataResponse> call, Throwable t) {
                            Toast.makeText(holder.itemView.getContext(), "timeout", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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

    public class PenginapanModelViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtTitle;
        private final TextView txtDesc;
        private final ImageView imgView;
        private final ImageView imgFavs;
        public PenginapanModelViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.penginapanTitle);
            txtDesc = itemView.findViewById(R.id.textvwDesc);
            imgFavs = itemView.findViewById(R.id.buttonFavs);
            imgView = itemView.findViewById(R.id.gambarPenginapanList);
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }
}
