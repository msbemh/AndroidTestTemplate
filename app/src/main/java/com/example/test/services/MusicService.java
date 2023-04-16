package com.example.test.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.test.models.Song;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class MusicService extends Service {
    public MusicService() {
    }

    public static String ACTION_PLAY = "play";
    public static String ACTION_RESUME = "resume";
    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(ACTION_PLAY.equals(intent.getAction())){
            /**
             * Song 객체 가져오기
             */
            Song song;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                song = (Song) intent.getSerializableExtra("song", Song.class);
            } else {
                song = (Song) intent.getSerializableExtra("data");
            }

            playMusic(song);
        }else if(ACTION_RESUME.equals(intent.getAction())){
            clickResumeButton();
        }

        return START_STICKY;
    }

    @Subscribe
    public void playMusic(Song song){
        Uri uri = song.uri;
        try {
            /**
             * 재생 중이라면 일단 꺼버린다.
             */
            if(mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            mMediaPlayer.setDataSource(this, uri);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mMediaPlayer.start();

                    /**
                     * {@link com.example.test.fragments.MusicControllerFragment#updatePlayButton(Boolean)}  }
                     */
                    EventBus.getDefault().post(isPlaying());
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void clickResumeButton(){
        if(isPlaying()){
            mMediaPlayer.pause();
        }else{
            mMediaPlayer.start();
        }

        /**
         * {@link com.example.test.fragments.MusicControllerFragment#updatePlayButton(Boolean)}}
         */
        EventBus.getDefault().post(isPlaying());

    }

    public boolean isPlaying() {
        if(mMediaPlayer != null){
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}