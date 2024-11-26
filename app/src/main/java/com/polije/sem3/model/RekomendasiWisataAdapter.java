package com.polije.sem3.model;

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
import com.polije.sem3.response.FavoritWisataResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RekomendasiWisataAdapter extends RecyclerView.Adapter<RekomendasiWisataAdapter.RekomendasiWisataViewHolder> {
    private ArrayList<WisataModel> dataList;

    private OnClickListener tampil;

    @NonNull
    @Override
    public RekomendasiWisataAdapter.RekomendasiWisataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_row_wisata_slide, parent, false);     // layoutfordisplay
        return new RekomendasiWisataViewHolder(view);
    }

    public RekomendasiWisataAdapter(ArrayList<WisataModel> dataList, OnClickListener listener) {
        this.dataList = dataList;
        this.tampil = listener;
    }

    @Override
    public void onBindViewHolder(RekomendasiWisataAdapter.RekomendasiWisataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UsersUtil usersUtil = new UsersUtil(holder.itemView.getContext());
        String idPengguna = usersUtil.getId();

        holder.txtTitle.setText(dataList.get(position).getNama());
        holder.txtLokasi.setText(dataList.get(position).getAlamat());

        Glide.with(holder.itemView.getContext())
                .load(Client.IMG_DATA + getFirstImage(dataList.get(position).getGambar()))
                .into(holder.imgWisata);

        Client.getInstance().cekfavwisata("cek","wisata",idPengguna, dataList.get(position).getIdwisata()).enqueue(new Callback<FavoritWisataResponse>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onResponse(Call<FavoritWisataResponse> call, Response<FavoritWisataResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("alreadyex")) {
                    holder.imgFavs.setImageResource(R.drawable.favorite_button_danger);
                } else {
                    holder.imgFavs.setImageResource(R.drawable.favorite_button_white);
                }
            }
            @Override
            public void onFailure(Call<FavoritWisataResponse> call, Throwable t) {
                Toast.makeText(holder.itemView.getContext(), "timeout", Toast.LENGTH_SHORT).show();
            }
        });


        holder.imgFavs.setTag("not_favorite");

        holder.imgFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentTag = (String) holder.imgFavs.getTag(); // Ambil status saat ini

                if (currentTag.equals("favorite")) {
                    // Hapus favorit jika status saat ini 'favorite'
                    holder.imgFavs.setImageResource(R.drawable.favorite_button_white);
                    holder.imgFavs.setTag("not_favorite"); // Update tag

                    Client.getInstance().deletefavwisata("hapus", "wisata", idPengguna, dataList.get(position).getIdwisata()).enqueue(new Callback<FavoritWisataResponse>() {
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
                    holder.imgFavs.setTag("favorite"); // Update tag

                    Client.getInstance().tambahfavwisata("tambah", "wisata", idPengguna, dataList.get(position).getIdwisata()).enqueue(new Callback<FavoritWisataResponse>() {
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



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    tampil.onItemClick(position);
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

    public class RekomendasiWisataViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtLokasi;
        private ImageView imgWisata, imgFavs;
        public RekomendasiWisataViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.titleWisata);
            txtLokasi = (TextView) itemView.findViewById(R.id.alamatWisata);
            imgFavs = (ImageView) itemView.findViewById(R.id.buttonFavs);
            imgWisata = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }
}
