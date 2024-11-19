package com.polije.sem3.detail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.polije.sem3.R;
import com.polije.sem3.model.KulinerModel;
import com.polije.sem3.response.DetailKulinerResponse;
import com.polije.sem3.retrofit.Client;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailKuliner extends AppCompatActivity {
    public static String ID_KULINER = "id";
    private String idSelected;
    private TextView namaKuliner, deskripsiKuliner, lokasiKuliner, linkMaps;
    private KulinerModel kulinerModel;
    private boolean availablelinkmaps;
    private String destination;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kuliner);

        idSelected = getIntent().getStringExtra(ID_KULINER);

        namaKuliner = findViewById(R.id.namaKuliner);
        deskripsiKuliner = findViewById(R.id.deskripsiKuliner);
        lokasiKuliner = findViewById(R.id.alamatKuliner);
        linkMaps = findViewById(R.id.mapsKuliner);
        ImageView gambarCover = findViewById(R.id.KulinerImage);
        btnBack = findViewById(R.id.backButtonDetail);

        // initiate link maps
        availablelinkmaps = true;
        destination = "";

//        namaKuliner.setText(idSelected);

        Client.getInstance().detailkuliner("detail_kuliner",idSelected).enqueue(new Callback<DetailKulinerResponse>() {
            @Override
            public void onResponse(Call<DetailKulinerResponse> call, Response<DetailKulinerResponse> response) {
                if(response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    kulinerModel = response.body().getData();
                    String getNamaKuliner =kulinerModel.getNama();
                    String getDeskripsiKuliner = kulinerModel.getDeskripsi();
                    String lokasi = kulinerModel.getLokasi();
                    String maps = kulinerModel.getLinkmaps();

                    namaKuliner.setText(getNamaKuliner);
                    deskripsiKuliner.setText(getDeskripsiKuliner);
                    lokasiKuliner.setText(lokasi);
                    Glide.with(DetailKuliner.this).load(Client.IMG_DATA + kulinerModel.getGambar()).into(gambarCover);

                    if (maps.isEmpty()) {
                        availablelinkmaps = false;
                        destination = null;
                    } else {
                        availablelinkmaps = true;
                        destination = maps;
                    }

                }
            }

            @Override
            public void onFailure(Call<DetailKulinerResponse> call, Throwable t) {

            }
        });

        linkMaps.setOnClickListener(v -> {

            if (availablelinkmaps){

//                destination = "Air+Terjun+Sedudo"; // Gantilah dengan nama atau alamat tujuan Anda
                String mapUri = "https://www.google.com/maps/search/?api=1&query=" + destination;
//                String mapUri = "https://maps.app.goo.gl/" + destination;

                Uri gmmIntentUri = Uri.parse(mapUri);

                // Buat intent untuk membuka Google Maps
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps"); // Hanya buka dengan aplikasi Google Maps

                // Periksa apakah aplikasi Google Maps terpasang
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe) {
                    // Buka aplikasi Google Maps
                    startActivity(mapIntent);
                } else {
                    // Jika Google Maps tidak terpasang, Anda dapat menampilkan pesan kesalahan
                    Toast.makeText(getApplicationContext(), "Aplikasi Google Maps tidak tersedia.", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(DetailKuliner.this, "Lokasi maps tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}