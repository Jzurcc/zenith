package com.cc17.zenith;

import android.net.Uri;

public class Document {
    private int thumbnail;
    private String title;
    private String date;
    private Uri imageUri;

    public Document(int thumbnail, String title, String date) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.date = date;
        this.imageUri = null;
    }

    public Document(Uri imageUri, String title, String date) {
        this.thumbnail = 0; // Indicates it's not a resource
        this.title = title;
        this.date = date;
        this.imageUri = imageUri;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
