package com.cc17.zenith;

public class Document {
    private int thumbnail;
    private String title;
    private String date;

    public Document(int thumbnail, String title, String date) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.date = date;
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
}
