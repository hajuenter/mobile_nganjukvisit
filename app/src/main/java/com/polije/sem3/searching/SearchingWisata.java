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

import com.polije.sem3.detail.DetailInformasi;
import com.polije.sem3.R;
import com.polije.sem3.model.WisataModel;
import com.polije.sem3.model.WisataModelAdapter;
import com.polije.sem3.response.WisataResponse;
import com.polije.sem3.retrofit.Client;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchingWisata extends AppCompatActivity {

    private EditText keySearch;
    private String valueKey;
    private ArrayList<WisataModel> WisataArrayList;
    private WisataModelAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyTextView, judulWisata;
    private LinearLayout contentLayout;


    @SuppressLint({"MissingInflatedId", "WrongViewCast", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_wisata);

        // initiate content layout
        contentLayout = findViewById(R.id.contentPanel);

        // initiate judul recview
        judulWisata = findViewById(R.id.favsWisataJudul);

        judulWisata.setVisibility(View.GONE);
        keySearch = findViewById(R.id.keySearch);
        keySearch.requestFocus();

        valueKey = "";
        recyclerView = findViewById(R.id.recyclerviewListWisata);

        emptyTextView = new TextView(SearchingWisata.this);
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

                    Client.getInstance().cariwisata(valueKey).enqueue(new Callback<WisataResponse>() {
                        @Override
                        public void onResponse(Call<WisataResponse> call, Response<WisataResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                WisataArrayList = response.body().getData();
                                if (!WisataArrayList.isEmpty()) {
                                    adapter = new WisataModelAdapter(WisataArrayList, new WisataModelAdapter.OnClickListener() {
                                        @Override
                                        public void onItemClick(int position) {
                                            startActivity(
                                                    new Intent(SearchingWisata.this, DetailInformasi.class)
                                                            .putExtra(DetailInformasi.ID_WISATA, WisataArrayList.get(position).getIdwisata())
                                            );
                                        }
                                    });
                                    emptyTextView.setVisibility(View.GONE);
                                    judulWisata.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    judulWisata.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<WisataResponse> call, Throwable t) {
                            Toast.makeText(SearchingWisata.this, "Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });

                    return true;
                }

                return false;
            }
        });

    }
}