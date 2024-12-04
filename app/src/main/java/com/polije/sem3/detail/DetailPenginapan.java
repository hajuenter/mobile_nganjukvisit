package com.polije.sem3.detail;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.polije.sem3.R;
import com.polije.sem3.databinding.ActivityDetailInformasiBinding;
import com.polije.sem3.model.PenginapanModel;
import com.polije.sem3.model.UlasanModel;
import com.polije.sem3.adapter.UlasanModelAdapter;
import com.polije.sem3.response.DetailPenginapanResponse;
import com.polije.sem3.response.UlasanKirimResponse;
import com.polije.sem3.response.UlasanResponse;
import com.polije.sem3.response.UlasanResponse1;
import com.polije.sem3.network.Client;
import com.polije.sem3.util.DepthPageTransformer;
import com.polije.sem3.adapter.SliderAdapter;
import com.polije.sem3.util.UsersUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.polije.sem3.databinding.ActivityDetailPenginapanBinding;

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

public class DetailPenginapan extends AppCompatActivity implements MapListener, GpsStatus.Listener {
    public static String ID_PENGINAPAN;
    private String idSelected;
    private MapView mMap;
    private IMapController controller;
    private Button btnLink;
    private PenginapanModel penginapanData;
    private TextView emptyTextView;
    private boolean availablelinkmaps;
    private String destination;
    private UlasanModel ulasansayaList;
    private UsersUtil usersUtil;
    private String idpengguna, fullnama, getComment,isiulasan;
    private Float isirating;
    private MapView mapView;
    private UlasanModelAdapter adapterUlasan;
    private LinearLayout layoutComment, layoutEditComment, layoutModifyButton;

