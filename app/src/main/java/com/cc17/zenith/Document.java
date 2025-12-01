package com.cc17.zenith;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Document implements Parcelable {
    private int thumbnail;
    private String title;
    private String date;
    private Uri imageUri;

    // constructor for dummy data
    public Document(int thumbnail, String title, String date) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.date = date;
    }

    // constructor for real images
    public Document(Uri imageUri, String title, String date) {
        this.imageUri = imageUri;
        this.title = title;
        this.date = date;
        this.thumbnail = 0;
    }

    protected Document(Parcel in) {
        thumbnail = in.readInt();
        title = in.readString();
        date = in.readString();
        imageUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(thumbnail);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeParcelable(imageUri, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getThumbnail() { return thumbnail; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public Uri getImageUri() { return imageUri; }
}