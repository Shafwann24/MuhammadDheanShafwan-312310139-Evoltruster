package com.example.evoltruster;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class MusicPlayerActivity extends AppCompatActivity {

    private TextView tvTitle, tvArtist, tvPath, tvTime, tvDuration;
    private SeekBar seekBarTime, seekBarVolume;
    private ImageView btnPlay;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable updateSeekBarTime;

    private static final int REQUEST_PERMISSION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menghubungkan TextView, ImageView, dan SeekBar di layout dengan variabel
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvPath = findViewById(R.id.tvPath);
        tvTime = findViewById(R.id.tvTime);
        tvDuration = findViewById(R.id.tvDuration);
        seekBarTime = findViewById(R.id.seekBarTime);
        seekBarVolume = findViewById(R.id.seekBarVolume);
        btnPlay = findViewById(R.id.btnPlay);

        // Handler untuk memperbarui waktu pada SeekBar Time
        handler = new Handler();

        // Menangkap objek Song yang dikirim dari Intent
        Song song = getIntent().getParcelableExtra("song");

        if (song != null) {
            // Menampilkan data lagu di TextView
            tvTitle.setText("Title: " + song.getTitle());
            tvArtist.setText("Artist: " + song.getArtist());
            tvPath.setText("Path: " + song.getPath());

            // Cek apakah file lagu ada
            File file = new File(song.getPath());
            if (!file.exists()) {
                Toast.makeText(this, "File not found!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cek izin akses penyimpanan
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            } else {
                setupMediaPlayer(song);
            }
        }

        // Tombol Play/Pause
        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    btnPlay.setBackgroundResource(R.drawable.buttonpause); // Ganti ikon play dengan pause
                    handler.post(updateSeekBarTime); // Mulai pembaruan waktu
                } else {
                    mediaPlayer.pause();
                    btnPlay.setBackgroundResource(R.drawable.buttonplay); // Ganti ikon pause dengan play
                }
            }
        });

        // Atur volume menggunakan SeekBar Volume
        setupVolumeControl();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Song song = getIntent().getParcelableExtra("song");
                if (song != null) {
                    setupMediaPlayer(song);
                }
            } else {
                Toast.makeText(this, "Permission denied to read storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupMediaPlayer(Song song) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(song.getPath());
            mediaPlayer.prepare();

            // Atur durasi dan SeekBar
            seekBarTime.setMax(mediaPlayer.getDuration());
            tvDuration.setText(millisecondsToTime(mediaPlayer.getDuration()));

            // Listener untuk ketika lagu selesai diputar
            mediaPlayer.setOnCompletionListener(mp -> {
                btnPlay.setBackgroundResource(R.drawable.buttonplay); // Ganti ikon ke play
                handler.removeCallbacks(updateSeekBarTime); // Hentikan pembaruan waktu
            });

            // Atur pembaruan waktu real-time
            updateSeekBarTime = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        seekBarTime.setProgress(mediaPlayer.getCurrentPosition());
                        tvTime.setText(millisecondsToTime(mediaPlayer.getCurrentPosition()));
                        handler.postDelayed(this, 100); // Update setiap 100ms
                    }
                }
            };

            // Listener untuk SeekBar Time
            seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && mediaPlayer != null) {
                        mediaPlayer.seekTo(progress);
                        tvTime.setText(millisecondsToTime(progress)); // Update waktu saat di-drag
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // Tidak diperlukan aksi khusus
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // Tidak diperlukan aksi khusus
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing music", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupVolumeControl() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Atur volume maksimum dan volume saat ini
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        seekBarVolume.setMax(maxVolume);
        seekBarVolume.setProgress(currentVolume);

        // Listener untuk SeekBar Volume
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Tidak diperlukan aksi khusus
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Tidak diperlukan aksi khusus
            }
        });
    }

    private String millisecondsToTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBarTime); // Hentikan pembaruan waktu
    }
}
