package com.polije.sem3.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.polije.sem3.Dashboard;
import com.polije.sem3.detail.DetailPenginapan;
import com.polije.sem3.R;
import com.polije.sem3.searching.SearchingPenginapan;
import com.polije.sem3.model.PenginapanModel;
import com.polije.sem3.model.PenginapanModelAdapter;
import com.polije.sem3.network.Config;
import com.polije.sem3.response.PenginapanResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListPenginapan extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PenginapanModelAdapter adapter;
    private ArrayList<PenginapanModel> PenginapanArrayList;
    private TextView txtSearch, txtNama;
    private ImageView imgUser, btnNotify;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_penginapan);

        UsersUtil usersUtil = new UsersUtil(this);
        String profilePhoto = usersUtil.getUserPhoto();
        String namaPengguna = usersUtil.getUsername();

        txtNama = (TextView) findViewById(R.id.userfullname);
        imgUser = findViewById(R.id.userImg);

        Glide.with(this).load(Config.API_IMAGE + profilePhoto).into(imgUser);
        txtNama.setText("Halo! " + namaPengguna);

        // searching
        txtSearch = findViewById(R.id.searchbox);

        // link to notify
        btnNotify = (ImageView) findViewById(R.id.btnNotif);
        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotifyFragment();
            }
        });

        // link to profiles
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileFragment();
            }
        });

        txtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txtSearch.setEnabled(false);
                    Intent i = new Intent(ListPenginapan.this, SearchingPenginapan.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                } else {
                    txtSearch.setEnabled(true);
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewListPenginapan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Client.getInstance().penginapan().enqueue(new Callback<PenginapanResponse>() {
            @Override
            public void onResponse(Call<PenginapanResponse> call, Response<PenginapanResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("true")) {
                    PenginapanArrayList = response.body().getData();

                    adapter = new PenginapanModelAdapter(response.body().getData(), new PenginapanModelAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            startActivity(
                                    new Intent(ListPenginapan.this, DetailPenginapan.class)
                                            .putExtra(DetailPenginapan.ID_PENGINAPAN, PenginapanArrayList.get(position).getIdPenginapan())
                            );
                        }
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ListPenginapan.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<PenginapanResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ListPenginapan.this, "ERROR -> " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showProfileFragment() {
        Intent i = new Intent(this, Dashboard.class);
        i.putExtra("fragmentToLoad", "Profiles");
        startActivity(i);
    }

    public void showNotifyFragment() {
        Intent i = new Intent(this, Dashboard.class);
        i.putExtra("fragmentToLoad", "Notify");
        startActivity(i);
    }
}
