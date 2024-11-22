package com.polije.sem3.searching;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.polije.sem3.detail.DetailKuliner;
import com.polije.sem3.R;
import com.polije.sem3.model.KulinerModel;
import com.polije.sem3.model.KulinerModelAdapter;
import com.polije.sem3.response.KulinerResponse;
import com.polije.sem3.retrofit.Client;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchingKuliner extends AppCompatActivity {
    private EditText keySearch;
    private String valueKey;
    private ArrayList<KulinerModel> KulinerArrayList;
    private KulinerModelAdapter adapter3;
    private RecyclerView recyclerView;
    private TextView emptyTextView, judulKuliner;
    private LinearLayout contentLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_kuliner);

        // initiate content layout
        contentLayout = findViewById(R.id.contentPanel);

        judulKuliner = findViewById(R.id.favsKulinerJudul);

        judulKuliner.setVisibility(View.GONE);

        keySearch = findViewById(R.id.keySearch);
        keySearch.requestFocus();

        recyclerView = findViewById(R.id.recyclerviewListKuliner);

        emptyTextView = new TextView(SearchingKuliner.this);
        emptyTextView.setText("Tidak Ada Hasil yang cocok");
        emptyTextView.setTextColor(getResources().getColor(R.color.black));
        emptyTextView.setTextSize(30);
        emptyTextView.setGravity(Gravity.CENTER);
        emptyTextView.setPadding(0, 400, 0, 100);
        contentLayout.addView(emptyTextView);
        emptyTextView.setVisibility(View.GONE);

        keySearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int drawableEndIndex = 2;

                if (event.getAction() == MotionEvent.ACTION_UP &&
                        event.getRawX() >= (keySearch.getRight() - keySearch.getCompoundDrawables()[drawableEndIndex].getBounds().width())) {
                    valueKey = keySearch.getText().toString();

                    emptyTextView.setVisibility(View.VISIBLE);

                    Client.getInstance().carikuliner("search_all","kuliner",valueKey).enqueue(new Callback<KulinerResponse>() {
                        @Override
                        public void onResponse(Call<KulinerResponse> call, Response<KulinerResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                KulinerArrayList = response.body().getData();
                                if(!KulinerArrayList.isEmpty()) {
                                    adapter3 = new KulinerModelAdapter(KulinerArrayList, new KulinerModelAdapter.OnClickListener() {
                                        @Override
                                        public void onItemClick(int position) {
                                            startActivity(
                                                    new Intent(SearchingKuliner.this, DetailKuliner.class)
                                                            .putExtra(DetailKuliner.ID_KULINER, KulinerArrayList.get(position).getIdKuliner())
                                            );
                                        }
                                    });
                                    emptyTextView.setVisibility(View.GONE);
                                    judulKuliner.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    recyclerView.setAdapter(adapter3);
                                } else {
                                    judulKuliner.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<KulinerResponse> call, Throwable t) {
                            Toast.makeText(SearchingKuliner.this, "Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });

                    return true;
                }

                return false;
            }
        });
    }
}