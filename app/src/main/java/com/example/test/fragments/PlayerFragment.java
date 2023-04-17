package com.example.test.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.databinding.FragmentPlayerBinding;
import com.example.test.services.MusicService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class PlayerFragment extends Fragment {

    private MusicService mService;
    private boolean mBound = false;
    private FragmentPlayerBinding mBinding;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(String param1, String param2) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPlayerBinding.inflate(inflater);
        return mBinding.getRoot();
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

    @Subscribe
    public void updateUI(Boolean isPlaying){
        if(isPlaying){
            byte[] imageData = mService.mSong.imageData;
            if(imageData != null){
                Glide.with(this).load(imageData).into(mBinding.imageView);
            }else{
                Glide.with(this).load(R.mipmap.ic_launcher).into(mBinding.imageView);
            }
        }
    }
}