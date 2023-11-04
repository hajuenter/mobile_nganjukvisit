package com.polije.sem3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.GpsStatus;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.polije.sem3.databinding.ActivityDetailInformasiBinding;
import com.polije.sem3.databinding.ActivityMapJavaBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class DetailInformasi extends AppCompatActivity implements MapListener, GpsStatus.Listener{

    private MapView mMap;
    private IMapController controller;
    private MyLocationNewOverlay mMyLocationOverlay;
    private Button btnLink;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_informasi);

        ActivityDetailInformasiBinding binding = ActivityDetailInformasiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        );
        mMap = binding.osmmap;
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.getMapCenter();
        mMap.setMultiTouchControls(true);
        mMap.getLocalVisibleRect(new Rect());

        ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<>(this, new ArrayList<>(), null);
        GeoPoint startPoint = new GeoPoint(-7.78142419909586, 111.75760589038738);
        OverlayItem overlayItem = new OverlayItem("Marker Title", "Marker Description", startPoint);
        Drawable marker = getResources().getDrawable(R.drawable.locationpin); // Ganti dengan gambar marker Anda
        marker.setBounds(0, 0, 10, 10);
        overlayItem.setMarker(marker);

        controller = mMap.getController();

        controller.setCenter(startPoint);
        controller.animateTo(startPoint);
        controller.setZoom(16);

        Log.e("TAG", "onCreate:in " + controller.zoomIn());
        Log.e("TAG", "onCreate: out " + controller.zoomOut());

        itemizedIconOverlay.addItem(overlayItem);
        mMap.getOverlays().add(itemizedIconOverlay);
        mMap.addMapListener(this);

        MapView mapView = findViewById(R.id.osmmap);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollviewLayout);

        // smooth scroll map
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Ketika pengguna menekan peta, nonaktifkan pengguliran ScrollView
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Ketika pengguna melepaskan peta, izinkan pengguliran ScrollView kembali
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        btnLink = findViewById(R.id.directToMaps);

        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destination = "Air+Terjun+Sedudo"; // Gantilah dengan nama atau alamat tujuan Anda
                String mapUri = "https://www.google.com/maps/search/?api=1&query=" + destination;
//                String mapUri = "https://maps.app.goo.gl/" + destination;

                Uri gmmIntentUri = Uri.parse(mapUri);

                // Buat intent untuk membuka Google Maps
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps"); // Hanya buka dengan aplikasi Google Maps

                // Periksa apakah aplikasi Google Maps terpasang
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe) {
                    // Buka aplikasi Google Maps
                    startActivity(mapIntent);
                } else {
                    // Jika Google Maps tidak terpasang, Anda dapat menampilkan pesan kesalahan
                    Toast.makeText(getApplicationContext(), "Aplikasi Google Maps tidak tersedia.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

        @Override
        public boolean onScroll(ScrollEvent event) {
            Log.e("TAG", "onCreate:la " + event.getSource().getMapCenter().getLatitude());
            Log.e("TAG", "onCreate:lo " + event.getSource().getMapCenter().getLongitude());
            return true;
        }

        @Override
        public boolean onZoom(ZoomEvent event) {
            Log.e("TAG", "onZoom zoom level: " + event.getZoomLevel() + "   source:  " + event.getSource());
            return false;
        }

        @Override
        public void onGpsStatusChanged(int event) {
            // Handle GPS status changes
        }



    }