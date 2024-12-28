package com.example.evoltruster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ListMusicActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 99;

    private ArrayList<Song> songArrayList;
    private ListView lvSong;
    private SongsAdapter songsAdapter;
    private Button btnProfile, btnLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_music);

        lvSong = findViewById(R.id.lvsongs);
        btnProfile = findViewById(R.id.btnProfile);
        btnLibrary = findViewById(R.id.btnLibrary);

        songArrayList = new ArrayList<>();
        songsAdapter = new SongsAdapter(this, songArrayList);
        lvSong.setAdapter(songsAdapter);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            return;
        } else {
            getSongs();
        }

        lvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Song song = songArrayList.get(position);
                Intent openMusicPlayer = new Intent(ListMusicActivity.this, MusicPlayerActivity.class);
                openMusicPlayer.putExtra("song", song);
                startActivity(openMusicPlayer);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListMusicActivity.this, profile.class);
                startActivity(intent);
            }
        });

        btnLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListMusicActivity.this, ListMusicActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongs();
            }
        }
    }

    private void getSongs() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null) {
            try {
                int indexTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int indexArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int indexData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

                if (indexTitle == -1 || indexArtist == -1 || indexData == -1) {
                    return;
                }

                while (songCursor.moveToNext()) {
                    String title = songCursor.getString(indexTitle);
                    String artist = songCursor.getString(indexArtist);
                    String path = songCursor.getString(indexData);
                    songArrayList.add(new Song(title, artist, path));
                }
            } finally {
                songCursor.close();
            }
        }

        songsAdapter.notifyDataSetChanged();
    }
}