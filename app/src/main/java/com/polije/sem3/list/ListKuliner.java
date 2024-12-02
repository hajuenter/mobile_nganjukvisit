package com.polije.sem3.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.polije.sem3.main_menu.Dashboard;
import com.polije.sem3.detail.DetailKuliner;
import com.polije.sem3.R;
import com.polije.sem3.searching.SearchingKuliner;
import com.polije.sem3.model.KulinerModel;
import com.polije.sem3.adapter.KulinerModelAdapter;
import com.polije.sem3.network.Config;
import com.polije.sem3.response.KulinerResponse;
import com.polije.sem3.network.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListKuliner extends AppCompatActivity {
    private RecyclerView recyclerView;
    private KulinerModelAdapter adapter;
    private ArrayList<KulinerModel> KulinerArrayList;
    private TextView txtSearch, txtNama;
    private ImageView imgUser, btnNotify;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kuliner);

        UsersUtil usersUtil = new UsersUtil(this);
        String profilePhoto = usersUtil.getUserPhoto();
        String namaPengguna = usersUtil.getUsername();

        txtNama = (TextView) findViewById(R.id.userfullname);
        imgUser = findViewById(R.id.userImg);

        Glide.with(this).load(Config.API_IMAGE + profilePhoto).into(imgUser);
        txtNama.setText("Halo! " + namaPengguna);

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

        // searching
        txtSearch = findViewById(R.id.searchbox);

        txtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txtSearch.setEnabled(false);
                    Intent i = new Intent(ListKuliner.this, SearchingKuliner.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                } else {
                    txtSearch.setEnabled(true);
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewListKuliner);

        Client.getInstance().kuliner().enqueue(new Callback<KulinerResponse>() {
            @Override
            public void onResponse(Call<KulinerResponse> call, Response<KulinerResponse> response) {
                if(response.body() != null && response.body().getStatus().equalsIgnoreCase("true")) {
                    KulinerArrayList = response.body().getData();
                    adapter = new KulinerModelAdapter(KulinerArrayList, new KulinerModelAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            startActivity(
                                    new Intent(ListKuliner.this, DetailKuliner.class)
                                            .putExtra(DetailKuliner.ID_KULINER, KulinerArrayList.get(position).getIdKuliner())
                            );
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }else {
                    Toast.makeText(ListKuliner.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KulinerResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ListKuliner.this, "ERROR -> " + t.getMessage(), Toast.LENGTH_SHORT).show();
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