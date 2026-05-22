package com.example.myapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapp.R;
import com.example.myapp.Video;
import com.example.myapp.VideoAdapter;
import java.util.ArrayList;
import java.util.List;

public class SelfDefenceActivity extends AppCompatActivity implements VideoAdapter.OnVideoClickListener {

    private RecyclerView recyclerView;
    private List<Video> videoList;
    private VideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_defence);

        Toolbar toolbar = findViewById(R.id.toolbarVideos);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Self Defence Videos");
        }

        recyclerView = findViewById(R.id.recyclerViewVideos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load the video list you provided.
        loadVideos();

        adapter = new VideoAdapter(videoList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onVideoClick(Video video) {
        // This code is proven to work correctly.
        String cleanVideoId = video.getVideoId().trim();
        Uri videoUri = Uri.parse("https://www.youtube.com/watch?v=" + cleanVideoId);
        Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
        intent.setPackage("com.google.android.youtube");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, videoUri);
            startActivity(webIntent);
        }
    }

    // ▼▼▼ THIS IS THE FINAL METHOD WITH YOUR HAND-PICKED VIDEOS ▼▼▼
    private void loadVideos() {
        videoList = new ArrayList<>();

        // It includes the first one that worked, plus the new ones you just gave me.
        videoList.add(new Video("5 Self-Defense Moves Every Woman Should Know", "KVpxP3ZZtAc")); // The first working link
        videoList.add(new Video("10 Self-Defence Gadgets", "M4_8PoRQP8w"));
        videoList.add(new Video("Street Fight Self Defense", "T7aNSRoDCmg"));
        videoList.add(new Video("Self Defense for Weaker People", "pndPbpHLpos"));
        videoList.add(new Video("Wing Chun Self Defense", "jq7yqox1D5w"));
        videoList.add(new Video("Muay Thai vs Boxing", "Gx3_x6RH1J4"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
