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
import com.polije.sem3.response.FavoritPenginapanResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RekomendasiPenginapanAdapter extends RecyclerView.Adapter<RekomendasiPenginapanAdapter.RekomendasiPenginapanViewHolder> {
    private ArrayList<PenginapanModel> dataList;
    private OnClickListener tampil;

    public RekomendasiPenginapanAdapter(ArrayList<PenginapanModel> dataList, OnClickListener listener) {
        this.dataList = dataList;
        this.tampil = listener;
    }

    @NonNull
    @Override
    public RekomendasiPenginapanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_row_penginapan, parent, false);
        return new RekomendasiPenginapanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RekomendasiPenginapanViewHolder holder, int position) {
        PenginapanModel penginapan = dataList.get(position);
        UsersUtil usersUtil = new UsersUtil(holder.itemView.getContext());
        String idPengguna = usersUtil.getId();

        holder.txtNama.setText(penginapan.getJudulPenginapan());
        holder.txtDesc.setText(penginapan.getDeskripsi());
        Glide.with(holder.itemView.getContext())
                .load(Client.IMG_DATA + getFirstImage(penginapan.getGambar())).into(holder.imgView);


        holder.itemView.setOnClickListener(v -> tampil.onItemClick(position));

        // Cek favorit
        checkFavoriteStatus(idPengguna, penginapan.getIdPenginapan(), holder.imgFavs);

        // Set listener untuk tombol favorit
        holder.imgFavs.setOnClickListener(v -> toggleFavorite(idPengguna, penginapan.getIdPenginapan(), holder.imgFavs));
    }

    private void checkFavoriteStatus(String idPengguna, String idPenginapan, ImageView imgFavs) {
        Client.getInstance().cekfavpenginapan("cek","penginapan",idPengguna, idPenginapan).enqueue(new Callback<FavoritPenginapanResponse>() {
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

    private void toggleFavorite(String idPengguna, String idPenginapan, ImageView imgFavs) {
        imgFavs.setImageResource(R.drawable.favorite_button_danger);
        Client.getInstance().tambahfavpenginapan("tambah","penginapan",idPengguna, idPenginapan).enqueue(new Callback<FavoritPenginapanResponse>() {
            @Override
            public void onResponse(Call<FavoritPenginapanResponse> call, Response<FavoritPenginapanResponse> response) {
                if (response.body() != null) {
                    Toast.makeText(imgFavs.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

    public class RekomendasiPenginapanViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNama, txtDesc;
        private ImageView imgFavs, imgView;

        public RekomendasiPenginapanViewHolder(View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.penginapanTitle);
            txtDesc = itemView.findViewById(R.id.textvwDesc);
            imgFavs = itemView.findViewById(R.id.buttonFavs);
            imgView = itemView.findViewById(R.id.gambarPenginapanList);
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }
}
