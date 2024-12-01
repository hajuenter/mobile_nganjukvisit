package com.polije.sem3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.polije.sem3.model.UserModel;
import com.polije.sem3.response.UserResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1000; // Request code for Google Sign-In
    EditText username, password;
    Button btnLogin, btnSignup, btnGoogle;
    TextView lupaPass;
    boolean passwordVisible;

    private GoogleSignInClient mGoogleSignInClient;
    private UsersUtil usersUtil;
    private ProgressDialog progressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Progress bar
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Proses login...");
        progressDialog.setMessage("Harap tunggu");
        progressDialog.setCancelable(false);

        username = findViewById(R.id.txtusername);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Tidak ada perubahan yang perlu dilakukan di sini
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                String input = charSequence.toString();
                String regex = "[a-zA-Z0-9._@+-]*";
                if (!input.matches(regex)) {
                    username.setText(input.replaceAll("[^a-zA-Z0-9._@+-]", ""));
                    int cursorPosition = input.length();
                    if (cursorPosition <= username.getText().length()) {
                        username.setSelection(cursorPosition); // Mengatur seleksi pada posisi yang valid
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Tidak ada perubahan yang perlu dilakukan di sini
            }
        });
        password = findViewById(R.id.txtpassword);
        lupaPass = findViewById(R.id.forgotPass);
        btnLogin = findViewById(R.id.loginButton);
        btnSignup = findViewById(R.id.signupButton);
        btnGoogle = findViewById(R.id.loginButtonWithGoogle);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("Token", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Mendapatkan token untuk FCM
                    String token = task.getResult();
                    Log.d("Token", "FCM Token: " + token);
                });

        // Konfigurasi Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("139403144427-id5vkrl8e0dihv2skdr2602v5h2lbqfu.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Login manual
        btnLogin.setOnClickListener(v -> {
            progressDialog.show();
            String usernameKey = username.getText().toString();
            String passwordKey = password.getText().toString();

            // Mengirim login request
            Client.getInstance().login("login", usernameKey, passwordKey).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    progressDialog.dismiss();
                    if (response.body() != null && response.body().getStatus().equalsIgnoreCase("true")) {
                        UserModel userModel = response.body().getData();
                        Intent intent = new Intent(Login.this, Dashboard.class);
                        Toast.makeText(Login.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        // Simpan detail pengguna di lokal
                        usersUtil = new UsersUtil(Login.this, userModel);
                        String noTelp = usersUtil.getNoTelp();
                        if (noTelp == null || noTelp.isEmpty()) {
                            // Jika nomor telepon kosong, arahkan ke dashboard dan tampilkan fragmen profil
                            Toast.makeText(Login.this, "Harap lengkapi informasi profil Anda", Toast.LENGTH_SHORT).show();
                            intent.putExtra("fragmentToLoad", "Profiles");  // Menambahkan flag untuk menampilkan fragmen profil
                        } else {
                            // Jika nomor telepon sudah ada, lanjutkan ke dashboard biasa
                            Toast.makeText(Login.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        // Mulai aktivitas selanjutnya
                        startActivity(intent);
                    } else {
                        Toast.makeText(Login.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Timeout", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Login dengan Google
        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        lupaPass.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgotPassword.class);
            startActivity(intent);
        });

        password.setOnTouchListener((v, event) -> {
            final int Right = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= password.getRight() - password.getCompoundDrawables()[Right].getBounds().width()) {
                    int selection = password.getSelectionEnd();
                    if (passwordVisible) {
                        // Set drawable image
                        password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eyeicon, 0);
                        // Hide password
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    } else {
                        // Set drawable image
                        password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eyeicon_close, 0);
                        // Show password
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    password.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String email = account.getEmail();

                    // Kirim token ke server untuk login
                    Client.getInstance().logingoogle("google",email).enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                Intent intent = new Intent(Login.this, Dashboard.class);

                                // Simpan detail pengguna di lokal
                                usersUtil = new UsersUtil(Login.this, response.body().getData());
                                mGoogleSignInClient.signOut();
                                Toast.makeText(Login.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            }if(response.body() != null && response.body().getStatus().equalsIgnoreCase("notfound")){
                                // Tampilkan dialog konfirmasi
                                new AlertDialog.Builder(Login.this)
                                        .setTitle("Konfirmasi")
                                        .setMessage("Akun belum terdaftar, mau daftar dulu?")
                                        .setPositiveButton("Daftar", (dialog, which) -> {
                                            mGoogleSignInClient.signOut();
                                            // Aksi untuk tombol Daftar
                                            Intent intent1 = new Intent(Login.this, Register.class);
                                            intent1.putExtra("emailuser", email);
                                            startActivity(intent1);
                                        })
                                        .setNegativeButton("Batal", (dialog, which) -> {
                                            // Aksi untuk tombol Batal
                                            dialog.dismiss();
                                            mGoogleSignInClient.signOut();
                                        })
                                        .show();
                            }
                            else {
                                mGoogleSignInClient.signOut();
                                Toast.makeText(Login.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            mGoogleSignInClient.signOut();
                            Toast.makeText(Login.this, "Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (ApiException e) {
                e.printStackTrace();
                mGoogleSignInClient.signOut();
                Toast.makeText(Login.this, "Login dengan Google gagal", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
