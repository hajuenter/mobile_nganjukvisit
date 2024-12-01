package com.polije.sem3.detail;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.polije.sem3.Booking;
import com.polije.sem3.R;
import com.polije.sem3.databinding.ActivityDetailInformasiBinding;
import com.polije.sem3.model.UlasanModel;
import com.polije.sem3.model.UlasanModelAdapter;
import com.polije.sem3.model.WisataModel;
import com.polije.sem3.response.DetailWisataResponse;
import com.polije.sem3.response.UlasanKirimResponse;
import com.polije.sem3.response.UlasanResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.DepthPageTransformer;
import com.polije.sem3.util.SliderAdapter;
import com.polije.sem3.util.UsersUtil;

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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailInformasi extends AppCompatActivity implements MapListener, GpsStatus.Listener {

    private MapView mMap;
    private IMapController controller;
    private MyLocationNewOverlay mMyLocationOverlay;
    private Button btnLink,buttonBooking;
    private WisataModel dataListWisata;
    private UlasanModelAdapter adapterUlasan;
    private UlasanModel ulasansayaList;

    public static String ID_WISATA = "id";

    private String idSelected;
    private String idpengguna;
    private TextView emptyTextView;
    private String destination;
    private String getComment;
    private LinearLayout layoutComment, layoutEditComment, layoutModifyButton;

    private ActivityDetailInformasiBinding binding;

    private MapView mapView;
    private ViewPager2 viewPager;
    private ItemizedIconOverlay<OverlayItem> itemizedIconOverlay;

    private boolean availablelinkmaps;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_informasi);

        idSelected = getIntent().getStringExtra(ID_WISATA);

        itemizedIconOverlay = new ItemizedIconOverlay<>(this, new ArrayList<>(), null);

        availablelinkmaps = true; // TextView Harga Tiket Navbar
        emptyTextView = new TextView(DetailInformasi.this);

        binding = ActivityDetailInformasiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        );


        layoutComment = binding.CommentSection;
        layoutComment.setVisibility(View.VISIBLE);
        layoutEditComment = binding.editCommentSection;
        layoutEditComment.setVisibility(View.GONE);
        layoutModifyButton = binding.layoutModifyButton;
        layoutModifyButton.setVisibility(View.GONE);
        binding.txtEditUlasan.setEnabled(false);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            binding.deskripsiWisata.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
//        }

        // kodingan retrofit get data
        Client.getInstance().detailwisata("detail_wisata",idSelected).enqueue(new Callback<DetailWisataResponse>() {

            @Override
            public void onResponse(Call<DetailWisataResponse> call, Response<DetailWisataResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    dataListWisata = response.body().getData();
                    String coordinates = dataListWisata.getCoordinate();
                    String linkmaps = dataListWisata.getLinkmaps();

                    if (!coordinates.isEmpty()){
                        String[] words = coordinates.split(",");
                        double firstCoordinates = Double.parseDouble(words[0].trim());  // Trim to remove leading and trailing whitespaces
                        double secondCoordinates = Double.parseDouble(words[1].trim());

                        mMap = binding.osmmap;
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
                        mMap.addMapListener(DetailInformasi.this);
                    }

                    if (linkmaps.isEmpty()) {
                        Toast.makeText(DetailInformasi.this, "Lokasi maps tidak tersedia", Toast.LENGTH_SHORT).show();
//                        destination = "Air+Terjun+Sedudo";
                        availablelinkmaps = false;
                    } else if (!linkmaps.isEmpty()) {
                        destination = linkmaps;
                    }
                    String gambarString = dataListWisata.getGambar();
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
                    SliderAdapter adapter = new SliderAdapter(DetailInformasi.this, imageUrls);
                    slider.setAdapter(adapter);
                    slider.setPageTransformer(new DepthPageTransformer());
                    binding.namaWisata.setText(dataListWisata.getNama());
                    binding.deskripsiWisata.setText(dataListWisata.getDeskripsi());
                    binding.jamOperasional.setText(dataListWisata.getJadwal());
                    String text = dataListWisata.getJadwal();
                    String formattedText = text.replace(",", "<br>");  // Mengganti koma dengan tag <br> untuk line break
                    binding.jamOperasional.setText(Html.fromHtml(formattedText));
                    String hargaTiket =dataListWisata.getHarga_tiket();
                    if (hargaTiket.equalsIgnoreCase("Gratis")) {
                        binding.hargaTiket.setText("Gratis");
                        binding.hargaTiketNavbar.setText("Gratis");
                    } else {
                        try {double ticketPrice = Double.parseDouble(hargaTiket);
                            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                            String formattedPrice = currencyFormat.format(ticketPrice);
                            binding.hargaTiket.setText(formattedPrice);
                            binding.hargaTiketNavbar.setText(formattedPrice+"/orang");
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    if (dataListWisata.getHarga_tiket().equalsIgnoreCase("Gratis")) {
                        binding.buttonbooking.setVisibility(View.GONE);
                    } else {
                        binding.buttonbooking.setVisibility(View.VISIBLE);
                    }
                    binding.alamatWisata.setText(dataListWisata.getAlamat());

                }
            }

            @Override
            public void onFailure(Call<DetailWisataResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(DetailInformasi.this, "ERROR -> " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("DetailInformasi", "error " + t.getMessage());
            }
        });
            binding.buttonbooking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Button", "kepencet");
                    // Membuat Intent untuk pindah ke BookingActivity
                    Intent intent = new Intent(DetailInformasi.this, Booking.class);

                    // Menambahkan data ke Intent
                    intent.putExtra("idWisata", idSelected); // Menambahkan ID Wisata
                    intent.putExtra("namaWisata", dataListWisata.getNama()); // Menambahkan Nama Wisata
                    intent.putExtra("hargaTiket", dataListWisata.getHarga_tiket());
                    intent.putExtra("nohp", dataListWisata.getNo_hp());// Menambahkan Harga Tiket

                    // Memulai BookingActivity dengan membawa data
                    startActivity(intent);
                }
            });

