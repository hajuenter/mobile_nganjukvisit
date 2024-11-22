package com.polije.sem3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.otpview.OTPTextView;
import com.polije.sem3.retrofit.Client;

public class OtpVerification extends AppCompatActivity {
    // intent email
    public static String EMAIL_USER = "email";
    private String emailGet;
    // intent otp
    public static String OTP_USER = "1234";
    private String otpGet;
    // intent endmillis
    public static String END_MILLIS = "1";
    private String endmillisget;
    private long currentmillis;

    // button
    private Button btnsubmitOTP;

    // otpget
    private OTPTextView otpinterface;
    private String stringOTP;

    //countdown
    private TextView countdownText;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60000;
    private boolean timerRunning = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        btnsubmitOTP = findViewById(R.id.btnVerify);
        otpinterface = findViewById(R.id.votp_inp_otp);
        emailGet = getIntent().getStringExtra(EMAIL_USER);
        otpGet = getIntent().getStringExtra(OTP_USER);
        // Mengambil nilai endMillis sebagai Long
        long endMillis = getIntent().getLongExtra(OtpVerification.END_MILLIS, 0L); // Default 0L jika tidak ditemukan



        // Validasi data
        if (emailGet == null || otpGet == null) {
            Toast.makeText(this, "Data tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }



        countdownText = findViewById(R.id.timercount);

        startCountdownTimer();

        btnsubmitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringOTP = otpinterface.getOtp();

                try {
                    currentmillis = 60000;

                    if (stringOTP.equalsIgnoreCase(otpGet)) {
                        if (endMillis > currentmillis) {
                            Intent intent = new Intent(OtpVerification.this, PasswordBaru.class);
                            intent.putExtra(PasswordBaru.OTP_USER, stringOTP);
                            intent.putExtra(PasswordBaru.EMAIL_USER, emailGet);
                            startActivity(intent);
                            finish(); // Tutup aktivitas saat ini
                        } else {
                            Toast.makeText(OtpVerification.this, "Sesi OTP berakhir.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(OtpVerification.this, "OTP tidak cocok", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Log.e("OtpVerification", "Error parsing millis", e);
                    Toast.makeText(OtpVerification.this, "Terjadi kesalahan pada sesi OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

        private void startCountdownTimer() {
            countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateCountdownText();
                }

                @Override
                public void onFinish() {
                    countdownText.setText("OTP expired");
                    btnsubmitOTP.setEnabled(false);
                    // Handle what should happen when the countdown finishes
                }
            }.start();

            timerRunning = true;
        }

        private void updateCountdownText() {
            int minutes = (int) (timeLeftInMillis / 1000) / 60;
            int seconds = (int) (timeLeftInMillis / 1000) % 60;

            String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
            countdownText.setText("Expired: " + timeLeftFormatted);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
        }
}