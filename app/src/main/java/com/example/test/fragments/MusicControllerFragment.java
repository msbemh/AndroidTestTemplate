package com.example.test.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.databinding.ActivityCustomDialogBinding;
import com.example.test.databinding.FragmentMusicControllerBinding;
import com.example.test.models.Song;
import com.example.test.services.MusicService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MusicControllerFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = MusicControllerFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FragmentMusicControllerBinding mBinding;

    MusicService mService;
    boolean mBound = false;

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
        mBinding = FragmentMusicControllerBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.playButton.setOnClickListener(this);
        mBinding.prevButton.setOnClickListener(this);
        mBinding.nextButton.setOnClickListener(this);
    }

    public void updateMetadata(Song song){
        if(song == null){
            return;
        }

        String title = song.title;
        String artist = song.artist;
        long duration = song.duration;
        byte[] imageData = song.imageData;

        if(imageData != null){
            Glide.with(this).load(imageData).into(mBinding.albumImage);
        }else{
            Glide.with(this).load(R.mipmap.ic_launcher).into(mBinding.albumImage);
        }

        mBinding.titleText.setText(title);
        mBinding.artistText.setText(artist);
    }

    @Subscribe
    public void updateUI(Boolean isPlaying){
        // 선택된 노래가 있다면, Visible
        if(mService.mSong != null){
            getView().setVisibility(View.VISIBLE);
        }
        // 버튼 텍스트 변경
        mBinding.playButton.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);
        // Song 메타 데이터 변경
        updateMetadata(mService.mSong);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        // Bind to LocalService
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        getActivity().unbindService(connection);
        mBound = false;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            // UI Update
            updateUI(mService.isPlaying());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    /**
     * onClickListener 구현
     * 재생 버튼 클릭
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.play_button:
                mService.resumeMusic();
                break;
            case R.id.prev_button:
                mService.prevMusic();
                break;
            case R.id.next_button:
                mService.nextMusic();
                break;
        }
    }
}