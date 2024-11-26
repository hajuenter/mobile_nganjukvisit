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

import com.polije.sem3.detail.DetailPenginapan;
import com.polije.sem3.R;
import com.polije.sem3.model.PenginapanModel;
import com.polije.sem3.model.PenginapanModelAdapter;
import com.polije.sem3.response.PenginapanResponse;
import com.polije.sem3.retrofit.Client;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchingPenginapan extends AppCompatActivity {

    private EditText keySearch;
    private String valueKey;
    private ArrayList<PenginapanModel> PenginapanArrayList;
    private PenginapanModelAdapter adapter2;
    private RecyclerView recyclerView;
    private TextView emptyTextView, judulPenginapan;
    private LinearLayout contentLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching_penginapan);

        contentLayout = findViewById(R.id.contentPanel);

        // initiate judul recview
        judulPenginapan = findViewById(R.id.favsPenginapanJudul);

        judulPenginapan.setVisibility(View.GONE);

        keySearch = findViewById(R.id.keySearch);
        keySearch.requestFocus();

        valueKey = "";
        recyclerView = findViewById(R.id.recyclerviewListPenginapan);

        emptyTextView = new TextView(SearchingPenginapan.this);
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
                    if (PenginapanArrayList != null) {
                        PenginapanArrayList.clear();
                        if (adapter2 != null) adapter2.notifyDataSetChanged();
                    }
                    // Sembunyikan semua tampilan hasil sebelumnya
                    recyclerView.setVisibility(View.GONE);
                    judulPenginapan.setVisibility(View.GONE);


                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText("Tidak ada Hasil yang cocok");

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
                                                    new Intent(SearchingPenginapan.this, DetailPenginapan.class)
                                                            .putExtra(DetailPenginapan.ID_PENGINAPAN, PenginapanArrayList.get(position).getIdPenginapan())
                                            );
                                        }
                                    });
                                    emptyTextView.setVisibility(View.GONE);
                                    judulPenginapan.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    recyclerView.setAdapter(adapter2);
                                } else {
                                    judulPenginapan.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PenginapanResponse> call, Throwable t) {
                            Toast.makeText(SearchingPenginapan.this, "Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });

                    return true;
                }

                return false;
            }
        });
    }
}