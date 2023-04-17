package com.example.test.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class SongListMessage implements Parcelable, Serializable {
    public List<Song> songList;

    public SongListMessage(List<Song> songList) {
        this.songList = songList;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.songList);
    }

    public void readFromParcel(Parcel source) {
        this.songList = source.createTypedArrayList(Song.CREATOR);
    }

    protected SongListMessage(Parcel in) {
        this.songList = in.createTypedArrayList(Song.CREATOR);
    }

    public static final Parcelable.Creator<SongListMessage> CREATOR = new Parcelable.Creator<SongListMessage>() {
        @Override
        public SongListMessage createFromParcel(Parcel source) {
            return new SongListMessage(source);
        }

        @Override
        public SongListMessage[] newArray(int size) {
            return new SongListMessage[size];
        }
    };
}
