package com.polije.sem3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.polije.sem3.model.Verification;
import com.polije.sem3.response.VerificationResponse;
import com.polije.sem3.retrofit.Client;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    private AppCompatImageButton btnBack;
    private Button btnSubmit;
    private EditText txtEmail;
    private String emaiUser;
    private ProgressDialog progressDialog;

    // model data
    private Verification verification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        // loading progress bar
        progressDialog = new ProgressDialog(ForgotPassword.this);
        progressDialog.setTitle("proses permintaan OTP...");
        progressDialog.setMessage("Harap Tunggu");
        progressDialog.setCancelable(false);

        btnBack = findViewById(R.id.backButton);
        btnSubmit = findViewById(R.id.btnSubmitOTP);
        txtEmail = findViewById(R.id.txtemails);
        emaiUser = "";

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, Login.class);
                startActivity(intent);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
//                startActivity(new Intent(ForgotPassword.this, OtpVerification.class));
                emaiUser = txtEmail.getText().toString();
                String tipe = "ForgotPass";

                Client.getInstance().sendmailotp(emaiUser).enqueue(new Callback<VerificationResponse>() {
                    @Override
                    public void onResponse(Call<VerificationResponse> call, Response<VerificationResponse> response) {
                        if (response.body() != null && response.body().getStatus().equalsIgnoreCase("true")) {
                            progressDialog.dismiss();

                            String otp = response.body().getData().getOtp();
                            String endMillis = response.body().getData().getEnd_millis();

                            Toast.makeText(ForgotPassword.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgotPassword.this, OtpVerification.class).putExtra(
                                    OtpVerification.EMAIL_USER, emaiUser
                            ).putExtra(
                                    OtpVerification.OTP_USER, otp
                            ).putExtra(
                                    OtpVerification.END_MILLIS, endMillis
                            ));
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ForgotPassword.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<VerificationResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPassword.this, "Request Timeout", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}