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
import com.polije.sem3.detail.DetailKuliner;
import com.polije.sem3.detail.DetailPenginapan;
import com.polije.sem3.R;
import com.polije.sem3.model.KulinerModel;
import com.polije.sem3.model.KulinerModelAdapter;
import com.polije.sem3.model.PenginapanModel;
import com.polije.sem3.model.PenginapanModelAdapter;
import com.polije.sem3.model.WisataModel;
import com.polije.sem3.model.WisataModelAdapter;
import com.polije.sem3.response.KulinerResponse;
import com.polije.sem3.response.PenginapanResponse;
import com.polije.sem3.response.WisataResponse;
import com.polije.sem3.retrofit.Client;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchingHomepage extends AppCompatActivity {

    private EditText keySearch;
    private String valueKey;
    private ArrayList<WisataModel> WisataArrayList;
    private ArrayList<PenginapanModel> PenginapanArrayList;
    private ArrayList<KulinerModel> KulinerArrayList;
    private WisataModelAdapter adapter;
    private PenginapanModelAdapter adapter2;
    private KulinerModelAdapter adapter3;
    private RecyclerView recyclerView, recyclerView2, recyclerView3;
    private TextView emptyTextView, judulWisata, judulPenginapan, judulKuliner;
    private LinearLayout contentLayout;
    private boolean isWisataAvailable;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_homepage);

        // initiate content layout
        contentLayout = findViewById(R.id.contentPanel);

        // initiate judul setiap recview
//        isWisataAvailable = false;
        judulWisata = findViewById(R.id.favsWisataJudul);
        judulPenginapan = findViewById(R.id.favsPenginapanJudul);
        judulKuliner = findViewById(R.id.favsKulinerJudul);

        judulWisata.setVisibility(View.GONE);
        judulPenginapan.setVisibility(View.GONE);
        judulKuliner.setVisibility(View.GONE);

        keySearch = findViewById(R.id.keySearch);
        keySearch.requestFocus();

        valueKey = "";
        recyclerView = findViewById(R.id.recyclerviewListWisata);
        recyclerView2 = findViewById(R.id.recyclerviewListPenginapan);
        recyclerView3 = findViewById(R.id.recyclerviewListKuliner);

        emptyTextView = new TextView(SearchingHomepage.this);
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

                    Client.getInstance().cariwisata("search_all","wisata",valueKey).enqueue(new Callback<WisataResponse>() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onResponse(Call<WisataResponse> call, Response<WisataResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                WisataArrayList = response.body().getData();
                                if (!WisataArrayList.isEmpty()) {
                                    adapter = new WisataModelAdapter(WisataArrayList, new WisataModelAdapter.OnClickListener() {
                                        @Override
                                        public void onItemClick(int position) {
                                            startActivity(
                                                    new Intent(SearchingHomepage.this, DetailInformasi.class)
                                                            .putExtra(DetailInformasi.ID_WISATA, WisataArrayList.get(position).getIdwisata())
                                            );
                                        }
                                    });
                                    emptyTextView.setVisibility(View.GONE);
                                    judulWisata.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    recyclerView.setAdapter(adapter);
                                    isWisataAvailable = true;
                                } else {
                                    judulWisata.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<WisataResponse> call, Throwable t) {
                            Toast.makeText(SearchingHomepage.this, "Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Client.getInstance().caripenginapan("search_all","penginapan",valueKey).enqueue(new Callback<PenginapanResponse>() {
                        @Override
                        public void onResponse(Call<PenginapanResponse> call, Response<PenginapanResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                PenginapanArrayList = response.body().getData();
                                if (!PenginapanArrayList.isEmpty()) {
                                    adapter2 = new PenginapanModelAdapter(PenginapanArrayList, new PenginapanModelAdapter.OnClickListener() {
                                        @Override
                                        public void onItemClick(int position) {
                                            startActivity(
                                                    new Intent(SearchingHomepage.this, DetailPenginapan.class)
                                                            .putExtra(DetailPenginapan.ID_PENGINAPAN, PenginapanArrayList.get(position).getIdPenginapan())
                                            );
                                        }
                                    });
                                    emptyTextView.setVisibility(View.GONE);
                                    judulPenginapan.setVisibility(View.VISIBLE);
                                    recyclerView2.setVisibility(View.VISIBLE);
                                    recyclerView2.setAdapter(adapter2);
                                } else {
                                    judulPenginapan.setVisibility(View.GONE);
                                    recyclerView2.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PenginapanResponse> call, Throwable t) {
                            Toast.makeText(SearchingHomepage.this, "Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });

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
                                                    new Intent(SearchingHomepage.this, DetailKuliner.class)
                                                            .putExtra(DetailKuliner.ID_KULINER, KulinerArrayList.get(position).getIdKuliner())
                                            );
                                        }
                                    });
                                    emptyTextView.setVisibility(View.GONE);
                                    judulKuliner.setVisibility(View.VISIBLE);
                                    recyclerView3.setVisibility(View.VISIBLE);
                                    recyclerView3.setAdapter(adapter3);
                                } else {
                                    judulKuliner.setVisibility(View.GONE);
                                    recyclerView3.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<KulinerResponse> call, Throwable t) {
                            Toast.makeText(SearchingHomepage.this, "Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });

                    return true;
                }

                return false;
            }
        });
    }

}