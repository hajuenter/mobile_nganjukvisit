package com.polije.sem3.list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;  // Import log untuk pencatatan
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

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListWisata extends AppCompatActivity {

    private RecyclerView recyclerView, recyclerView1;
    private WisataModelAdapter adapter;
    private RekomendasiWisataAdapter adapter2;
    private ArrayList<WisataModel> wisataArrayList, wisataArrayList2;

    private ImageView imgUser, btnNotify;
    private TextView txtSearch, txtNama;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_wisata);

        // Inisialisasi dan Setup
        initView();
        setupUserProfile();
        setupSearchListener();

        // Konfigurasi RecyclerViews
        setupRecyclerView(recyclerView, LinearLayoutManager.VERTICAL);
        setupRecyclerView(recyclerView1, LinearLayoutManager.HORIZONTAL);

        // Muat Data Wisata dan Wisata Populer
        loadDataWisata();
        loadDataWisataPopuler();
    }

    private void initView() {
        txtNama = findViewById(R.id.userfullname);
        imgUser = findViewById(R.id.userImg);
        btnNotify = findViewById(R.id.btnNotif);
        txtSearch = findViewById(R.id.searchbox);
        recyclerView = findViewById(R.id.recyclerviewListWisata);
        recyclerView1 = findViewById(R.id.recyclerviewListWisataPopuler);
    }

    private void setupUserProfile() {
        UsersUtil usersUtil = new UsersUtil(this);
        String profilePhoto = usersUtil.getUserPhoto();
        String namaPengguna = usersUtil.getUsername();

        // Set profile image dan nama
        Glide.with(this)
                .load(Config.API_IMAGE + profilePhoto)
                .error(R.drawable.profilespicturetumb) // Gambar default jika gagal memuat
                .into(imgUser);

        txtNama.setText("Halo, " + namaPengguna + "!");

        // Aksi klik untuk gambar profil dan notifikasi
        imgUser.setOnClickListener(v -> openFragment("Profiles"));
        btnNotify.setOnClickListener(v -> openFragment("Notify"));
    }

    private void setupSearchListener() {
        txtSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                txtSearch.setEnabled(false);
                Intent intent = new Intent(ListWisata.this, SearchingWisata.class);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            txtSearch.setEnabled(true); // Aktifkan kembali setelah kembali dari aktivitas pencarian
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView, int orientation) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, orientation, false));
    }

    private void loadDataWisata() {
        Client.getInstance().wisata().enqueue(new Callback<WisataResponse>() {
            @Override
            public void onResponse(Call<WisataResponse> call, Response<WisataResponse> response) {
                Log.d("API Response Wisata", "Response Code: " + response.code());

                if (response.isSuccessful()) {
                    Log.d("API Response Wisata", "Response Body: " + response.body());

                    if (response.body() != null && "true".equalsIgnoreCase(response.body().getStatus())) {
                        wisataArrayList = response.body().getData();

                        if (wisataArrayList != null && !wisataArrayList.isEmpty()) {
                            adapter = new WisataModelAdapter(wisataArrayList, position -> {
                                Intent detailIntent = new Intent(ListWisata.this, DetailInformasi.class);
                                detailIntent.putExtra(DetailInformasi.ID_WISATA, wisataArrayList.get(position).getIdwisata());
                                startActivity(detailIntent);
                            });
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(ListWisata.this, "Data Wisata Kosong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ListWisata.this, "Status tidak berhasil", Toast.LENGTH_SHORT).show();
                        Log.e("API Error Wisata", "Status tidak berhasil: " + response.body());
                    }
                } else {
                    Toast.makeText(ListWisata.this, "Kesalahan Respons: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("API Error Wisata", "Kesalahan Respons: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WisataResponse> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    private void loadDataWisataPopuler() {
        Client.getInstance().wisatapopuler().enqueue(new Callback<WisataResponse>() {
            @Override
            public void onResponse(Call<WisataResponse> call, Response<WisataResponse> response) {
                Log.d("API Response Wisata Populer", "Response Code: " + response.code());

                if (response.isSuccessful()) {
                    Log.d("API Response Wisata Populer", "Response Body: " + response.body());

                    if (response.body() != null && "success".equalsIgnoreCase(response.body().getStatus())) {
                        wisataArrayList2 = response.body().getData();

                        if (wisataArrayList2 != null && !wisataArrayList2.isEmpty()) {
                            adapter2 = new RekomendasiWisataAdapter(wisataArrayList2, position -> {
                                Intent detailIntent = new Intent(ListWisata.this, DetailInformasi.class);
                                detailIntent.putExtra(DetailInformasi.ID_WISATA, wisataArrayList2.get(position).getIdwisata());
                                startActivity(detailIntent);
                            });
                            recyclerView1.setAdapter(adapter2);
                        } else {
                            Toast.makeText(ListWisata.this, "Data Wisata Populer Kosong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ListWisata.this, "Status tidak berhasil", Toast.LENGTH_SHORT).show();
                        Log.e("API Error Wisata Populer", "Status tidak berhasil: " + response.body());
                    }
                } else {
                    Toast.makeText(ListWisata.this, "Kesalahan Respons: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("API Error Wisata Populer", "Kesalahan Respons: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WisataResponse> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    private void handleFailure(Throwable t) {
        if (t instanceof IOException) {
            Toast.makeText(ListWisata.this, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
            Log.e("API Failure", "IOException: " + t.getMessage());
        } else {
            Toast.makeText(ListWisata.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("API Failure", "Error: " + t.getMessage());
        }
        t.printStackTrace();
    }

    private void openFragment(String fragmentName) {
        Intent intent = new Intent(this, Dashboard.class);
        intent.putExtra("fragmentToLoad", fragmentName);
        startActivity(intent);
    }
}
