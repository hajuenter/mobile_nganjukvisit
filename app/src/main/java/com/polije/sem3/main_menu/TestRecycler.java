package com.polije.sem3.main_menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.polije.sem3.R;
import com.polije.sem3.model.WisataModel;
import com.polije.sem3.adapter.WisataModelAdapter;

import java.util.ArrayList;

public class TestRecycler extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WisataModelAdapter adapter;
    private ArrayList<WisataModel> WisataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testrecycler);

//        addData();

        recyclerView = findViewById(R.id.my_recycler_view);

        adapter = new WisataModelAdapter(WisataArrayList, new WisataModelAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(TestRecycler.this, WisataArrayList.get(position).getNama(), Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TestRecycler.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

//    void addData() {
//        WisataArrayList = new ArrayList<>();
//        WisataArrayList.add(new WisataModel("Sedudo", "Sedudo adalah bla bla bla bla"));
//        WisataArrayList.add(new WisataModel("Taman Nyawiji", "Taman Nyawiji adalah bla bla bla bla"));
//    }
}