package com.polije.sem3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.polije.sem3.detail.DetailInformasi;
import com.polije.sem3.detail.DetailKuliner;
import com.polije.sem3.detail.DetailPenginapan;
import com.polije.sem3.list.ListEvent;
import com.polije.sem3.list.ListKuliner;
import com.polije.sem3.list.ListPenginapan;
import com.polije.sem3.list.ListWisata;
import com.polije.sem3.model.EventModel;
import com.polije.sem3.model.EventModelAdapter;
import com.polije.sem3.model.KulinerModel;
import com.polije.sem3.model.PenginapanModel;
import com.polije.sem3.model.RekomendasiKulinerAdapter;
import com.polije.sem3.model.RekomendasiPenginapanAdapter;
import com.polije.sem3.model.RekomendasiWisataAdapter;
import com.polije.sem3.model.ViewpagerAdapter;
import com.polije.sem3.model.WisataModel;
import com.polije.sem3.network.Config;
import com.polije.sem3.response.EventResponse;
import com.polije.sem3.response.KulinerResponse;
import com.polije.sem3.response.PenginapanResponse;
import com.polije.sem3.response.WisataResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.searching.SearchingHomepage;
import com.polije.sem3.util.UsersUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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

    private RekomendasiPenginapanAdapter adapter;
    private RekomendasiWisataAdapter adapter2;
    private RekomendasiKulinerAdapter adapter3;
    private EventModelAdapter adapter4;
    private ViewpagerAdapter adapter5;
    private ArrayList<WisataModel> wisataArraylist;
    private ArrayList<KulinerModel> KulinerArrayList;
    private ArrayList<EventModel> eventList;
    private ArrayList<PenginapanModel> penginapanList;
    private MaterialCardView catWisata, catKuliner, catPenginapan, catEvent;
    private TextView txtSearch;
    private ImageView imgUser, btnNotification;
    private String token;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        UsersUtil userUtil = new UsersUtil(requireContext());

        TextView namaPengguna;
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                token = task.getResult();

                Log.e("TOKEN", token);

            }
        });


        // searching
        txtSearch = rootView.findViewById(R.id.searchbox);

        txtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txtSearch.setEnabled(false);
                    Intent i = new Intent(requireContext(), SearchingHomepage.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                } else {
                    txtSearch.setEnabled(true);
                }
            }
        });

//        getnamapengguna
        namaPengguna = (TextView) rootView.findViewById(R.id.namaLengkapPengguna);
        namaPengguna.setText("Halo," + userUtil.getUsername()+"!");
        imgUser = (ImageView) rootView.findViewById(R.id.userImg);
        String profilePhoto = userUtil.getUserPhoto();

        Glide.with(requireContext()).load(Config.API_IMAGE + profilePhoto).into(imgUser);

