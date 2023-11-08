package com.polije.sem3;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polije.sem3.util.UsersUtil;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView namaPengguna;
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

//        getnamapengguna
        UsersUtil userUtil = new UsersUtil(requireContext());
        namaPengguna = (TextView) rootView.findViewById(R.id.namaLengkapPengguna);
        namaPengguna.setText(userUtil.getFullName());

        // Temukan tombol berdasarkan ID
        CardView button = rootView.findViewById(R.id.showWisata);
        CardView buttonShowEvent = rootView.findViewById(R.id.showEvent);
        CardView buttonShowKuliner = rootView.findViewById(R.id.showKuliner);
        CardView buttonShowPenginapan = rootView.findViewById(R.id.showPenginapan);

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

        return rootView;
    }
}