package com.polije.sem3.model;

import android.annotation.SuppressLint;
import android.util.Log;
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
import com.polije.sem3.response.WisataResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritWisataModelAdapter extends RecyclerView.Adapter<FavoritWisataModelAdapter.FavoritWisataViewHolder> {
    private ArrayList<FavoritWisataModel> dataList;

    private OnClickListener tampil;

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

        Glide.with(holder.itemView.getContext()).load(Client.IMG_DATA + dataList.get(position).getGambar()).into(holder.imgView);

        holder.imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgButton.setImageResource(R.drawable.favorite_button_white);
                Client.getInstance().deletefavwisata("hapus","wisata",idPengguna, dataList.get(position).getIdWisata()).enqueue(new Callback<FavoritWisataResponse>() {
                    @Override
                    public void onResponse(Call<FavoritWisataResponse> call, Response<FavoritWisataResponse> response) {
                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                            Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(holder.itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FavoritWisataResponse> call, Throwable t) {
                        Toast.makeText(holder.itemView.getContext(), "timeout", Toast.LENGTH_SHORT).show();
                    }
                });
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

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    private String fitmeTxt (String textDescOrigin) {

        int maxLength = 100; // Panjang maksimal yang diinginkan

        if (textDescOrigin.length() > maxLength) {
            String limitedText = textDescOrigin.substring(0, maxLength);
            String finalText = limitedText + " ...";
            return finalText;
        } else {
            // Teks tidak perlu dibatasi
            String finalText = textDescOrigin;
            return finalText;
        }
    }

    public class FavoritWisataViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNama, txtDesc;
        private ImageView imgButton, imgView;

        public FavoritWisataViewHolder(View itemView) {
            super(itemView);
            txtNama = (TextView) itemView.findViewById(R.id.wisataTitle);
            txtDesc = (TextView) itemView.findViewById(R.id.textvwDescw);
            imgButton = itemView.findViewById(R.id.favsbutton);
            imgView = (ImageView) itemView.findViewById(R.id.imageWisata);
        }
    }

    public interface OnClickListener {
        void onItemClick(int position);
    }

}