    // View Binding instance
    private ActivityDetailPenginapanBinding binding;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize binding
        binding = ActivityDetailPenginapanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<>(this, new ArrayList<>(), null);
        emptyTextView = new TextView(DetailPenginapan.this);
        idSelected = getIntent().getStringExtra(idSelected);
        availablelinkmaps = true;
        destination = "";
        usersUtil = new UsersUtil(this);
        idpengguna = usersUtil.getId();
        fullnama = usersUtil.getUsername();
        binding = ActivityDetailPenginapanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        );
        // Set up RecyclerView with Adapter
        binding.recyclerviewUlasan.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerviewUlasan.setHasFixedSize(true);
        layoutComment = binding.CommentSection;
        layoutComment.setVisibility(View.VISIBLE);
        layoutEditComment = binding.editCommentSection;
        layoutEditComment.setVisibility(View.GONE);
        layoutModifyButton = binding.layoutModifyButton;
        layoutModifyButton.setVisibility(View.GONE);
        binding.txtEditUlasan.setEnabled(false);

        Client.getInstance().detailpenginapan("detail_penginapan", idSelected).enqueue(new Callback<DetailPenginapanResponse>() {
            @Override
            public void onResponse(Call<DetailPenginapanResponse> call, Response<DetailPenginapanResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    penginapanData = response.body().getData();

                    // Set data to the views using binding
                    binding.namaPenginapan.setText(penginapanData.getJudulPenginapan());
                    binding.alamatPenginapan.setText(penginapanData.getLokasi());
                    binding.deskripsiPenginapan.setText(penginapanData.getDeskripsi());
                    binding.notelp.setText(penginapanData.getTelepon().isEmpty() ? "Tidak Diketahui" : penginapanData.getTelepon());
                    /*Glide.with(DetailPenginapan.this).load(Client.IMG_DATA + penginapanData.getGambar()).into(binding.penginapanImage);*/
                    String gambarString = penginapanData.getGambar();
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
                    SliderAdapter adapter = new SliderAdapter(DetailPenginapan.this, imageUrls);
                    slider.setAdapter(adapter);
                    slider.setPageTransformer(new DepthPageTransformer());
                    String coordinates = penginapanData.getCoordinate();
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
                        mMap.addMapListener(DetailPenginapan.this);
                    }

                    if (penginapanData.getLinkmaps().isEmpty()) {
                        availablelinkmaps = false;
                        destination = null;
                    } else {
                        String destination1 = penginapanData.getLinkmaps().replace("\\","");
                        destination = destination1;
                        Log.d("cek1 link", "onCreate: "+destination);

                    }
                    getUlasan();
                } else {
                    Toast.makeText(DetailPenginapan.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DetailPenginapanResponse> call, Throwable t) {
                Toast.makeText(DetailPenginapan.this, "Request Timeout", Toast.LENGTH_SHORT).show();
            }


        });
        Client.getInstance().ulasan("get_all_ulasan","ulasan_penginapan",idSelected).enqueue(new Callback<UlasanResponse>() {
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
                    Toast.makeText(DetailPenginapan.this, "Tidak ada ulasan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UlasanResponse> call, Throwable t) {
                Toast.makeText(DetailPenginapan.this, "Request Timeout", Toast.LENGTH_SHORT).show();
            }
        });
        mapView = binding.osmmap;
        ScrollView scrollView = findViewById(R.id.scrollviewLayout);

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
        btnLink = findViewById(R.id.mapsPenginapan);

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
                Toast.makeText(DetailPenginapan.this, "Lokasi maps tidak tersedia", Toast.LENGTH_SHORT).show();
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
                binding.txtEditUlasan.setText(getComment);
                binding.ratingBareditUlasan.setRating(ratingValue);
                if(!getComment.isEmpty()) {
                    Client.getInstance().kirimulasan("add_ulasan","ulasan_penginapan",idpengguna, fullnama, getComment, String.valueOf(ratingValue),idSelected).enqueue(new Callback<UlasanKirimResponse>() {
                        @Override
                        public void onResponse(Call<UlasanKirimResponse> call, Response<UlasanKirimResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                Client.getInstance().ulasan("get_all_ulasan","ulasan_penginapan",idSelected).enqueue(new Callback<UlasanResponse>() {
                                    @Override
                                    public void onResponse(Call<UlasanResponse> call, Response<UlasanResponse> response) {
                                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                            if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                                                adapterUlasan = new UlasanModelAdapter(response.body().getData());
                                                binding.ratingBarUlasan.setRating(0.0f);
                                                binding.linearLayoutUlasan.setPadding(0,0,0,0);
                                                binding.recyclerviewUlasan.setAdapter(adapterUlasan);
                                                layoutComment.setVisibility(View.GONE);
                                                layoutEditComment.setVisibility(View.VISIBLE);
                                                layoutModifyButton.setVisibility(View.GONE);
                                                binding.btnModify.setVisibility(View.VISIBLE);

                                                Client.getInstance().ulasansaya("get_all_ulasan","ulasan_penginapan",idSelected, idpengguna).enqueue(new Callback<UlasanKirimResponse>() {
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
                                                        Toast.makeText(DetailPenginapan.this, "Timeout", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            } else {
                                                Toast.makeText(DetailPenginapan.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(DetailPenginapan.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UlasanResponse> call, Throwable t) {
                                        Toast.makeText(DetailPenginapan.this, "Request Timeout", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Toast.makeText(DetailPenginapan.this, "Berhasil memberi ulasan.", Toast.LENGTH_SHORT).show();
                                binding.txtAddComment.setText(null);
                            } else if (response.body() != null && response.body().getStatus().equalsIgnoreCase("fail")) {
                                Toast.makeText(DetailPenginapan.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DetailPenginapan.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UlasanKirimResponse> call, Throwable t) {
                            Toast.makeText(DetailPenginapan.this, "Request Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(DetailPenginapan.this, "Anda harus mengisi komentar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // cek ulasansaya

        Client.getInstance().ulasansaya("get_all_ulasan","ulasan_penginapan",idSelected, idpengguna).enqueue(new Callback<UlasanKirimResponse>() {
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
                Float ratingedit = binding.ratingBareditUlasan.getRating();
                if (commentValue != null && commentValue.isEmpty()) {
                    Toast.makeText(DetailPenginapan.this, "Komentar Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
                } else {
                    binding.layoutModifyButton.setVisibility(View.GONE);
                    binding.btnModify.setVisibility(View.VISIBLE);
                    binding.txtEditUlasan.setEnabled(true);

                    Client.getInstance().editulasan("edit_ulasan","ulasan_penginapan",commentValue, idSelected,ratingedit,usersUtil.getUsername(), idpengguna).enqueue(new Callback<UlasanResponse1>() {
                        @Override
                        public void onResponse(Call<UlasanResponse1> call, Response<UlasanResponse1> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                layoutComment.setVisibility(View.VISIBLE);
                                layoutEditComment.setVisibility(View.GONE);
                                getUlasan();
                                Toast.makeText(DetailPenginapan.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DetailPenginapan.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UlasanResponse1> call, Throwable t) {
                            Toast.makeText(DetailPenginapan.this, "Timeout", Toast.LENGTH_SHORT).show();
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

        // Set onClick listeners
//        binding.mapsPenginapan.setOnClickListener(v -> openMap());
    }


//    private void openMap() {
//        if (availablelinkmaps) {
//            String mapUri =  destination.replace("\\","");
//            Uri gmmIntentUri = Uri.parse(mapUri);
//            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//            mapIntent.setPackage("com.google.android.apps.maps");
//
//            if (mapIntent.resolveActivity(getPackageManager()) != null) {
//                startActivity(mapIntent);
//            } else {
//                Toast.makeText(this, "Aplikasi Google Maps tidak tersedia.", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "Lokasi maps tidak tersedia", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void getUlasan() {
        Client.getInstance().ulasan("get_all_ulasan","ulasan_penginapan",idSelected).enqueue(new Callback<UlasanResponse>() {
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
                    Toast.makeText(DetailPenginapan.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UlasanResponse> call, Throwable t) {
                Toast.makeText(DetailPenginapan.this, "Request Timeout", Toast.LENGTH_SHORT).show();
            }
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
        Client.getInstance().deleteulasan("delete_ulasan","ulasan_penginapan",idpengguna, idSelected).enqueue(new Callback<UlasanResponse>() {
            @Override
            public void onResponse(Call<UlasanResponse> call, Response<UlasanResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    layoutComment.setVisibility(View.VISIBLE);
                    layoutEditComment.setVisibility(View.GONE);
                    getUlasan();
                    Toast.makeText(DetailPenginapan.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailPenginapan.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UlasanResponse> call, Throwable t) {
                Toast.makeText(DetailPenginapan.this, "Timeout", Toast.LENGTH_SHORT).show();
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
