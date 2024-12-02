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
import com.polije.sem3.model.FavoritPenginapanModel;
import com.polije.sem3.response.FavoritPenginapanResponse;
import com.polije.sem3.network.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritPenginapanModelAdapter extends RecyclerView.Adapter<FavoritPenginapanModelAdapter.FavoritPenginapanViewHolder> {
    private ArrayList<FavoritPenginapanModel> dataList;
    private OnClickListener tampil;

    @NonNull
    @Override
    public FavoritPenginapanModelAdapter.FavoritPenginapanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_row_penginapan, parent, false);     // layoutfordisplay
        return new FavoritPenginapanViewHolder(view);
    }

    public FavoritPenginapanModelAdapter(ArrayList<FavoritPenginapanModel> dataList, OnClickListener listener) {
        this.dataList = dataList;
        this.tampil = listener;
    }

    @Override
    public void onBindViewHolder(FavoritPenginapanModelAdapter.FavoritPenginapanViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UsersUtil usersUtil = new UsersUtil(holder.itemView.getContext());
        String idPengguna = usersUtil.getId();

        holder.txtNama.setText(dataList.get(position).getNama());
        holder.txtDesc.setText(dataList.get(position).getDeskripsiPenginapan());
        holder.imgButton.setImageResource(R.drawable.favorite_button_danger);
        Glide.with(holder.itemView.getContext())
                .load(Client.IMG_DATA + getFirstImage(dataList.get(position).getGambar()))
                .into(holder.imgView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampil.onItemClick(position);
            }
        });

        holder.imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgButton.setImageResource(R.drawable.favorite_button_white);
                Client.getInstance().deletefavpenginapan("hapus","penginapan",idPengguna, dataList.get(position).getIdPenginapan()).enqueue(new Callback<FavoritPenginapanResponse>() {
                    @Override
                    public void onResponse(Call<FavoritPenginapanResponse> call, Response<FavoritPenginapanResponse> response) {
                        if (response.body() != null && response.body().getMessage() == "success") {
                            Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FavoritPenginapanResponse> call, Throwable t) {
                        Toast.makeText(holder.itemView.getContext(), "timeout", Toast.LENGTH_SHORT).show();
                    }
                });
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

    public class FavoritPenginapanViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNama, txtDesc;
        private ImageView imgButton, imgView;
        public FavoritPenginapanViewHolder(View itemView) {
            super(itemView);
            txtNama = (TextView) itemView.findViewById(R.id.penginapanTitle);
            txtDesc = (TextView) itemView.findViewById(R.id.textvwDesc);
            imgButton = (ImageView) itemView.findViewById(R.id.buttonFavs);
            imgView = (ImageView) itemView.findViewById(R.id.gambarPenginapanList);
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }

}
