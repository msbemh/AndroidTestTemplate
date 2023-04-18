package com.example.test.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.activities.ServiceTestActivity;
import com.example.test.databinding.FragmentPlayerBinding;
import com.example.test.models.Song;
import com.example.test.services.MusicService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerFragment extends Fragment {
    private static final String TAG = PlayerFragment.class.getSimpleName();

    private MusicService mService;
    private boolean mBound = false;
    private FragmentPlayerBinding mBinding;
    private TimerTask timerTask;
    private Handler mHandler;

    private final int MESSAGE_MUSIC_ING = 1;
    private final int MESSAGE_MUSIC_END = 2;

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

        /**
         * Worker Thread 에서 보낸 Message 로
         * UI 작업
         */
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if(msg.what == MESSAGE_MUSIC_ING){
                    int currentPosition = mService.getMediaPlayer().getCurrentPosition();
                    mBinding.seekBar.setProgress(currentPosition);
                    mBinding.durationStartText.setText(getTimeFormat(currentPosition));
                }else if(msg.what == MESSAGE_MUSIC_END){
                    mService.end();
                    mBinding.seekBar.setProgress(0);
                    mBinding.durationStartText.setText(getTimeFormat(0));
                }

            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPlayerBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /**
         * Seek Bar Change 되면 음악 해당 위치 부터 재생
         */
        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    MediaPlayer mediaPlayer = mService.getMediaPlayer();
                    if(mediaPlayer != null) mediaPlayer.seekTo(progress);

                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
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

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to MusicService, cast the IBinder and get MusicService instance
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
        Song song = mService.mSong;
        if(mService.getMediaPlayer() != null && mService.mSong != null){
            // 이미지 보여주기
            byte[] imageData = song.imageData;
            if(imageData != null){
                Glide.with(this).load(imageData).into(mBinding.imageView);
            }else{
                Glide.with(this).load(R.drawable.default_music_background).into(mBinding.imageView);
            }

            // int duration = Long.valueOf(song.duration).intValue();
            int duration = mService.getMediaPlayer().getDuration();
            // 재생시간 보여주기
            mBinding.durationEndText.setText(getTimeFormat(duration));
            Log.d(TAG, "duration:" + duration);

            // SeekBar 세팅
            mBinding.seekBar.setMax(duration);

            // 정지상태였고, SeekBar Position이 있었다면 UI 적용
            if(!mService.isPlaying() && mService.mPosition != 0){
                mBinding.seekBar.setProgress(mService.mPosition);
                mBinding.durationStartText.setText(getTimeFormat(mService.mPosition));
            }

            // SeekBar 이동 Timer
            if(mService.mTimer == null){
                timerStart();
            }else{
                mService.mTimer.cancel();
                timerStart();
            }
        }
    }

    private String getTimeFormat(int duration){
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        return String.format("%d:%02d", min, sec);
    }

    private void timerStart(){
        mService.mTimer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // 음악이 중지(or 끝난) 상태일 경우 무시
                if(!mService.isPlaying()) {
                    return;
                }

                int currentPosition = mService.getMediaPlayer().getCurrentPosition();
                Log.d(TAG, "currentPosition:" + currentPosition);

                /**
                 * Main Thread Handler 로 보내기 위한 Message
                 */
                Message message = mHandler.obtainMessage();
                message.what = MESSAGE_MUSIC_ING;
                mHandler.sendMessage(message);

                /**
                 * 음악 재생이 다 끝나면 중지
                 * 1000 을 뺸 이유
                 * 노래가 끝나도 max duration 과 current duration 값이 맞지 않아서
                 * 여유값으로 넣어줌
                 */
                if(currentPosition >= mService.getMediaPlayer().getDuration() - 1000){
                    cancel();
                    Message message2 = mHandler.obtainMessage();
                    message2.what = MESSAGE_MUSIC_END;
                    mHandler.sendMessage(message2);
                }
            }
        };
        // 바로 시작 하고, 1초 마다 반복 작업 실행
        mService.mTimer.schedule(timerTask,0, 1000);
    }


}