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

import com.polije.sem3.apigoogle.GoogleUsers;
import com.polije.sem3.model.UserModel;
import com.polije.sem3.response.UserResponse;
import com.polije.sem3.retrofit.Client;
import com.polije.sem3.util.UsersUtil;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    EditText username, password;
    Button btnLogin, btnSignup, btnGoogle;
    TextView lupaPass;
    boolean passwordVisible;

    private GoogleUsers googleUsers;

    private UsersUtil usersUtil;

    private ProgressDialog progressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // progress bar
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("proses login...");
        progressDialog.setMessage("Harap Tunggu");
        progressDialog.setCancelable(false);

        googleUsers = new GoogleUsers(this);

        username = findViewById(R.id.txtusername);
        password = findViewById(R.id.txtpassword);
        lupaPass = findViewById(R.id.forgotPass);
        btnLogin = findViewById(R.id.loginButton);
        btnSignup = findViewById(R.id.signupButton);
        btnGoogle = findViewById(R.id.loginButtonWithGoogle);

        btnLogin.setOnClickListener(v -> {
            progressDialog.show();
            String usernameKey = username.getText().toString();
            String passwordKey = password.getText().toString();

            // Mengirim login request
            Client.getInstance().login("login",usernameKey, passwordKey).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    progressDialog.dismiss();
                    if (response.body() != null && response.body().getStatus().equalsIgnoreCase("true")) {
//                        // Generate a random token
//                        String token = UUID.randomUUID().toString();
//
//                        // Send the token to the server
//                        addTokenToServer(usernameKey, token);
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
                        // set drawable image
                        password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eyeicon, 0);
                        // hide password
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    } else {
                        // set drawable image
                        password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eyeicon_close, 0);
                        // show password
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

        btnGoogle.setOnClickListener(v -> {
            googleUsers.resetLastSignIn();
            startActivityForResult(googleUsers.getIntent(), GoogleUsers.REQUEST_CODE);
        });
    }

    private void addTokenToServer(String email, String token) {
        Client.getInstance().addToken(email, token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                    Log.i("Token Update", response.body().getMessage());
                } else {
                    Log.e("Token Update", "Failed to update token: " + response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("Token Update", "Error: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleUsers.onActivityResult(requestCode, resultCode, data);

        if (googleUsers.isAccountSelected()) {
            // Send Google user login request
            String email = googleUsers.getUserData().getEmail();
            String token = UUID.randomUUID().toString(); // Generate a new token for Google login

            Client.getInstance().logingoogle(email, token).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.body() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                        Intent intent = new Intent(Login.this, Dashboard.class);

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
        } else {
            Toast.makeText(Login.this, "NO DATA", Toast.LENGTH_SHORT).show();
        }
    }
}
