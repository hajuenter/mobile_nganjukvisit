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
import com.polije.sem3.Home;
import com.polije.sem3.R;
import com.polije.sem3.response.FavoritKulinerResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RekomendasiKulinerAdapter extends RecyclerView.Adapter<RekomendasiKulinerAdapter.RekomendasiKulinerViewHolder> {
    private ArrayList<KulinerModel> dataList;
    private OnClickListener tampil;

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

        holder.titleTxt.setText(dataList.get(position).getNama());
        holder.lokasiTxt.setText(dataList.get(position).getLokasi());

        Glide.with(holder.itemView.getContext()).load(Client.IMG_DATA + dataList.get(position).getGambar()).into(holder.imgKuliner);

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
                }
            }

            @Override
            public void onFailure(Call<FavoritKulinerResponse> call, Throwable t) {
                Toast.makeText(holder.itemView.getContext(), "timeout", Toast.LENGTH_SHORT).show();
            }
        });

        holder.imgFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgFavs.setImageResource(R.drawable.favorite_button_danger);
                Client.getInstance().tambahfavkuliner("tambah","kuliner",idPengguna, dataList.get(position).getIdKuliner()).enqueue(new Callback<FavoritKulinerResponse>() {
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
        });
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class RekomendasiKulinerViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTxt, lokasiTxt;
        private ImageView imgKuliner, imgFavs;

        public RekomendasiKulinerViewHolder(View itemView) {
            super(itemView);
            titleTxt = (TextView) itemView.findViewById(R.id.namaKuliner);
            lokasiTxt = (TextView) itemView.findViewById(R.id.lokasiKuliner);
            imgKuliner = (ImageView) itemView.findViewById(R.id.imageViewKuliner);
            imgFavs = (ImageView) itemView.findViewById(R.id.buttonFavs);
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }
}
