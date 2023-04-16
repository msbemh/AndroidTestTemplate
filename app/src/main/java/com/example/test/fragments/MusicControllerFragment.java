package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.activities.MusicPlayerActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MusicControllerFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ImageView mAlbumImageView;
    private TextView mTitleTextView;
    private TextView mArtistTextView;
    private Button mPlayButton;

    public MusicControllerFragment() {
        // Required empty public constructor
    }

    public static MusicControllerFragment newInstance(String param1, String param2) {
        MusicControllerFragment fragment = new MusicControllerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAlbumImageView = (ImageView) view.findViewById(R.id.album_image);
        mTitleTextView = (TextView) view.findViewById(R.id.title_text);
        mArtistTextView = (TextView) view.findViewById(R.id.artist_text);
        mPlayButton = (Button) view.findViewById((R.id.play_button));
        mPlayButton.setOnClickListener(this);
    }

    @Subscribe
    public void updateUI(SongFragment.MessageEvent messageEvent){
        String title = messageEvent.title;
        String artist = messageEvent.artist;
        long duration = messageEvent.duration;
        byte[] imageData = messageEvent.imageData;

        if(imageData != null){
            Glide.with(this).load(imageData).into(mAlbumImageView);
        }

        mTitleTextView.setText(title);
        mArtistTextView.setText(artist);
    }

    @Subscribe
    public void updatePlayButton(Boolean isPlaying){
        mPlayButton.setText(isPlaying ? "중지" : "재생");
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        /**
         * {@link com.example.test.activities.MusicPlayerActivity#clickPlayButton(View)}
         */
        EventBus.getDefault().post(view);
    }
}