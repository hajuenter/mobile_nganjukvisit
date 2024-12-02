package com.polije.sem3.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.polije.sem3.R;
import com.polije.sem3.model.EventModel;
import com.polije.sem3.response.DetailEventResponse;
import com.polije.sem3.network.Client;
import com.polije.sem3.util.DepthPageTransformer;
import com.polije.sem3.adapter.SliderAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailEvent extends AppCompatActivity {
    public static String ID_EVENT = "id";
    private String idSelected;

    private EventModel eventArrayList;
    private TextView namaEvent, desc, cp, jadwal, lokasi;
    private Button btnBack;
    private ImageView imgViewEvent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        idSelected = getIntent().getStringExtra(ID_EVENT);

        namaEvent = findViewById(R.id.namaEvent);
        desc = findViewById(R.id.deskripsiEvent);
        jadwal = findViewById(R.id.jadwalEvent);
        lokasi = findViewById(R.id.lokasiEvent);
        btnBack = findViewById(R.id.backButtonDetail);
        imgViewEvent = findViewById(R.id.imageView);

        Client.getInstance().detailevent("detail_event",idSelected).enqueue(new Callback<DetailEventResponse>() {
            @Override
            public void onResponse(Call<DetailEventResponse> call, Response<DetailEventResponse> response) {
                if(response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {

                    eventArrayList = response.body().getData();

                    namaEvent.setText(eventArrayList.getNama());
                    desc.setText(eventArrayList.getDeskripsi());
                    jadwal.setText(eventArrayList.getTanggaldanwaktu());

/*
                    Glide.with(DetailEvent.this).load(Client.IMG_DATA + eventArrayList.getGambar()).into(imgViewEvent);
*/
                    String gambarString = eventArrayList.getGambar();
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
                    SliderAdapter adapter = new SliderAdapter(DetailEvent.this, imageUrls);
                    slider.setAdapter(adapter);
                    slider.setPageTransformer(new DepthPageTransformer());

                    lokasi.setText(eventArrayList.getLokasi());

                } else {
                    Toast.makeText(DetailEvent.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DetailEventResponse> call, Throwable t) {
                Toast.makeText(DetailEvent.this, "Timeout", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public static String convertToDate(@NonNull String date){
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date inputDate = inputDateFormat.parse(date);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id"));
            assert inputDate != null;
            return outputDateFormat.format(inputDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return inputDateFormat.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}