//        RecyclerView recyclerView = findViewById(R.id.recyclerviewUlasan);

        Client.getInstance().ulasan("get_all_ulasan","ulasan_wisata",idSelected).enqueue(new Callback<UlasanResponse>() {
            @Override
            public void onResponse(Call<UlasanResponse> call, Response<UlasanResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                        adapterUlasan = new UlasanModelAdapter(response.body().getData());
                        binding.recyclerviewUlasan.setAdapter(adapterUlasan);
                    } else {
                        emptyTextView.setText("Belum ada ulasan");
                        emptyTextView.setGravity(Gravity.CENTER);
                        binding.linearLayoutUlasan.setPadding(10, 100, 50, 100);
                        binding.linearLayoutUlasan.addView(emptyTextView);
                    }
                } else {
                    Toast.makeText(DetailInformasi.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UlasanResponse> call, Throwable t) {
                Toast.makeText(DetailInformasi.this, "Request Timeout", Toast.LENGTH_SHORT).show();
            }
        });

        mapView = findViewById(R.id.osmmap);
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

        btnLink.setOnClickListener(v -> {

            if (availablelinkmaps) {

                // Membuat URL pencarian di Google Maps
                String mapUri =  destination;

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
                Toast.makeText(DetailInformasi.this, "Lokasi maps tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });



        UsersUtil usersUtil = new UsersUtil(this);
        idpengguna = usersUtil.getId();
        String fullnama = usersUtil.getUsername();

        binding.btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RatingBar ratingBar = findViewById(R.id.ratingBarUlasan);
                getComment = String.valueOf(binding.txtAddComment.getText());
                float ratingValue = ratingBar.getRating();
                if(!getComment.isEmpty()) {
                    Client.getInstance().kirimulasan("add_ulasan","ulasan_wisata",idpengguna, fullnama, getComment, String.valueOf(ratingValue),idSelected).enqueue(new Callback<UlasanKirimResponse>() {
                        @Override
                        public void onResponse(Call<UlasanKirimResponse> call, Response<UlasanKirimResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                Client.getInstance().ulasan("get_all_ulasan","ulasan_wisata",idSelected).enqueue(new Callback<UlasanResponse>() {
                                    @Override
                                    public void onResponse(Call<UlasanResponse> call, Response<UlasanResponse> response) {
                                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                            if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                                                adapterUlasan = new UlasanModelAdapter(response.body().getData());
                                                emptyTextView.setVisibility(View.GONE);
                                                binding.linearLayoutUlasan.setPadding(0,0,0,0);
                                                binding.recyclerviewUlasan.setAdapter(adapterUlasan);
                                                layoutComment.setVisibility(View.GONE);
                                                layoutEditComment.setVisibility(View.VISIBLE);
                                                layoutModifyButton.setVisibility(View.GONE);
                                                binding.btnModify.setVisibility(View.VISIBLE);

                                                Client.getInstance().ulasansaya("get_all_ulasan","ulasan_wisata",idSelected, idpengguna).enqueue(new Callback<UlasanKirimResponse>() {
                                                    @Override
                                                    public void onResponse(Call<UlasanKirimResponse> call, Response<UlasanKirimResponse> response) {
                                                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                                            ulasansayaList = response.body().getData();
                                                            if (ulasansayaList != null && ulasansayaList.getUlasan() != null) {
                                                                layoutEditComment.setVisibility(View.VISIBLE);
                                                                binding.tanggalKomen.setText(ulasansayaList.getDateTime());
                                                                binding.txtEditUlasan.setText(ulasansayaList.getUlasan());
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<UlasanKirimResponse> call, Throwable t) {
                                                        Toast.makeText(DetailInformasi.this, "Timeout", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            } else {
                                                Toast.makeText(DetailInformasi.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(DetailInformasi.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UlasanResponse> call, Throwable t) {
                                        Toast.makeText(DetailInformasi.this, "Request Timeout", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Toast.makeText(DetailInformasi.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                binding.txtAddComment.setText(null);
                            } else if (response.body() != null && response.body().getStatus().equalsIgnoreCase("fail")) {
                                Toast.makeText(DetailInformasi.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DetailInformasi.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UlasanKirimResponse> call, Throwable t) {
                            Toast.makeText(DetailInformasi.this, "Request Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(DetailInformasi.this, "Anda harus mengisi komentar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // cek ulasansaya

        Client.getInstance().ulasansaya("get_all_ulasan","ulasan_wisata",idSelected, idpengguna).enqueue(new Callback<UlasanKirimResponse>() {
            @Override
            public void onResponse(Call<UlasanKirimResponse> call, Response<UlasanKirimResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    ulasansayaList = response.body().getData();
                    if (ulasansayaList != null && ulasansayaList.getUlasan() != null) {
                       layoutEditComment.setVisibility(View.VISIBLE);
                       binding.tanggalKomen.setText(ulasansayaList.getDateTime());
                        binding.txtEditUlasan.setText(ulasansayaList.getUlasan());
                        layoutComment.setVisibility(View.GONE);
                    } else {
                        layoutComment.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<UlasanKirimResponse> call, Throwable t) {
                Toast.makeText(DetailInformasi.this, "Timeout", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutModifyButton.setVisibility(View.VISIBLE);
                binding.txtEditUlasan.setEnabled(true);
                binding.txtEditUlasan.requestFocus();
                binding.btnModify.setVisibility(View.GONE);


            }
        });



        binding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentValue = binding.txtEditUlasan.getText().toString();
                String ratingedit = String.valueOf(binding.ratingBareditUlasan.getRating());
                if (commentValue != null && commentValue.isEmpty()) {
                    Toast.makeText(DetailInformasi.this, "Komentar Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
                } else {
                    binding.layoutModifyButton.setVisibility(View.GONE);
                    binding.btnModify.setVisibility(View.VISIBLE);
                    binding.txtEditUlasan.setEnabled(true);

                    Client.getInstance().editulasan("edit_ulasan","ulasan_wisata",commentValue, idSelected,ratingedit,usersUtil.getUsername(), idpengguna).enqueue(new Callback<UlasanResponse>() {
                        @Override
                        public void onResponse(Call<UlasanResponse> call, Response<UlasanResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                layoutComment.setVisibility(View.VISIBLE);
                                layoutEditComment.setVisibility(View.GONE);
                                getUlasan();
                                Toast.makeText(DetailInformasi.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DetailInformasi.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UlasanResponse> call, Throwable t) {
                            Toast.makeText(DetailInformasi.this, "Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        binding.backButtonDetail.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Hapus");
        builder.setMessage("Apakah anda yakin ingin menghapus ulasan anda?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform the delete action here
                    performDeleteAction();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "No" - do nothing or handle accordingly
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void performDeleteAction() {
        Client.getInstance().deleteulasan("delete_ulasan","ulasan_wisata",idpengguna, idSelected).enqueue(new Callback<UlasanResponse>() {
            @Override
            public void onResponse(Call<UlasanResponse> call, Response<UlasanResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    layoutComment.setVisibility(View.VISIBLE);
                    layoutEditComment.setVisibility(View.GONE);
                    getUlasan();
                    Toast.makeText(DetailInformasi.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailInformasi.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UlasanResponse> call, Throwable t) {
                Toast.makeText(DetailInformasi.this, "Timeout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUlasan() {
        Client.getInstance().ulasan("get_all_ulasan","ulasan_wisata",idSelected).enqueue(new Callback<UlasanResponse>() {
            @Override
            public void onResponse(Call<UlasanResponse> call, Response<UlasanResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                        adapterUlasan = new UlasanModelAdapter(response.body().getData());
                        binding.recyclerviewUlasan.setAdapter(adapterUlasan);
                    }
                } else {
                    Toast.makeText(DetailInformasi.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UlasanResponse> call, Throwable t) {
                Toast.makeText(DetailInformasi.this, "Request Timeout", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}