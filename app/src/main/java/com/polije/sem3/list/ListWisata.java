package com.polije.sem3.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.polije.sem3.Dashboard;
import com.polije.sem3.detail.DetailInformasi;
import com.polije.sem3.R;
import com.polije.sem3.searching.SearchingWisata;
import com.polije.sem3.model.RekomendasiWisataAdapter;
import com.polije.sem3.model.WisataModel;
import com.polije.sem3.model.WisataModelAdapter;
import com.polije.sem3.network.Config;
import com.polije.sem3.response.WisataResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListWisata extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WisataModelAdapter adapter;
    private RekomendasiWisataAdapter adapter2;
    private ArrayList<WisataModel> WisataArrayList, WisataArrayList2;

    Resources resources;
    private String textDescOrigin, textViewDesc;
    private ImageView imageView, imgUser, btnNotify;
    private TextView txtSearch, txtNama;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_wisata);

        UsersUtil usersUtil = new UsersUtil(this);
        String profilePhoto = usersUtil.getUserPhoto();
        String namaPengguna = usersUtil.getFullName();

        txtNama = (TextView) findViewById(R.id.userfullname);
        imgUser = (ImageView) findViewById(R.id.userImg);
        btnNotify = (ImageView) findViewById(R.id.btnNotif);

        Glide.with(this).load(Config.API_IMAGE + profilePhoto).into(imgUser);
        txtNama.setText("Halo! " + namaPengguna);


        // click to profile
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileFragment();
            }
        });

        // click to notifikasi
        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotifyFragment();
            }
        });

        // searching
        txtSearch = findViewById(R.id.searchbox);

        txtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txtSearch.setEnabled(false);
                    Intent i = new Intent(ListWisata.this, SearchingWisata.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                } else {
                    txtSearch.setEnabled(true);
                }
            }
        });

//        addData();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewListWisata);

        Client.getInstance().wisata().enqueue(new Callback<WisataResponse>() {
            @Override
            public void onResponse(Call<WisataResponse> call, Response<WisataResponse> response) {

                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")){
                    WisataArrayList = response.body().getData();
                    adapter = new WisataModelAdapter(WisataArrayList, new WisataModelAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            startActivity(
                                    new Intent(ListWisata.this, DetailInformasi.class)
                                            .putExtra(DetailInformasi.ID_WISATA, WisataArrayList.get(position).getIdwisata())
                            );
                        }
                    });

                    recyclerView.setAdapter(adapter);
                }else {
                    Toast.makeText(ListWisata.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<WisataResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ListWisata.this, "ERROR -> " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView recyclerView1 = findViewById(R.id.recyclerviewListWisataPopuler);

        Client.getInstance().wisatapopuler().enqueue(new Callback<WisataResponse>() {

            @Override
            public void onResponse(Call<WisataResponse> call, Response<WisataResponse> response) {
                if(response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    WisataArrayList2 = response.body().getData();
                    adapter2 = new RekomendasiWisataAdapter(WisataArrayList2, new RekomendasiWisataAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            startActivity(
                                    new Intent(ListWisata.this, DetailInformasi.class)
                                            .putExtra(DetailInformasi.ID_WISATA, WisataArrayList2.get(position).getIdwisata())
                            );
                        }
                    });
                    recyclerView1.setAdapter(adapter2);
                } else {
                    Toast.makeText(ListWisata.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WisataResponse> call, Throwable t) {
                Toast.makeText(ListWisata.this, "Request Timeout", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void gantiDetail() {
        Intent i = new Intent(ListWisata.this, DetailInformasi.class);
        startActivity(i);

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