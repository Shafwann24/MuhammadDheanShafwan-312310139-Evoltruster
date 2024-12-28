package com.example.evoltruster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    // Deklarasi komponen
    private EditText etUsername, etPassword;
    private Button btnLogin;

    // Username dan password valid
    private final String validUsername = "Dynamysx";
    private final String validPassword = "08072004";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi komponen
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Tombol Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.equals(validUsername) && password.equals(validPassword)) {
                    // Login berhasil, pindah ke MainActivity
                    Intent intent = new Intent(login.this, ListMusicActivity.class);
                    startActivity(intent);
                    finish(); // Tutup halaman login agar tidak bisa kembali
                } else {
                    // Login gagal, tampilkan pesan kesalahan
                    Toast.makeText(login.this, "Username atau Password salah!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
