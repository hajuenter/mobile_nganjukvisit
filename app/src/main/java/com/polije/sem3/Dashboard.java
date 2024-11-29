package com.polije.sem3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.polije.sem3.util.WebSocketService;

public class Dashboard extends AppCompatActivity {

    public static BottomNavigationView btnView;
    MenuItem dashboardMenuItem;
    Fragment selectedFragment = null;
    public static String fragmentToLoad = "0";
    private String getFragmentToLoad;

    private static final int PERMISSION_REQUEST_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getFragmentToLoad = getIntent().getStringExtra("fragmentToLoad");

//        if (
//                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED
//        ) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    PERMISSION_REQUEST_STORAGE);
//            Toast.makeText(this, "permission needed", Toast.LENGTH_SHORT).show();
//        }


        Dashboard.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, new Home())
                .commit();
        Intent serviceIntent = new Intent(Dashboard.this, WebSocketService.class);
        startService(serviceIntent);
        FloatingActionButton fab;
        btnView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        btnView.setBackground(null);
        btnView.setItemIconTintList(
                getResources().getColorStateList(R.color.bottom_nav_item_color)
        );
        btnView.setItemTextColor(
                getResources().getColorStateList(R.color.bottom_nav_item_color)
        );

        // disable menuitem
        dashboardMenuItem = btnView.getMenu().findItem(R.id.placeholder);
        btnView.setSelectedItemId(R.id.placeholder);
        dashboardMenuItem.setEnabled(false);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnView.setSelectedItemId(R.id.placeholder);
                selectedFragment = new Home();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, new Home()).commit();
            }
        });

        if ("Profiles".equals(getFragmentToLoad)) {
            btnView.setSelectedItemId(R.id.miProfiles);
            selectedFragment = new Profiles();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new Profiles()).commit();
        } else if ("Notify".equals(getFragmentToLoad)) {
            btnView.setSelectedItemId(R.id.miNotify);
            selectedFragment = new Notify();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new Notify()).commit();
        }


        btnView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.miBook:
                    // Handle book item click
                    selectedFragment = new Book();
                    this.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, new Book()).commit();
                    return true;
                case R.id.miFavs:
                    // Handle favorites item click
                    selectedFragment = new Favs();
                    this.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, new Favs()).commit();
                    return true;
                case R.id.placeholder:
                    // Handle placeholder item click
                    return true;
                case R.id.miNotify:
                    // Handle notification item click
                    selectedFragment = new Notify();
                    this.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, selectedFragment).commit();
                    return true;
                case R.id.miProfiles:
                    // Handle profiles item click
                    selectedFragment = new Profiles();
                    this.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, selectedFragment).commit();
                    return true;
            }

            return false;
        });



    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case PERMISSION_REQUEST_NOTIFICATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                }
//
//                return;
//            }
//        }
//    }
}