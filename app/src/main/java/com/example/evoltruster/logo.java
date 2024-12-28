package com.example.evoltruster;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class logo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        // Navigasi otomatis ke halaman login setelah 3 detik
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(logo.this, login.class);
            startActivity(intent);
            finish(); // Tutup halaman logo agar tidak bisa kembali
        }, 3000); // Waktu tunggu 3 detik
    }
}
