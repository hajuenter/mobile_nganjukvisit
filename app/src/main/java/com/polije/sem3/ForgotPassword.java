package com.polije.sem3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.polije.sem3.model.Verification;
import com.polije.sem3.response.VerificationResponse;
import com.polije.sem3.retrofit.Client;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    private AppCompatImageButton btnBack;
    private Button btnSubmit;
    private EditText txtEmail;
    private String emailUser;
    private ProgressDialog progressDialog;

    // model data
    private Verification verification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        // Initialize Progress Dialog
        progressDialog = new ProgressDialog(ForgotPassword.this);
        progressDialog.setTitle("Proses Permintaan OTP...");
        progressDialog.setMessage("Harap Tunggu");
        progressDialog.setCancelable(false);

        // Bind views
        btnBack = findViewById(R.id.backButton);
        btnSubmit = findViewById(R.id.btnSubmitOTP);
        txtEmail = findViewById(R.id.txtemails);
        emailUser = "";

        // Back Button click listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, Login.class);
                startActivity(intent);
            }
        });

        // Submit Button click listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailUser = txtEmail.getText().toString().trim();

                // Validate email input
                if (TextUtils.isEmpty(emailUser)) {
                    Toast.makeText(ForgotPassword.this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show progress dialog
                progressDialog.show();

                // Send OTP request to server
                Client.getInstance().sendmailotp(emailUser).enqueue(new Callback<VerificationResponse>() {
                    @Override
                    public void onResponse(Call<VerificationResponse> call, Response<VerificationResponse> response) {
                        progressDialog.dismiss();
                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("true")) {
                            String otp = response.body().getData().getOtp();
                            // Misalnya, Anda memiliki startMillis dalam bentuk long
                            long startMillis = 1677000000000L;  // contoh nilai startMillis dalam milidetik

// Tambahkan 60000 milidetik (1 menit)
                            long endMillis = startMillis + 600000;

// Konversi endMillis menjadi String jika diperlukan
                            String endMillisString = String.valueOf(endMillis);

                            // Pastikan Toast ditampilkan di thread utama
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ForgotPassword.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Pastikan navigasi ke halaman selanjutnya dilakukan dengan benar
                            Intent intent = new Intent(ForgotPassword.this, OtpVerification.class);
                            intent.putExtra(OtpVerification.EMAIL_USER, emailUser);
                            intent.putExtra(OtpVerification.OTP_USER, otp);
                            intent.putExtra(OtpVerification.END_MILLIS, endMillis);
                            startActivity(intent);
                        } else {
                            // Tampilkan pesan error jika status bukan "true"
                            Toast.makeText(ForgotPassword.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<VerificationResponse> call, Throwable t) {
                        progressDialog.dismiss();

                        // Log error untuk debugging
                        Log.e("ForgotPassword", "Error: " + t.getMessage(), t);

                        runOnUiThread(() -> {
                            // Berikan pesan error yang lebih informatif
                            if (t instanceof SocketTimeoutException) {
                                Toast.makeText(ForgotPassword.this, "Koneksi timeout. Periksa koneksi internet Anda.", Toast.LENGTH_SHORT).show();
                            } else if (t instanceof UnknownHostException) {
                                Toast.makeText(ForgotPassword.this, "Tidak dapat terhubung ke server. Periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ForgotPassword.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                });
            }
        });
    }
}
