package com.polije.sem3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.polije.sem3.model.WisataModel;
import com.polije.sem3.model.WisataModelAdapter;
import com.polije.sem3.response.WisataResponse;
import com.polije.sem3.retrofit.Client;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListWisata extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WisataModelAdapter adapter;
    private ArrayList<WisataModel> WisataArrayList;

    Resources resources;
    private String textDescOrigin, textViewDesc;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_wisata);

        resources = getResources();
        textDescOrigin = resources.getString(R.string.loremipsumgenerator);
        int maxLength = 60; // Panjang maksimal yang diinginkan

        if (textDescOrigin.length() > maxLength) {
            String limitedText = textDescOrigin.substring(0, maxLength);
            String finalText = limitedText + " ...";
            textViewDesc = finalText;
        } else {
            // Teks tidak perlu dibatasi
            String finalText = textDescOrigin;
            textViewDesc = finalText;
        }

//        addData();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewListWisata);

        Client.getInstance().wisata().enqueue(new Callback<WisataResponse>() {
            @Override
            public void onResponse(Call<WisataResponse> call, Response<WisataResponse> response) {
                adapter = new WisataModelAdapter(response.body().getData());

//                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListWisata.this);
//                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<WisataResponse> call, Throwable t) {

            }
        });


        imageView = findViewById(R.id.imageViewSedudo);
        imageView.setOnClickListener(v -> startActivity(new Intent(ListWisata.this, DetailInformasi.class)));

    }

    void addData() {
        WisataArrayList = new ArrayList<>();
        WisataArrayList.add(new WisataModel("Sedudo", textViewDesc));
        WisataArrayList.add(new WisataModel("Taman Nyawiji", textViewDesc));
        WisataArrayList.add(new WisataModel("Taman Nyawiji", textViewDesc));
        WisataArrayList.add(new WisataModel("Taman Nyawiji", textViewDesc));
    }

}