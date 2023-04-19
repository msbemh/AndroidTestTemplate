package com.example.test.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.example.test.R;
import com.example.test.activities.MusicPlayerActivity;
import com.example.test.fragments.PlayerFragment;
import com.example.test.models.Song;
import com.example.test.models.SongListMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

public class MusicService extends Service {
    public MusicService() {
    }

    private static final String TAG = MusicService.class.getSimpleName();

    public static String ACTION_PLAY = "play";
    public static String ACTION_RESUME = "resume";
    private MediaPlayer mMediaPlayer;
    public Song mSong;
    public int mPosition;
    public Timer mTimer;
    public List<Song> songList;
    public SongListMessage songListMessage;

    public static final String CHANNEL_ID = "ForegroundServiceChannel_1";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 음악 Item을 클릭 했을 때, 서비스 시작 된다.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_PLAY.equals(intent.getAction())) {
            /**
             * Song 객체 가져오기
             */
            Song song;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                song = intent.getSerializableExtra("song", Song.class);
                // songList는 1번만 받는다.
                if (songList == null) {
                    songListMessage = intent.getSerializableExtra("songList", SongListMessage.class);
                    songList = songListMessage.songList;
                }
            } else {
                song = (Song) intent.getSerializableExtra("data");
                // songList는 1번만 받는다.
                if (songList == null) {
                    songListMessage = (SongListMessage) intent.getSerializableExtra("songList");
                    songList = songListMessage.songList;
                }
            }
            musicStart(song);
        }else if(ACTION_RESUME.equals(intent.getAction())){
            resumeMusic();
        }
        return START_STICKY;
    }

    public void musicStart(Song song) {
        /**
         * Intent 로 넘겨온 Song과
         * List<Song> 의 객체주소가 다르기 때문에
         * ID를 비교해서 일치하는 Song을 mSong에 넣는다.
         */
        Song innerSong = songList.stream().filter(song2 -> song2.audioId.equals(song.audioId)).findAny().orElse(null);
        mSong = innerSong;
        Uri uri = song.uri;
        try {
            if (mMediaPlayer != null) {
                release();
            }

            /**
             * 미디어 플레이어 초기화
             */
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            /**
             * [미디어 플레이어]
             * 소스 세팅, 준비, 시작
             */
            mMediaPlayer.setDataSource(this, uri);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mMediaPlayer.start();
                    /**
                     * {@link com.example.test.fragments.MusicControllerFragment#updateUI(Boolean)}  }
                     * {@link com.example.test.fragments.PlayerFragment#updateUI(Boolean)}  }
                     */
                    EventBus.getDefault().post(isPlaying());
                }
            });

            // 포그라운드 서비스
            showNotification();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void release() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public void end() {
        release();
        /**
         * {@link com.example.test.fragments.MusicControllerFragment#updateUI(Boolean)}  }
         * {@link com.example.test.fragments.PlayerFragment#updateUI(Boolean)}  }
         */
        EventBus.getDefault().post(isPlaying());
    }

    /**
     * [뮤직 플레이어]
     * 재생, 중지
     */
    public void resumeMusic() {
        if (mMediaPlayer == null) {
            if (mSong != null) musicStart(mSong);
        } else {
            if (isPlaying()) {
                mMediaPlayer.pause();
                mPosition = mMediaPlayer.getCurrentPosition();
            } else {
                mMediaPlayer.seekTo(mPosition);
                mMediaPlayer.start();
            }
            /**
             * {@link com.example.test.fragments.MusicControllerFragment#updateUI(Boolean)}}
             */
            EventBus.getDefault().post(isPlaying());
        }
    }

    /**
     * [뮤직 플레이어]
     * 다음 뮤직 재생
     */
    public void nextMusic() {
        int index = songList.indexOf(mSong);
        // 현재 곡이 마지막 곡이라면, 처음으로 다시 돌아 가서 재생
        if (index == songList.size() - 1) {
            index = 0;
        } else {
            index++;
        }

        // 다음 Song 가져오기
        mSong = songList.get(index);
        musicStart(mSong);
    }

    /**
     * [뮤직 플레이어]
     * 이전 뮤직 재생
     */
    public void prevMusic() {
        int index = songList.indexOf(mSong);
        // 현재 곡이 처음 곡이라면, 이전 곡은 마지막 곡이 된다.
        if (index == 0) {
            index = songList.size() - 1;
        } else {
            index--;
        }

        // 다음 Song 가져오기
        mSong = songList.get(index);
        musicStart(mSong);
    }

    /**
     * [뮤직 플레이어]
     * 재생 중인지 확인
     */
    public boolean isPlaying() {
        try {
            if (mMediaPlayer != null) {
                return mMediaPlayer.isPlaying();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //안드로이드 O버전 이상에서는 알림창을 띄워야 포그라운드 사용 가능
            createNotificationChannel();

            /**
             * 맞춤 레이아웃 만들기
             */
//            NotificationSmallBinding binding = NotificationSmallBinding.inflate(LayoutInflater.from(this));
            RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_small);
            notificationLayout.setTextViewText(R.id.title_view, mSong.title);
            notificationLayout.setTextViewText(R.id.content_view, mSong.title);
            notificationLayout.setProgressBar(R.id.seekBar, 100, 0, false); // SeekBar 추가
//            RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);



           // notificationLayout.setImageViewUri(R.id.album_image, mSong.uri);





            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setContentTitle(mSong.title);
            builder.setContentText(mSong.artist);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            //.setCustomBigContentView(notificationLayoutExpanded)
            builder.setCustomContentView(notificationLayout);

            // Bitmap
            Bitmap bitmap = BitmapFactory.decodeResource(
                    getResources(), R.mipmap.ic_launcher);
            builder.setLargeIcon(bitmap);

            // 알림을 클릭하면 수행될 인텐트
            Intent notificationIntent = new Intent(this, MusicPlayerActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent,
                            PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(pendingIntent);

            // 클릭하면 날리기
            builder.setAutoCancel(false);

            // 기본 알림음
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(uri);

            // 진동
            builder.setVibrate(new long[]{100, 200, 300});


            Intent stopIntent = new Intent(this, MusicService.class);
            stopIntent.setAction(ACTION_RESUME);
            PendingIntent stopPendingIntent = PendingIntent.getService(this, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE);

            // 액션
            builder.addAction(R.mipmap.ic_launcher, "중지", stopPendingIntent);
            builder.addAction(R.mipmap.ic_launcher, "다음 곡", pendingIntent);
            builder.addAction(R.mipmap.ic_launcher, "이전 곡", pendingIntent);

            // Notification 전달
            int notificationId = 1; // 고유한 ID

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 없음");
                return;
            }
            // 알림표시
            //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            //notificationManager.notify(notificationId, builder.build());

            Notification notification = builder.build();

            /**
             * [알림]
             * 이미지 Glide를 이용하여 추가
             */
            NotificationTarget notificationTarget = new NotificationTarget(
                    this,
                    R.id.album_image,
                    notificationLayout,
                    notification,
                    notificationId);
            Glide.with(this)
                    .asBitmap()
                    .load(mSong.imageData)
                    .into(notificationTarget);

            startForeground(notificationId, notification);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
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