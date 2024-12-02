package com.polije.sem3.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.GpsStatus;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.polije.sem3.R;
import com.polije.sem3.model.KulinerModel;
import com.polije.sem3.response.DetailKulinerResponse;
import com.polije.sem3.network.Client;
import com.polije.sem3.util.DepthPageTransformer;
import com.polije.sem3.adapter.SliderAdapter;


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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailKuliner extends AppCompatActivity implements MapListener, GpsStatus.Listener{
    private MapView mapView;
    private MapView mMap;
    private IMapController controller;
    public static String ID_KULINER = "id";
    private String idSelected;
    private TextView namaKuliner, deskripsiKuliner,hargakuliner,alamatkuliner;
    private KulinerModel kulinerModel;
    private Button btnBack,btnLink;
    private String destination;
    private ItemizedIconOverlay<OverlayItem> itemizedIconOverlay;
    private boolean availablelinkmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kuliner);
        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE));
        idSelected = getIntent().getStringExtra(ID_KULINER);
        availablelinkmaps = true;
        itemizedIconOverlay = new ItemizedIconOverlay<>(this, new ArrayList<>(), null);
        namaKuliner = findViewById(R.id.namaKuliner);
        deskripsiKuliner = findViewById(R.id.deskripsiKuliner);
        hargakuliner = findViewById(R.id.hargaKuliner);
        alamatkuliner = findViewById(R.id.alamatKuliner);
        /*ImageView gambarCover = findViewById(R.id.KulinerImage);*/
        btnBack = findViewById(R.id.backButtonDetail);

//        namaKuliner.setText(idSelected);

        Client.getInstance().detailkuliner("detail_kuliner",idSelected).enqueue(new Callback<DetailKulinerResponse>() {
            @Override
            public void onResponse(Call<DetailKulinerResponse> call, Response<DetailKulinerResponse> response) {
                if(response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    kulinerModel = response.body().getData();
                    String getNamaKuliner =kulinerModel.getNama();
                    String getDeskripsiKuliner = kulinerModel.getDeskripsi();
                    String harga = kulinerModel.getHarga();

                    double kulinerprice = Double.parseDouble(harga);
                    namaKuliner.setText(getNamaKuliner);
                    String formattedText = kulinerModel.getLokasi().replace(",", "<br>").replace("'",",").replace(":",": ").replace("Alamat","<b>Alamat</b>");  // Mengganti koma dengan tag <br> untuk line break
                    alamatkuliner.setText(Html.fromHtml(formattedText));
                    deskripsiKuliner.setText(getDeskripsiKuliner);
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    String formattedTotalCost = currencyFormat.format(kulinerprice);
                    String Price = formattedTotalCost.replace("Rp","Rp.").replace(",00","").trim();
                    // Menampilkan hasil ke TextView
                    hargakuliner.setText(Price+"/porsi");

/*
                    Glide.with(DetailKuliner.this).load(Client.IMG_DATA + kulinerModel.getGambar()).into(gambarCover);
*/                  String coordinates = kulinerModel.getCoordinate();
                    String linkmaps = kulinerModel.getLinkmaps();

                    if (!coordinates.isEmpty()){
                        String[] words = coordinates.split(",");
                        double firstCoordinates = Double.parseDouble(words[0].trim());  // Trim to remove leading and trailing whitespaces
                        double secondCoordinates = Double.parseDouble(words[1].trim());

                        mMap = findViewById(R.id.osmmap);
                        mMap.setTileSource(TileSourceFactory.MAPNIK);
                        mMap.getMapCenter();
                        mMap.setMultiTouchControls(true);
                        mMap.getLocalVisibleRect(new Rect());
                        GeoPoint startPoint = new GeoPoint(firstCoordinates, secondCoordinates);
                        OverlayItem overlayItem = new OverlayItem("Marker Title", "Marker Description", startPoint);
                        Drawable marker = getResources().getDrawable(R.drawable.locationpin); // Ganti dengan gambar marker Anda
                        marker.setBounds(0, 0, 10, 10);
                        overlayItem.setMarker(marker);

                        controller = mMap.getController();
                        controller.setCenter(startPoint);
                        controller.animateTo(startPoint);
                        controller.setZoom(16);

                        Log.d("TAG", "onCreate:in " + controller.zoomIn());
                        Log.d("TAG", "onCreate: out " + controller.zoomOut());

                        itemizedIconOverlay.addItem(overlayItem);
                        mMap.getOverlays().add(itemizedIconOverlay);
                        mMap.addMapListener(DetailKuliner.this);
                    }

                    if (linkmaps.isEmpty()) {
                        Toast.makeText(DetailKuliner.this, "Lokasi maps tidak tersedia", Toast.LENGTH_SHORT).show();
//                        destination = "Air+Terjun+Sedudo";
                        availablelinkmaps = false;
                    } else if (!linkmaps.isEmpty()) {
                        String destination1 = linkmaps.replace("\\","");
                        Log.d("ini link", "onResponse: "+destination1);
                        destination = destination1;
                    }
                    String gambarString = kulinerModel.getGambar();
                    List<String> imageUrls = new ArrayList<>();
                    if (gambarString.contains(",")) {
                        String[] images = gambarString.split(",");
                        for (String image : images) {
                            imageUrls.add(Client.IMG_DATA + image.trim()); // Tambahkan base URL + gambar
                        }
                    } else {
                        // Jika hanya ada satu gambar
                        imageUrls.add(Client.IMG_DATA + gambarString.trim());
                    }
                    ViewPager2 slider = findViewById(R.id.slider);
                    SliderAdapter adapter = new SliderAdapter(DetailKuliner.this, imageUrls);
                    slider.setAdapter(adapter);
                    slider.setPageTransformer(new DepthPageTransformer());

                }
            }

            @Override
            public void onFailure(Call<DetailKulinerResponse> call, Throwable t) {

            }
        });
        btnLink = findViewById(R.id.directToMaps);

        btnLink.setOnClickListener(v -> {

            if (availablelinkmaps) {

                // Membuat URL pencarian di Google Maps
                String mapUri = destination;

                Uri gmmIntentUri = Uri.parse(mapUri);

                // Buat intent untuk membuka Google Maps
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                // Periksa apakah ada aplikasi yang bisa membuka link
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe) {
                    // Buka aplikasi Google Maps
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Aplikasi Google Maps tidak tersedia.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DetailKuliner.this, "Lokasi maps tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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