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

public class PenginapanModelAdapter extends RecyclerView.Adapter<PenginapanModelAdapter.PenginapanModelViewHolder>{
    private ArrayList<PenginapanModel> dataList;
    private OnClickListener tampil;

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

        Glide.with(holder.itemView.getContext()).load(Client.IMG_DATA + dataList.get(position).getGambar()).into(holder.imgView);
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
                }else {holder.imgFavs.setImageResource(R.drawable.favorite_button_white);}
            }
            @Override
            public void onFailure(Call<FavoritPenginapanResponse> call, Throwable t) {
                Toast.makeText(holder.itemView.getContext(), "timeout", Toast.LENGTH_SHORT).show();
            }
        });

        holder.imgFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgFavs.setImageResource(R.drawable.favorite_button_danger);
                Client.getInstance().tambahfavpenginapan("tambah","penginapan",idPengguna, dataList.get(position).getIdPenginapan()).enqueue(new Callback<FavoritPenginapanResponse>() {
                    @Override
                    public void onResponse(Call<FavoritPenginapanResponse> call, Response<FavoritPenginapanResponse> response) {
                        if (response.body() != null && response.body().getMessage() == "success") {
                            Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else {
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

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class PenginapanModelViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtDesc;
        private ImageView imgView, imgFavs;
        public PenginapanModelViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.penginapanTitle);
            txtDesc = (TextView) itemView.findViewById(R.id.textvwDesc);
            imgFavs = (ImageView) itemView.findViewById(R.id.buttonFavs);
            imgView = (ImageView) itemView.findViewById(R.id.gambarPenginapanList);
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }
}
