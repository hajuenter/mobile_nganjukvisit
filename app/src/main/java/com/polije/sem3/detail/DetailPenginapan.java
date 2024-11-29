package com.polije.sem3.detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.polije.sem3.R;
import com.polije.sem3.model.PenginapanModel;
import com.polije.sem3.model.UlasanModel;
import com.polije.sem3.model.UlasanModelAdapter;
import com.polije.sem3.response.DetailPenginapanResponse;
import com.polije.sem3.response.UlasanKirimResponse;
import com.polije.sem3.response.UlasanResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.DepthPageTransformer;
import com.polije.sem3.util.SliderAdapter;
import com.polije.sem3.util.UsersUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.polije.sem3.databinding.ActivityDetailPenginapanBinding;

import java.util.ArrayList;
import java.util.List;

public class DetailPenginapan extends AppCompatActivity {
    public static String ID_PENGINAPAN;
    private String idSelected;
    private PenginapanModel penginapanData;
    private boolean availablelinkmaps;
    private String destination;
    private UsersUtil usersUtil;
    private String idpengguna, fullnama, getComment;
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

        idSelected = getIntent().getStringExtra(idSelected);
        availablelinkmaps = true;
        destination = "";
        usersUtil = new UsersUtil(this);
        idpengguna = usersUtil.getId();
        fullnama = usersUtil.getUsername();

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
                    if (penginapanData.getLinkmaps().isEmpty()) {
                        availablelinkmaps = false;
                        destination = null;
                    } else {
                        availablelinkmaps = true;
                        destination = penginapanData.getLinkmaps();
                    }
                } else {
                    Toast.makeText(DetailPenginapan.this, "Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DetailPenginapanResponse> call, Throwable t) {
                Toast.makeText(DetailPenginapan.this, "Request Timeout", Toast.LENGTH_SHORT).show();
            }
        });

        // Set onClick listeners
        binding.mapsPenginapan.setOnClickListener(v -> openMap());
        binding.btnSendComment.setOnClickListener(v -> sendComment());
        binding.btnModify.setOnClickListener(v -> editComment());
        binding.deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        binding.backButtonDetail.setOnClickListener(v -> onBackPressed());
    }

    private void openMap() {
        if (availablelinkmaps) {
            String mapUri = "geo:0,0?q=" + destination;
            Uri gmmIntentUri = Uri.parse(mapUri);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "Aplikasi Google Maps tidak tersedia.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Lokasi maps tidak tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendComment() {
        getComment = binding.txtAddComment.getText().toString();
        float ratingValue = binding.ratingBarUlasan.getRating();

        if (!getComment.isEmpty()) {
            Client.getInstance().kirimulasan("add_ulasan", "ulasan_penginapan", idpengguna, fullnama, getComment, String.valueOf(ratingValue), idSelected)
                    .enqueue(new Callback<UlasanKirimResponse>() {
                        @Override
                        public void onResponse(Call<UlasanKirimResponse> call, Response<UlasanKirimResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                refreshUlasan();
                                binding.txtAddComment.setText(null);
                                layoutComment.setVisibility(View.GONE);
                                layoutEditComment.setVisibility(View.VISIBLE);
                                layoutModifyButton.setVisibility(View.GONE);
                                binding.btnModify.setVisibility(View.VISIBLE);
                                Toast.makeText(DetailPenginapan.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DetailPenginapan.this, "Gagal menambahkan ulasan", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UlasanKirimResponse> call, Throwable t) {
                            Toast.makeText(DetailPenginapan.this, "Request Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Anda harus mengisi komentar", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshUlasan() {
        Client.getInstance().ulasan("get_all_ulasan", "ulasan_penginapan", idSelected)
                .enqueue(new Callback<UlasanResponse>() {
                    @Override
                    public void onResponse(Call<UlasanResponse> call, Response<UlasanResponse> response) {
                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                            adapterUlasan = new UlasanModelAdapter(response.body().getData());
                            binding.recyclerviewUlasan.setAdapter(adapterUlasan);
                        }
                    }

                    @Override
                    public void onFailure(Call<UlasanResponse> call, Throwable t) {
                        Toast.makeText(DetailPenginapan.this, "Timeout", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void editComment() {
        binding.layoutModifyButton.setVisibility(View.VISIBLE);
        binding.txtEditUlasan.setEnabled(true);
        binding.txtEditUlasan.requestFocus();
        binding.btnModify.setVisibility(View.GONE);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah anda yakin ingin menghapus ulasan anda?")
                .setPositiveButton("Ya", (dialog, which) -> performDeleteAction())
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void performDeleteAction() {
        Client.getInstance().deleteulasan("delete_ulasan", "ulasan_penginapan", idpengguna, idSelected)
                .enqueue(new Callback<UlasanResponse>() {
                    @Override
                    public void onResponse(Call<UlasanResponse> call, Response<UlasanResponse> response) {
                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                            Toast.makeText(DetailPenginapan.this, "Ulasan berhasil dihapus", Toast.LENGTH_SHORT).show();
                            refreshUlasan();
                        } else {
                            Toast.makeText(DetailPenginapan.this, "Gagal menghapus ulasan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UlasanResponse> call, Throwable t) {
                        Toast.makeText(DetailPenginapan.this, "Request Timeout", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
