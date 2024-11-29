package com.polije.sem3.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

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
import com.polije.sem3.util.DepthPageTransformer;
import com.polije.sem3.util.SliderAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailKuliner extends AppCompatActivity {
    public static String ID_KULINER = "id";
    private String idSelected;
    private TextView namaKuliner, deskripsiKuliner;
    private KulinerModel kulinerModel;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kuliner);

        idSelected = getIntent().getStringExtra(ID_KULINER);

        namaKuliner = findViewById(R.id.namaKuliner);
        deskripsiKuliner = findViewById(R.id.deskripsiKuliner);
        /*ImageView gambarCover = findViewById(R.id.KulinerImage);*/
        btnBack = findViewById(R.id.backButtonDetail);

//        namaKuliner.setText(idSelected);

        Client.getInstance().detailkuliner("detail_kuliner",idSelected).enqueue(new Callback<DetailKulinerResponse>() {
            @Override
            public void onResponse(Call<DetailKulinerResponse> call, Response<DetailKulinerResponse> response) {
                if(response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    kulinerModel = response.body().getData();
                    String getNamaKuliner =kulinerModel.getNama();
                    String getDeskripsiKuliner = kulinerModel.getDeskripsi();

                    namaKuliner.setText(getNamaKuliner);
                    deskripsiKuliner.setText(getDeskripsiKuliner);
/*
                    Glide.with(DetailKuliner.this).load(Client.IMG_DATA + kulinerModel.getGambar()).into(gambarCover);
*/
                    String gambarString = kulinerModel.getGambar();
                    List<String> imageUrls = new ArrayList<>();
                    if (gambarString.contains(",")) {
                        String[] images = gambarString.split(",");
                        for (String image : images) {
                            imageUrls.add(Client.IMG_DATA + image.trim()); // Tambahkan base URL + gambar
                        }
                    } else {
                        // Jika hanya ada satu gambar
                        imageUrls.add(Client.IMG_DATA + gambarString.trim());
                    }
                    ViewPager2 slider = findViewById(R.id.slider);
                    SliderAdapter adapter = new SliderAdapter(DetailKuliner.this, imageUrls);
                    slider.setAdapter(adapter);
                    slider.setPageTransformer(new DepthPageTransformer());

                }
            }

            @Override
            public void onFailure(Call<DetailKulinerResponse> call, Throwable t) {

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