package com.polije.sem3;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.polije.sem3.response.UserResponse;
import com.polije.sem3.retrofit.Client;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordBaru extends AppCompatActivity {
    public static String EMAIL_USER = "email";
    private String getEmailUser;
    public static String OTP_USER = "1234";
    private String otpGet;

    private EditText password, password2;
    private String newpasswordvalue, passwordvalueconfirm;
    boolean passwordVisible;
    private Button btnSubmit;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_baru);

        // get email from intent
        getEmailUser = getIntent().getStringExtra(EMAIL_USER);


        otpGet = getIntent().getStringExtra(OTP_USER);

        // get passowrd from activity
        newpasswordvalue = "newpassword";
        passwordvalueconfirm = "passwordconfirm";

        password = (EditText) findViewById(R.id.txtpassword);
        password2 = (EditText) findViewById(R.id.txtpasswordconfirm);
        btnSubmit = findViewById(R.id.btnSubmitNewpass);

//        Toast.makeText(this, "email -> " + getEmailUser, Toast.LENGTH_SHORT).show();
//
//        Toast.makeText(this, "otp -> " + otpGet, Toast.LENGTH_SHORT).show();

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

        password2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right=2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=password2.getRight()-password2.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = password2.getSelectionEnd();
                        if(passwordVisible){
                            // set drawable image
                            password2.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eyeicon, 0);
                            // hide password
                            password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }else{
                            // set drawable image
                            password2.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eyeicon_close, 0);
                            // show password
                            password2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        password2.setSelection(selection);
                        return true;
                    }
                }

                return false;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newpasswordvalue = password.getText().toString();
                passwordvalueconfirm = password2.getText().toString();

                if (newpasswordvalue.equalsIgnoreCase(passwordvalueconfirm)) {

                    Client.getInstance().lupapass("forgot_password",getEmailUser, newpasswordvalue).enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (response.body() != null && response.body().getStatus().equalsIgnoreCase("true")) {
                                Toast.makeText(PasswordBaru.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PasswordBaru.this, Login.class));
                            } else {
                                Toast.makeText(PasswordBaru.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            Toast.makeText(PasswordBaru.this, "Request Timeout", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });

                } else {
                    Toast.makeText(PasswordBaru.this, "konfirmasi password salah !", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}