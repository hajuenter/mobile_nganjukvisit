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
import com.polije.sem3.adapter.KulinerModelAdapter;
import com.polije.sem3.model.PenginapanModel;
import com.polije.sem3.adapter.PenginapanModelAdapter;
import com.polije.sem3.model.WisataModel;
import com.polije.sem3.adapter.WisataModelAdapter;
import com.polije.sem3.response.KulinerResponse;
import com.polije.sem3.response.PenginapanResponse;
import com.polije.sem3.response.WisataResponse;
import com.polije.sem3.network.Client;

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

                    // Kosongkan data dan beri tahu adapter
                    if (WisataArrayList != null) {
                        WisataArrayList.clear();
                        if (adapter != null) adapter.notifyDataSetChanged();
                    }
                    if (PenginapanArrayList != null) {
                        PenginapanArrayList.clear();
                        if (adapter2 != null) adapter2.notifyDataSetChanged();
                    }
                    if (KulinerArrayList != null) {
                        KulinerArrayList.clear();
                        if (adapter3 != null) adapter3.notifyDataSetChanged();
                    }

                    // Sembunyikan semua tampilan hasil sebelumnya
                    recyclerView.setVisibility(View.GONE);
                    recyclerView2.setVisibility(View.GONE);
                    recyclerView3.setVisibility(View.GONE);

                    judulWisata.setVisibility(View.GONE);
                    judulPenginapan.setVisibility(View.GONE);
                    judulKuliner.setVisibility(View.GONE);

                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText("Tidak ada Hasil yang cocok");

                    // Pencarian Wisata
                    Client.getInstance().cariwisata("search_all", "wisata", valueKey).enqueue(new Callback<WisataResponse>() {
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
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    judulWisata.setVisibility(View.VISIBLE);
                                    emptyTextView.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<WisataResponse> call, Throwable t) {
                            Toast.makeText(SearchingHomepage.this, "Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Pencarian Penginapan
                    Client.getInstance().caripenginapan("search_all", "penginapan", valueKey).enqueue(new Callback<PenginapanResponse>() {
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
                                    recyclerView2.setAdapter(adapter2);
                                    recyclerView2.setVisibility(View.VISIBLE);
                                    judulPenginapan.setVisibility(View.VISIBLE);
                                    emptyTextView.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PenginapanResponse> call, Throwable t) {
                            Toast.makeText(SearchingHomepage.this, "Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Pencarian Kuliner
                    Client.getInstance().carikuliner("search_all", "kuliner", valueKey).enqueue(new Callback<KulinerResponse>() {
                        @Override
                        public void onResponse(Call<KulinerResponse> call, Response<KulinerResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                KulinerArrayList = response.body().getData();
                                if (!KulinerArrayList.isEmpty()) {
                                    adapter3 = new KulinerModelAdapter(KulinerArrayList, new KulinerModelAdapter.OnClickListener() {
                                        @Override
                                        public void onItemClick(int position) {
                                            startActivity(
                                                    new Intent(SearchingHomepage.this, DetailKuliner.class)
                                                            .putExtra(DetailKuliner.ID_KULINER, KulinerArrayList.get(position).getIdKuliner())
                                            );
                                        }
                                    });
                                    recyclerView3.setAdapter(adapter3);
                                    recyclerView3.setVisibility(View.VISIBLE);
                                    judulKuliner.setVisibility(View.VISIBLE);
                                    emptyTextView.setVisibility(View.GONE);
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
    private void clearAllData() {
        // Kosongkan semua ArrayList
        if (WisataArrayList != null) WisataArrayList.clear();
        if (PenginapanArrayList != null) PenginapanArrayList.clear();
        if (KulinerArrayList != null) KulinerArrayList.clear();

        // Perbarui tampilan RecyclerView agar kosong
        if (adapter != null) adapter.notifyDataSetChanged();
        if (adapter2 != null) adapter2.notifyDataSetChanged();
        if (adapter3 != null) adapter3.notifyDataSetChanged();

        // Sembunyikan RecyclerView dan judul
        recyclerView.setVisibility(View.GONE);
        recyclerView2.setVisibility(View.GONE);
        recyclerView3.setVisibility(View.GONE);

        judulWisata.setVisibility(View.GONE);
        judulPenginapan.setVisibility(View.GONE);
        judulKuliner.setVisibility(View.GONE);

        // Tampilkan pesan kosong jika diperlukan
        emptyTextView.setVisibility(View.VISIBLE);
        emptyTextView.setText("Tidak ada data yang ditemukan.");
    }


}