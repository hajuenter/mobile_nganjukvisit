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
import com.polije.sem3.model.FavoritWisataModel;
import com.polije.sem3.response.FavoritKulinerResponse;
import com.polije.sem3.network.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritWisataModelAdapter extends RecyclerView.Adapter<FavoritWisataModelAdapter.FavoritWisataViewHolder> {
    private final ArrayList<FavoritWisataModel> dataList;

    private final OnClickListener tampil;

    @NonNull
    @Override
    public FavoritWisataModelAdapter.FavoritWisataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_row_wisata, parent, false);     // layoutfordisplay
        return new FavoritWisataViewHolder(view);
    }

    public FavoritWisataModelAdapter(ArrayList<FavoritWisataModel> dataList, OnClickListener listener) {
        this.dataList = dataList;
        this.tampil = listener;
    }

    @Override
    public void onBindViewHolder(FavoritWisataModelAdapter.FavoritWisataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UsersUtil usersUtil = new UsersUtil(holder.itemView.getContext());
        String idPengguna = usersUtil.getId();

        holder.txtNama.setText(dataList.get(position).getNamaWisata());
        holder.txtDesc.setText(fitmeTxt(dataList.get(position).getDeskripsi()));
        holder.imgButton.setImageResource(R.drawable.favorite_button_danger);
        Glide.with(holder.itemView.getContext())
                .load(Client.IMG_DATA + getFirstImage(dataList.get(position).getGambar()))
                .into(holder.imgView);

        holder.imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Periksa tag untuk menentukan drawable yang sedang ditampilkan
                if ("favorited".equals(holder.imgButton.getTag())) {
                    // Jika sudah favorit, ubah ke non-favorit
                    holder.imgButton.setImageResource(R.drawable.favorite_button_white);
                    holder.imgButton.setTag("not_favorited");

                    // Tambahkan logika untuk menghapus dari favorit
                    Client.getInstance().deletefavkuliner("hapus", "wisata", idPengguna, dataList.get(position).getIdWisata()).enqueue(new Callback<FavoritKulinerResponse>() {
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
                    holder.imgButton.setImageResource(R.drawable.favorite_button_danger);
                    holder.imgButton.setTag("favorited");

                    // Tambahkan logika untuk menambahkan ke favorit
                    Client.getInstance().tambahfavkuliner("tambah", "wisata", idPengguna, dataList.get(position).getIdWisata()).enqueue(new Callback<FavoritKulinerResponse>() {
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



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
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

    private String fitmeTxt (String textDescOrigin) {

        int maxLength = 100;

        if (textDescOrigin.length() > maxLength) {
            String limitedText = textDescOrigin.substring(0, maxLength);
            String finalText = limitedText + " ...";
            return finalText;
        } else {
            String finalText = textDescOrigin;
            return finalText;
        }
    }

    public class FavoritWisataViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtNama;
        private final TextView txtDesc;
        private final ImageView imgButton;
        private final ImageView imgView;

        public FavoritWisataViewHolder(View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.wisataTitle);
            txtDesc = itemView.findViewById(R.id.textvwDescw);
            imgButton = itemView.findViewById(R.id.favsbutton);
            imgView = itemView.findViewById(R.id.imageWisata);
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }

}
