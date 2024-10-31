package com.polije.sem3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.polije.sem3.detail.DetailInformasi;
import com.polije.sem3.detail.DetailKuliner;
import com.polije.sem3.detail.DetailPenginapan;
import com.polije.sem3.model.FavoritKulinerModelAdapter;
import com.polije.sem3.model.FavoritPenginapanModel;
import com.polije.sem3.model.FavoritPenginapanModelAdapter;
import com.polije.sem3.model.FavoritKulinerModel;
import com.polije.sem3.model.FavoritWisataModelAdapter;
import com.polije.sem3.model.FavoritWisataModel;
import com.polije.sem3.response.FavoritKulinerResponse;
import com.polije.sem3.response.FavoritPenginapanResponse;
import com.polije.sem3.response.FavoritWisataResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Favs#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Favs extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Favs() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Favs.
     */
    // TODO: Rename and change types and number of parameters
    public static Favs newInstance(String param1, String param2) {
        Favs fragment = new Favs();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private RecyclerView recyclerView, recyclerView2, recyclerView3;
    private FavoritWisataModelAdapter adapter;
    private FavoritPenginapanModelAdapter adapter2;
    private FavoritKulinerModelAdapter adapter3;
    private ArrayList<FavoritWisataModel> WisataArrayList;
    private ArrayList<FavoritPenginapanModel> PenginapanArrayList;
    private ArrayList<FavoritKulinerModel> KulinerArrayList;
    private TextView favsWisataJudul, favsPenginapanJudul, favsKulinerJudul;
    private TextView emptyTextView;
    private LinearLayout contentLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favs, container, false);

        UsersUtil userUtil = new UsersUtil(requireContext());
        String idUser = userUtil.getId();

        // inisialisasi layout
        contentLayout = rootView.findViewById(R.id.content);

        // inisiasialisasi emptytextview untuk data kosong
        emptyTextView = new TextView(requireContext());
        emptyTextView.setText("Belum Ada yang Disukai");
        emptyTextView.setTextColor(getResources().getColor(R.color.black));
        emptyTextView.setTextSize(30);
        emptyTextView.setGravity(Gravity.CENTER);
        emptyTextView.setPadding(0, 400, 0, 100);
        contentLayout.addView(emptyTextView);

        // inisialisasi judul setiap favorit
        favsWisataJudul = rootView.findViewById(R.id.favsWisataJudul);
        favsPenginapanJudul = rootView.findViewById(R.id.favsPenginapanJudul);
        favsKulinerJudul = rootView.findViewById(R.id.favsKulinerJudul);

        // menampilkan recview wisata favorit
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerviewListWisataFavorit);

        Client.getInstance().favwisata(idUser).enqueue(new Callback<FavoritWisataResponse>() {
            @Override
            public void onResponse(Call<FavoritWisataResponse> call, Response<FavoritWisataResponse> response) {
                WisataArrayList = response.body().getData();
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    if (!WisataArrayList.isEmpty()) {
                        adapter = new FavoritWisataModelAdapter(WisataArrayList, new FavoritWisataModelAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                startActivity(
                                        new Intent(requireContext(), DetailInformasi.class)
                                                .putExtra(DetailInformasi.ID_WISATA, WisataArrayList.get(position).getIdWisata())
                                );
                            }
                        });
                        recyclerView.setAdapter(adapter);
                        emptyTextView.setVisibility(rootView.GONE);
                    } else {
                        favsWisataJudul.setVisibility(rootView.GONE);
                    }
                } else {
                    Toast.makeText(requireContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FavoritWisataResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });


        // menampilkan recview penginapan favorit
        recyclerView2 = (RecyclerView) rootView.findViewById(R.id.recyclerviewListPenginapanFavorit);

        Client.getInstance().favpenginapan(idUser).enqueue(new Callback<FavoritPenginapanResponse>() {
            @Override
            public void onResponse(Call<FavoritPenginapanResponse> call, Response<FavoritPenginapanResponse> response) {
                PenginapanArrayList = response.body().getData();
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    if (!PenginapanArrayList.isEmpty()) {
                        adapter2 = new FavoritPenginapanModelAdapter(PenginapanArrayList, new FavoritPenginapanModelAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                startActivity(
                                        new Intent(requireContext(), DetailPenginapan.class)
                                                .putExtra(DetailPenginapan.ID_PENGINAPAN, PenginapanArrayList.get(position).getIdPenginapan())
                                );
                            }
                        });
                        recyclerView2.setAdapter(adapter2);
                        emptyTextView.setVisibility(rootView.GONE);
                    } else {
                        favsPenginapanJudul.setVisibility(rootView.GONE);
                    }
                } else {
                    Toast.makeText(requireContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FavoritPenginapanResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

        // menampilkan recview kuliner favorit
        recyclerView3 = (RecyclerView) rootView.findViewById(R.id.recyclerviewListKulinerFavorit);

        Client.getInstance().favkuliner(idUser).enqueue(new Callback<FavoritKulinerResponse>(){

            @Override
            public void onResponse(Call<FavoritKulinerResponse> call, Response<FavoritKulinerResponse> response) {
                KulinerArrayList = response.body().getData();
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    if (!KulinerArrayList.isEmpty()) {
                        adapter3 = new FavoritKulinerModelAdapter(KulinerArrayList, new FavoritKulinerModelAdapter.OnClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                startActivity(

                                    new Intent(requireContext(), DetailKuliner.class)
                                            .putExtra(DetailKuliner.ID_KULINER, KulinerArrayList.get(position).getIdKuliner())
                                );
                            }
                        });
                        recyclerView3.setAdapter(adapter3);
                        emptyTextView.setVisibility(rootView.GONE);
                    } else {
                        favsKulinerJudul.setVisibility(rootView.GONE);
                    }

//                    updateUI();
                } else {
                    Toast.makeText(requireContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FavoritKulinerResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;
    }
}