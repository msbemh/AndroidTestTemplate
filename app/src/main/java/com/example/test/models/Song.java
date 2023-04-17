package com.example.test.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Song implements Serializable, Parcelable {
    public String audioId;
    public String title;
    public String artist;
    public byte[] imageData;
    public long duration;
    public Uri uri;

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Song(String audioId, String title, String artist, byte[] imageData, long duration, Uri uri) {
        this.audioId = audioId;
        this.title = title;
        this.artist = artist;
        this.imageData = imageData;
        this.duration = duration;
        this.uri = uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.audioId);
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeByteArray(this.imageData);
        dest.writeLong(this.duration);
        dest.writeParcelable(this.uri, flags);
    }

    public void readFromParcel(Parcel source) {
        this.audioId = source.readString();
        this.title = source.readString();
        this.artist = source.readString();
        this.imageData = source.createByteArray();
        this.duration = source.readLong();
        this.uri = source.readParcelable(Uri.class.getClassLoader());
    }

    protected Song(Parcel in) {
        this.audioId = in.readString();
        this.title = in.readString();
        this.artist = in.readString();
        this.imageData = in.createByteArray();
        this.duration = in.readLong();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