//        Toast.makeText(requireContext(), userUtil.getUserPhoto(), Toast.LENGTH_SHORT).show();

        String idPengguna = userUtil.getId();

        // button notifikasi
        btnNotification = (ImageView) rootView.findViewById(R.id.btnNotif);

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationFragment(rootView);
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileFragment(rootView);
            }
        });

        // tombol kategori
        catWisata = rootView.findViewById(R.id.catWisata);
        catKuliner = rootView.findViewById(R.id.catKuliner);
        catPenginapan = rootView.findViewById(R.id.catHomestay);
        catEvent = rootView.findViewById(R.id.catEvent);

        // Temukan tombol berdasarkan ID
        CardView button = rootView.findViewById(R.id.showWisata);
        CardView buttonShowEvent = rootView.findViewById(R.id.showEvent);
        CardView buttonShowKuliner = rootView.findViewById(R.id.showKuliner);
        CardView buttonShowPenginapan = rootView.findViewById(R.id.showPenginapan);

        // atur tombol kategori
        catWisata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), ListWisata.class);
                startActivity(i);
            }
        });

        catKuliner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ListKuliner.class);
                startActivity(i);
            }
        });

        catPenginapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ListPenginapan.class);
                startActivity(i);
            }
        });

        catEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ListEvent.class);
                startActivity(i);
            }
        });

        // Atur OnClickListener pada tombol
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ketika tombol diklik, buat intent untuk menuju aktivitas baru
                Intent intent = new Intent(getActivity(), ListWisata.class); // Gantilah 'AktivitasTujuan' dengan aktivitas yang ingin Anda tuju
                startActivity(intent);
            }
        });

        buttonShowEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListEvent.class);
                startActivity(intent);
            }
        });

        buttonShowKuliner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListKuliner.class);
                startActivity(intent);
            }
        });

        buttonShowPenginapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListPenginapan.class);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerviewListPenginapanRekomendasi);

        Client.getInstance().penginapanpopuler("rekomendasi_penginapan").enqueue(new Callback<PenginapanResponse>() {
            @Override
            public void onResponse(Call<PenginapanResponse> call, Response<PenginapanResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    penginapanList = response.body().getData();
                    adapter = new RekomendasiPenginapanAdapter(penginapanList, new RekomendasiPenginapanAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            startActivity(
                                    new Intent(requireContext(), DetailPenginapan.class)
                                            .putExtra(DetailPenginapan.ID_PENGINAPAN, penginapanList.get(position).getIdPenginapan())
                            );
                        }
                    });
                    recyclerView.setAdapter(adapter);

                } else {
                    Toast.makeText(requireContext(), "data kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PenginapanResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "timeout", Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView recyclerView1 = rootView.findViewById(R.id.recyclerviewListWisataPopuler);

        Client.getInstance().wisatapopuler("rekomendasi_wisata").enqueue(new Callback<WisataResponse>() {
            @Override
            public void onResponse(Call<WisataResponse> call, Response<WisataResponse> response) {
                if(response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    wisataArraylist = response.body().getData();
                    adapter2 = new RekomendasiWisataAdapter(wisataArraylist, new RekomendasiWisataAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            startActivity(
                                    new Intent(requireContext(), DetailInformasi.class)
                                            .putExtra(DetailInformasi.ID_WISATA, wisataArraylist.get(position).getIdwisata())
                            );
                        }
                    });
                    recyclerView1.setAdapter(adapter2);
                } else {
                    Toast.makeText(requireContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WisataResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Request Timeout", Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView recyclerView2 = rootView.findViewById(R.id.recyclerviewListKulinerPopuler);

        Client.getInstance().kulinerpopuler("rekomendasi_kuliner").enqueue(new Callback<KulinerResponse>() {
            @Override
            public void onResponse(Call<KulinerResponse> call, Response<KulinerResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    KulinerArrayList = response.body().getData();
                    adapter3 = new RekomendasiKulinerAdapter(KulinerArrayList, new RekomendasiKulinerAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            startActivity(
                                    new Intent(requireContext(), DetailKuliner.class)
                                            .putExtra(DetailKuliner.ID_KULINER, KulinerArrayList.get(position).getIdKuliner())
                            );
                        }
                    });
                    recyclerView2.setAdapter(adapter3);
                } else {
                    Toast.makeText(requireContext(), "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KulinerResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Request Timeout", Toast.LENGTH_SHORT).show();
            }
        });

        ViewPager2 viewPager = rootView.findViewById(R.id.viewpagerEvent);

        Client.getInstance().upcomingevent("rekomendasi_event").enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {

                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    ArrayList<EventModel> datalist = response.body().getData();
                    adapter5 = new ViewpagerAdapter(datalist);
                    viewPager.setAdapter(adapter5);
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Timeout", Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;

    }

    public void showProfileFragment(View view) {
        // Ganti dengan logika untuk menampilkan fragment Profile
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Aktifkan kembali MenuItem
        Dashboard dbr = new Dashboard();
        dbr.btnView.setSelectedItemId(R.id.miProfiles);

        Profiles profileFragment = new Profiles();
        fragmentTransaction.replace(R.id.frame, profileFragment);
        fragmentTransaction.addToBackStack(null); // Untuk menambahkan fragment ke back stack
        fragmentTransaction.commit();
    }

    public void showNotificationFragment(View view) {
        // Ganti dengan logika untuk menampilkan fragment notifikasi
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Dashboard dbr = new Dashboard();
        dbr.btnView.setSelectedItemId(R.id.miNotify);

        Notify notifyFragment = new Notify();
        fragmentTransaction.replace(R.id.frame, notifyFragment);
        fragmentTransaction.addToBackStack(null); // Untuk menambahkan fragment ke back stack
        fragmentTransaction.commit();
    }

//    public void click(View v) {
//        Intent intent = null;
//        switch(v.getId()) {
//            case R.id.searchbox: // R.id.textView1
//                intent = new Intent(requireContext(), SearchingHomepage.class);
//                break;
//        }
//        startActivity(intent);
//    }
}