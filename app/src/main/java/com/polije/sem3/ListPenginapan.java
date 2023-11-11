package com.polije.sem3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.polije.sem3.model.EventModel;
import com.polije.sem3.model.EventModelAdapter;
import com.polije.sem3.model.PenginapanModel;
import com.polije.sem3.model.PenginapanModelAdapter;
import com.polije.sem3.response.PenginapanResponse;
import com.polije.sem3.retrofit.Client;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListPenginapan extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PenginapanModelAdapter adapter;
    private ArrayList<PenginapanModel> PenginapanArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_penginapan);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewListPenginapan);

        Client.getInstance().penginapan().enqueue(new Callback<PenginapanResponse>() {
            @Override
            public void onResponse(Call<PenginapanResponse> call, Response<PenginapanResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    adapter = new PenginapanModelAdapter(response.body().getData());
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
}