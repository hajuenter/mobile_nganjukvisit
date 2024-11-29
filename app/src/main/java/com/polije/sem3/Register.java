package com.polije.sem3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

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

import com.polije.sem3.response.UserResponse;
import com.polije.sem3.retrofit.Client;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    EditText alamat, email, password, fullname;
    boolean passwordVisible;
    private AppCompatImageButton btnBack;
    Button btnSubmit;
    private ProgressDialog progressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        String emailuser = getIntent().getStringExtra("emailuser");
        // loading progress bar
        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setTitle("proses register...");
        progressDialog.setMessage("Harap Tunggu");
        progressDialog.setCancelable(false);

        alamat = (EditText) findViewById(R.id.txtalamat);
        password = (EditText) findViewById(R.id.txtpassword);
        email = (EditText) findViewById(R.id.txtemails);
        fullname = (EditText) findViewById(R.id.txtfullname);
        btnBack = findViewById(R.id.backButton);
        btnSubmit = findViewById(R.id.signupButton);
        if(emailuser != null){
            email.setText(emailuser);
        }else{
            email.setText("");
        }

        btnSubmit.setOnClickListener(v -> {
            progressDialog.show();
            String alamatkey = alamat.getText().toString();
            String fullnameKey = fullname.getText().toString();
            String emailKey = email.getText().toString();
            String passwordKey = password.getText().toString();

            Client.getInstance().register("register",alamatkey, emailKey, fullnameKey, passwordKey).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.body() != null && response.body().getStatus().equalsIgnoreCase("true")){
                        progressDialog.dismiss();
                        Toast.makeText(Register.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent4 = new Intent(Register.this, Login.class);
                        startActivity(intent4);
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(Register.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(Register.this, "Request Timeout", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                    Log.d("Error Regist", t.getMessage());
                }
            });
        });

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right=2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=password.getRight()-password.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = password.getSelectionEnd();
                        if(passwordVisible){
                            // set drawable image
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eyeicon, 0);
                            // hide password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }else{
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
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
    }
}