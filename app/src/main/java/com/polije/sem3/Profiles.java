package com.polije.sem3;

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

import android.provider.MediaStore;
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
import com.polije.sem3.model.UserModel;
import com.polije.sem3.network.BaseResponse;
import com.polije.sem3.network.Config;
import com.polije.sem3.network.UploadService;
import com.polije.sem3.response.UserResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.UsersUtil;
import com.polije.sem3.utility.ImageUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profiles extends Fragment {

    private static final int PICK_IMAGE = 1;
    private static final int PERMISSION_REQUEST_STORAGE = 2;

    private Uri uri;
    private ImageView imgThumb;
    private EditText editNamaText, emailText, alamatText, notelpText;
    private ProgressDialog progressDialog;

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

        imgThumb = view.findViewById(R.id.img_thumb);
        Button btnLogout = view.findViewById(R.id.btn_logout);
        TextView btnChoose = view.findViewById(R.id.choosePictures);
        Button btnUpload2 = view.findViewById(R.id.btn_upload_2);
        ScrollView scrollView = view.findViewById(R.id.scrollView);
        editNamaText = view.findViewById(R.id.textnama);
        emailText = view.findViewById(R.id.edt_emailaddr);
        alamatText = view.findViewById(R.id.edt_alamat);
        notelpText = view.findViewById(R.id.edt_notelp);

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

        btnChoose.setOnClickListener(v -> choosePhoto());

        btnUpload2.setOnClickListener(v -> {
            // Run loading bar
            progressDialog.show();

            if (uri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                    String encoded = ImageUtils.bitmapToBase64String(bitmap, 100);
                    uploadBase64(encoded);
                } catch (IOException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(requireActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
                }
            } else {
                updateProfiles();
                Log.d("Profiles", "Just Update String Information");
            }
        });

        btnLogout.setOnClickListener(v -> {
            util.signOut();
            if (!util.isSignIn()) {
                Toast.makeText(requireActivity(), "Logout Sukses", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireActivity(), WelcomeScreen.class));
            }
        });
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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }

    private void uploadBase64(String imgBase64) {
        UploadService uploadService = new UploadService();
        UsersUtil util = new UsersUtil(requireActivity());
        String idPengguna = util.getId();

        uploadService.uploadPhotoBase64("base64", imgBase64, idPengguna).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    Toast.makeText(requireActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    updateProfiles();
                } else {
                    Toast.makeText(requireActivity(), "Upload GAGAL", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), "GAGAL 2", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void updateProfiles() {
        UsersUtil util = new UsersUtil(requireActivity());
        String idPengguna = util.getId();
        String namaUser = editNamaText.getText().toString();
        String emailUser = emailText.getText().toString();
        String alamatUser = alamatText.getText().toString();
        String notelpUser = notelpText.getText().toString();

        Client.getInstance().updateprofiles(idPengguna, namaUser, emailUser, alamatUser, notelpUser).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                progressDialog.dismiss();
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    UserModel model = response.body().getData();
                    util.setFullName(namaUser);
                    util.setAlamat(alamatUser);
                    util.setNoTelp(notelpUser);
                    Log.d("Profiles", "Update success for user: " + model.toString());
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
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            if (uri != null) {
                try {
                    imgThumb.setImageURI(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
