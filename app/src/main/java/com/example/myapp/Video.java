package com.example.myapp;

public class Video {
    private final String title;    private final String videoId;

    public Video(String title, String videoId) {
        this.title = title;
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoId() {
        return videoId;
    }
}

