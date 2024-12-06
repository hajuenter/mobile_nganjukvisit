package com.polije.sem3.main_menu;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.polije.sem3.R;
import com.polije.sem3.model.UserModel;
import com.polije.sem3.network.BaseResponse;
import com.polije.sem3.network.Config;
import com.polije.sem3.network.UploadService;
import com.polije.sem3.response.UserResponse;
import com.polije.sem3.network.Client;
import com.polije.sem3.util.UsersUtil;
import com.polije.sem3.utility.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profiles extends Fragment {

    private static final int PICK_IMAGE = 1;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap selectedBitmap;
    private static final int PERMISSION_REQUEST_STORAGE = 2;
    private String fotobaru;
    private Uri uri;
    private ImageView imgThumb;
    private EditText editNamaText, emailText, alamatText, notelpText;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    public Profiles() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profiles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Mengubah profil...");
        progressDialog.setMessage("Harap Tunggu");
        progressDialog.setCancelable(false);

        imgThumb = view.findViewById(R.id.img_thumb1);
        Button btnLogout = view.findViewById(R.id.btn_logout);
        TextView btnChoose = view.findViewById(R.id.choosePictures);
        Button btnUpload2 = view.findViewById(R.id.btn_upload_2);
        ScrollView scrollView = view.findViewById(R.id.scrollView);
        editNamaText = view.findViewById(R.id.textnama);
        editNamaText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Hanya izinkan huruf dan spasi
                String filteredText = s.toString().replaceAll("[^a-zA-Z ]", "");
                if (!s.toString().equals(filteredText)) {
                    editNamaText.setText(filteredText);
                    int cursorPosition = filteredText.length();
                    if (cursorPosition <= editNamaText.getText().length()) {
                        editNamaText.setSelection(cursorPosition); // Menjaga kursor tetap di akhir
                    } // Menjaga kursor di akhir
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        emailText = view.findViewById(R.id.edt_emailaddr);
        alamatText = view.findViewById(R.id.edt_alamat);
        alamatText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Regex untuk memfilter karakter yang tidak diperbolehkan
                String filteredText = s.toString().replaceAll("[^a-zA-Z0-9 ,.\\-]", "");

                // Jika teks berubah setelah difilter, perbarui EditText
                if (!s.toString().equals(filteredText)) {
                    alamatText.setText(filteredText);
                    int cursorPosition = filteredText.length();
                    if (cursorPosition <= alamatText.getText().length()) {
                        alamatText.setSelection(cursorPosition); // Menjaga kursor tetap di akhir
                    } // Menjaga kursor tetap di akhir
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        notelpText = view.findViewById(R.id.edt_notelp);
        if (notelpText.getText().equals("(Kosong)")){
            notelpText.setError("Harap isi Nomor Telepon");
        }else{
            notelpText.setError(null);
        }

        notelpText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String currentText = charSequence.toString();
                String filteredText = currentText.replaceAll("[^0-9]", ""); // Menghapus karakter selain angka

                // Jika teks yang dimasukkan tidak sesuai, set ulang EditText dengan teks yang telah difilter
                if (!currentText.equals(filteredText)) {
                    notelpText.setText(filteredText);
                    int cursorPosition = filteredText.length();
                    if (cursorPosition <= notelpText.getText().length()) {
                        notelpText.setSelection(cursorPosition); // Menjaga kursor tetap di akhir
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Disable email edit
        emailText.setEnabled(false);
        UsersUtil util = new UsersUtil(requireContext());

        // Read data from SharedPreferences
        String idPengguna = util.getId();
        String namaPengguna = util.getUsername();
        String emailPengguna = util.getEmail();
        String notelpPengguna = util.getNoTelp();
        String alamatPengguna = util.getAlamat();
        String gambarPengguna = util.getUserPhoto();
        // Display data in EditText and ImageView
        editNamaText.setText(namaPengguna);
        emailText.setText(emailPengguna);
        notelpText.setText(notelpPengguna);
        alamatText.setText(alamatPengguna);

        Log.d("Profiles", "ID Pengguna: " + idPengguna);
        Log.d("Profiles", "Nama Pengguna: " + namaPengguna);
        Log.d("Profiles", "Email Pengguna: " + emailPengguna);
        Log.d("Profiles", "No Telepon Pengguna: " + notelpPengguna);
        Log.d("Profiles", "Alamat Pengguna: " + alamatPengguna);
        Log.d("Profiles", "Gambar Pengguna: " + gambarPengguna);

        // Load user image
        if (gambarPengguna != null && !gambarPengguna.isEmpty()) {
            Glide.with(requireContext()).load(Config.API_IMAGE + gambarPengguna).into(imgThumb);
        } else {
            Log.d("Profiles", "No image URL found for user.");
        }

        // Scroll up for more visibility edit text
        setupScrollListener(scrollView);

        btnChoose.setOnClickListener(v -> openGallery());

        btnUpload2.setOnClickListener(v -> {
            progressDialog.show();
                updateProfiles(util.getUserPhoto());
                Log.d("Profiles", "Just Update String Information");

        });

        btnLogout.setOnClickListener(v -> {
            util.signOut();
            if (!util.isSignIn()) {
                Toast.makeText(requireActivity(), "Logout Sukses", Toast.LENGTH_SHORT).show();

                // Menghapus semua fragment di FragmentManager
                requireActivity().getSupportFragmentManager()
                        .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                // Membuat intent untuk aktivitas baru dan menghapus semua aktivitas sebelumnya
                Intent intent = new Intent(requireActivity(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                // Mengakhiri aktivitas saat ini
                requireActivity().finish();
            }
        });
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

// Listener untuk mendeteksi gesture refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
        });
    }
    // Fungsi untuk mengontrol animasi loading selesai
    private void refreshData() {
        // Contoh: Delay 2 detik untuk simulasi proses refresh
        new Handler().postDelayed(() -> {
            updateUserData();
            swipeRefreshLayout.setRefreshing(false);
        }, 2000);
    }



    private void setupScrollListener(ScrollView scrollView) {
        editNamaText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollView.scrollTo(0, editNamaText.getTop());
            }
        });

        emailText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollView.scrollTo(0, emailText.getTop());
            }
        });
    }

    private void choosePhoto() {
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_STORAGE);
            Toast.makeText(requireActivity(), "Permission needed", Toast.LENGTH_SHORT).show();
        } else {
            openGallery();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserData();
    }

    private void updateUserData() {
        UsersUtil usersUtil = new UsersUtil(requireActivity());
        String emailUser = usersUtil.getEmail();
        String namaUser = usersUtil.getUsername();
        String alamatUser = usersUtil.getAlamat();
        String notelpUser = usersUtil.getNoTelp();
        String gambarUser = usersUtil.getUserPhoto();

        Log.d("Profiles", "onResume - Email: " + emailUser + ", Name: " + namaUser);
        editNamaText.setText(namaUser);
        emailText.setText(emailUser);
        alamatText.setText(alamatUser);
        notelpText.setText(notelpUser);
        if (gambarUser != null && !gambarUser.isEmpty()) {
            Glide.with(requireContext()).load(Config.API_IMAGE + gambarUser).into(imgThumb);
        } else {
            Log.d("Profiles", "No image URL found in onResume.");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

//    private void uploadBase64(String imgBase64) {
//        UploadService uploadService = new UploadService();
//        UsersUtil util = new UsersUtil(requireActivity());
//        String idPenggunaS = util.getId();
//        Integer idPengguna = Integer.parseInt(idPenggunaS);
//
//        uploadService.uploadPhotoBase64("base64", imgBase64, idPengguna).enqueue(new Callback<BaseResponse>() {
//            @Override
//            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
//                progressDialog.dismiss();
//                if (response.body() != null) {
//                    fotobaru = response.body().getMessage();
//                    if (!Objects.equals(fotobaru, "Duplikat foto terdeteksi. Upload ditolak.")){
//                    Toast.makeText(requireActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                    updateProfiles(fotobaru);}
//                } else {
//                    Toast.makeText(requireActivity(), "Upload GAGAL", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BaseResponse> call, Throwable t) {
//                progressDialog.dismiss();
//                Toast.makeText(requireActivity(), "GAGAL 2", Toast.LENGTH_SHORT).show();
//                t.printStackTrace();
//            }
//        });
//    }
    private void uploadBase64(String base64Image) {
        UploadService uploadService = new UploadService();
        UsersUtil util = new UsersUtil(requireActivity());
        String idPenggunaS = util.getId();
        Integer idPengguna = Integer.parseInt(idPenggunaS);
        Log.e("ERRPRRRRRRRR", "uploadBase64: "+base64Image);
        uploadService.uploadPhotoBase64("base64", base64Image, idPengguna).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()) {
                    String gambar = String.valueOf(response.body().getMessage());
                    util.setUserPhoto(gambar);
                    Toast.makeText(requireContext(), "Gambar berhasil diunggah!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Gagal mengunggah gambar.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfiles(String fotobaru1) {
        // Ambil data dari input pengguna
        UsersUtil util = new UsersUtil(requireActivity());
        String idPengguna = util.getId();
        String namaUser = editNamaText.getText().toString().trim();  // Pastikan untuk trim() agar tidak ada spasi ekstra
        String alamatUser = alamatText.getText().toString().trim();
        String notelpUser = notelpText.getText().toString().trim();

        // Validasi nomor telepon
        if (notelpUser.length() < 10 || notelpUser.length() > 13) {
            progressDialog.dismiss();
            notelpText.setError("Nomor telepon harus antara 10 hingga 13 digit.");
            return; // Menghentikan eksekusi lebih lanjut jika validasi gagal
        }

        // Validasi jika nama atau alamat kosong
        if (namaUser.isEmpty()) {
            progressDialog.dismiss();
            editNamaText.setError("Nama tidak boleh kosong");
            return;
        }

        if (alamatUser.isEmpty()) {
            progressDialog.dismiss();
            alamatText.setError("Alamat tidak boleh kosong");
            return;
        }

        // Lakukan update data jika semua validasi berhasil
        Client.getInstance().updateprofiles("edit_user_info", idPengguna, namaUser, alamatUser, notelpUser)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        progressDialog.dismiss();
                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("true")) {
                            Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            UserModel model = response.body().getData();
                            util.setUsername(namaUser);
                            util.setAlamat(alamatUser);
                            util.setNoTelp(notelpUser);
                            util.setUserPhoto(fotobaru1);
                            updateUserData();
                            Log.d("Profiles", "Update success for user: " + namaUser);
                        } else {
                            Toast.makeText(requireActivity(), "Gagal mengupdate profil", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(requireActivity(), "GAGAL mengupdate profil", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            Log.e("imageUriiiiiiiiiiiiiiiiii", "onActivityResult: "+uri );
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                Log.e("selectedBitmap", "onActivityResult: "+selectedBitmap );
                Picasso.get().load(uri).into(imgThumb);
//                Glide.with(requireContext()).load(uri).circleCrop().into(imgThumb);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    private void uploadImage() {
        if (selectedBitmap != null) {
            String base64Image = convertToBase64(selectedBitmap);
            Log.e("base64Image", "uploadImage: "+base64Image );
            uploadBase64(base64Image);
        } else {
            Toast.makeText(requireContext(), "Pilih gambar terlebih dahulu!", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(requireActivity(), "Permission denied:please activated Storage permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
