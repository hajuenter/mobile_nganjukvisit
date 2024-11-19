package com.polije.sem3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
        password = findViewById(R.id.txtpassword);
        lupaPass = findViewById(R.id.forgotPass);
        btnLogin = findViewById(R.id.loginButton);
        btnSignup = findViewById(R.id.signupButton);
        btnGoogle = findViewById(R.id.loginButtonWithGoogle);

        // Konfigurasi Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1002502978577-q9trtjpv5c27cl3u8do9jel9sak7v7kp.apps.googleusercontent.com") // Ganti dengan Client ID dari Google Cloud Console
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
                    String token = UUID.randomUUID().toString(); // Generate a new token for Google login

                    // Kirim token ke server untuk login
                    Client.getInstance().logingoogle(email, token).enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                Intent intent = new Intent(Login.this, Dashboard.class);

                                // Simpan detail pengguna di lokal
                                usersUtil = new UsersUtil(Login.this, response.body().getData());

                                Toast.makeText(Login.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                            } else {
                                Toast.makeText(Login.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            Toast.makeText(Login.this, "Timeout", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(Login.this, "Login dengan Google gagal", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
