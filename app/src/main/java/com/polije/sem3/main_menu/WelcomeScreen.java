package com.polije.sem3.main_menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.polije.sem3.R;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        Button tombolPindah = findViewById(R.id.btnStart);
        tombolPindah.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(WelcomeScreen.this, Login.class);
                startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}