package com.example.test.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.test.models.Song;
import com.example.test.models.SongListMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service {
    public MusicService() {
    }

    public static String ACTION_PLAY = "play";
    public static String ACTION_RESUME = "resume";
    private MediaPlayer mMediaPlayer;
    public Song mSong;
    public List<Song> songList;
    public SongListMessage songListMessage;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 음악 Item을 클릭 했을 때, 서비스 시작 된다.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(ACTION_PLAY.equals(intent.getAction())){
            /**
             * Song 객체 가져오기
             */
            Song song;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                song = intent.getSerializableExtra("song", Song.class);
                // songList는 1번만 받는다.
                if(songList == null) {
                    songListMessage = intent.getSerializableExtra("songList", SongListMessage.class);
                    songList = songListMessage.songList;
                }
            } else {
                song = (Song) intent.getSerializableExtra("data");
                // songList는 1번만 받는다.
                if(songList == null) {
                    songListMessage = (SongListMessage) intent.getSerializableExtra("songList");
                    songList = songListMessage.songList;
                }
            }

            mSong = song;
            musicStart(song);
        }
        return START_STICKY;
    }

    public void musicStart(Song song){
        Uri uri = song.uri;
        try {
            /**
             * 미디어 플레이어가 이미 재생중 이라면
             * 중지시키고 객체를 다시 생성 시킨다.
             */
            if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }

            /**
             * 미디어 플레이어 초기화
             */
            if(mMediaPlayer == null){
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            mMediaPlayer.setDataSource(this, uri);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mMediaPlayer.start();
                    mSong = song;

                    /**
                     * {@link com.example.test.fragments.MusicControllerFragment#updateUI(Boolean)}  }
                     */
                    EventBus.getDefault().post(isPlaying());
                    /**
                     * {@link com.example.test.fragments.MusicControllerFragment#updateMetadata(Song)}  }
                     */
                    //EventBus.getDefault().post(song);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void release(){
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }


    public void resumeMusic(){
        if(isPlaying()){
            mMediaPlayer.pause();
        }else{
            mMediaPlayer.start();
        }

        /**
         * {@link com.example.test.fragments.MusicControllerFragment#updateUI(Boolean)}}
         */
        EventBus.getDefault().post(isPlaying());

    }

    public boolean isPlaying() {
        if(mMediaPlayer != null){
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public MusicService getService() {
            // Return this instance of MusicService so clients can call public methods
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    public MediaPlayer getMediaPlayer(){
        return mMediaPlayer;
    }
}