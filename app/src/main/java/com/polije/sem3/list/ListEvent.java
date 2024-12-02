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
import com.polije.sem3.detail.DetailEvent;
import com.polije.sem3.R;
import com.polije.sem3.model.EventModel;
import com.polije.sem3.adapter.EventModelAdapter;
import com.polije.sem3.network.Config;
import com.polije.sem3.response.EventResponse;
import com.polije.sem3.network.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListEvent extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventModelAdapter adapter;
    private ArrayList<EventModel> EventArrayList;

    private ImageView imgUser, btnNotify;
    private TextView txtNama;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_event);

        // header profiles photo profiles and name of user
        UsersUtil util = new UsersUtil(this);
        String namaUser = util.getUsername();
        String profilePhoto = util.getUserPhoto();
        imgUser = (ImageView) findViewById(R.id.userImg);
        txtNama = (TextView) findViewById(R.id.userfullname);
        txtNama.setText("Halo! " + namaUser);
        Glide.with(this).load(Config.API_IMAGE + profilePhoto).into(imgUser);

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

        recyclerView = (RecyclerView) findViewById(R.id.recyclerviewListEvent);

        Client.getInstance().event().enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("true")) {
                    EventArrayList = response.body().getData();
                    adapter = new EventModelAdapter(EventArrayList, new EventModelAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            startActivity(
                                    new Intent(ListEvent.this, DetailEvent.class)
                                            .putExtra(DetailEvent.ID_EVENT, EventArrayList.get(position).getIdEvent())
                            );
                        }
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ListEvent.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(ListEvent.this, "Request Timeout", Toast.LENGTH_SHORT).show();
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