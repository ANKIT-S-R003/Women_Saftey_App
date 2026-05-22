package com.example.myapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Make sure this import is present
import com.bumptech.glide.Glide;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final List<Video> videoList;
    private final OnVideoClickListener listener; // Listener for handling clicks

    // Interface to send click events back to the activity
    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }

    // The constructor requires the listener
    public VideoAdapter(List<Video> videoList, OnVideoClickListener listener) {
        this.videoList = videoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This inflates the layout for each row
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video currentVideo = videoList.get(position);

        // 1. Set the video title
        holder.tvVideoTitle.setText(currentVideo.getTitle());

        // 2. Construct the thumbnail URL
        String thumbnailUrl = "https://img.youtube.com/vi/" + currentVideo.getVideoId().trim() + "/0.jpg";

        // 3. Use Glide to load the thumbnail image from the URL
        Glide.with(holder.itemView.getContext())
                .load(thumbnailUrl)
                .placeholder(R.drawable.ic_video) // Shows a default icon while loading
                .error(R.drawable.ic_warning)     // Shows an error icon if the thumbnail fails to load
                .into(holder.imgThumbnail);

        // 4. Set the click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // This tells SelfDefenceActivity which video was clicked
                listener.onVideoClick(currentVideo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    // The ViewHolder class links the Java code to the views in item_video.xml
    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvVideoTitle;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            // These IDs must match the IDs in your item_video.xml file
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            tvVideoTitle = itemView.findViewById(R.id.tvVideoTitle);
        }
    }
}
