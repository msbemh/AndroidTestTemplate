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
import android.service.notification.StatusBarNotification;
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
import com.example.test.Util;
import com.example.test.activities.MusicPlayerActivity;
import com.example.test.databinding.NotificationSmallBinding;
import com.example.test.fragments.PlayerFragment;
import com.example.test.models.Song;
import com.example.test.models.SongListMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

public class MusicService extends Service {
    private RemoteViews remoteViewsExpanded;
    private NotificationCompat.Builder builder;
    private Notification notification;

    public MusicService() {
    }

    private static final String TAG = MusicService.class.getSimpleName();

    public static String ACTION_PLAY = "play";
    public static String ACTION_RESUME = "resume";
    public static String ACTION_NEXT = "next";
    public static String ACTION_PREV = "prev";
    public static String ACTION_STOP = "stop";

    private MediaPlayer mMediaPlayer;
    public Song mSong;
    public int mPosition;
    public Timer mTimer;
    public List<Song> songList;
    public SongListMessage songListMessage;
    private RemoteViews remoteViews;

    public static final String CHANNEL_ID = "ForegroundServiceChannel_1";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "서비스 onCreate");
    }

    /**
     * Action 으로 Service Start가 호출 됐을때
     * 호출 받는 Callback
     *
     *  - 음악 Item을 클릭 했을 때, 서비스 시작 된다.
     *  -
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "서비스 onStartCommand");
        Boolean isPending = intent.getBooleanExtra("isPending", false);

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
        } else if (ACTION_RESUME.equals(intent.getAction())) {
            resumeMusic();
            if (isPending) notificationUIUpdate(intent.getAction());
        } else if (ACTION_NEXT.equals(intent.getAction())) {
            nextMusic();
            if (isPending) notificationUIUpdate(intent.getAction());
        } else if (ACTION_PREV.equals(intent.getAction())) {
            prevMusic();
            if (isPending) notificationUIUpdate(intent.getAction());
        } else if (ACTION_STOP.equals(intent.getAction())) {
            notifiactionExit();
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
            // 중지
            if (isPlaying()) {
                mMediaPlayer.pause();
                mPosition = mMediaPlayer.getCurrentPosition();
            // 시작
            } else {
                mMediaPlayer.seekTo(mPosition);
                mMediaPlayer.start();
                // 알림이 동작 중이지 않으면 재개
                if(!isInProgressNotification()){
                    showNotification();
                }
            }

            // Application UI Update
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

    public void notifiactionExit() {
        mMediaPlayer.pause();
        mPosition = mMediaPlayer.getCurrentPosition();

        // Foreground 서비스 중지
        stopForeground(true);
        // 서비스 중지
        stopSelf();

        // 알림 제거
        NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        notificationManager.cancel(Util.NOTIFICATION_ID);

        /**
         * {@link com.example.test.fragments.MusicControllerFragment#updateUI(Boolean)}}
         */
        EventBus.getDefault().post(isPlaying());
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

    /**
     * [Notification]
     * Show
     */
    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //안드로이드 O버전 이상에서는 알림창을 띄워야 포그라운드 사용 가능
            createNotificationChannel();

            /**
             * 맞춤 레이아웃 만들기
             */
            NotificationSmallBinding binding = NotificationSmallBinding.inflate(LayoutInflater.from(this));

            /**
             * [RemoteView]
             * 앱에서 생성한 뷰 계층 구조를 다른 프로세스에서도 사용할 수 있게 해줍니다.
             * 즉, 앱에서 생성한 뷰를 다른 앱이나 시스템 서비스에서 사용할 수 있습니다.
             * Example, Notification이나 App Widget 등에서 앱의 뷰 계층을 사용할 수 있습니다.
             */
            remoteViews = new RemoteViews(getPackageName(), R.layout.notification_small);
            remoteViewsExpanded = new RemoteViews(getPackageName(), R.layout.notification_large);
            remoteViews.setTextViewText(R.id.title_view, mSong.title);
            remoteViews.setTextViewText(R.id.content_view, mSong.title);

            /**
             * [NotificationCompat.Builder]
             * 알림을 만들기 위해 사용되는 클래스
             */
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setContentTitle(mSong.title);
            builder.setContentText(mSong.artist);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setCustomBigContentView(remoteViewsExpanded);
            builder.setCustomContentView(remoteViews);
            builder.setOngoing(true); // Swipe로 알림 삭제하는 기능 중지

            /**
             * Notification
             * 컨텐츠 영역 클릭 Pending Intent
             */
            Intent contentIntent = new Intent(this, MusicPlayerActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, contentIntent,
                            PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(pendingIntent);

            /**
             * Notification Click Event
             */
            // 시작 & 중지
            Intent resumeIntent = new Intent(this, MusicService.class);
            resumeIntent.setAction(ACTION_RESUME);
            resumeIntent.putExtra("isPending", true);
            PendingIntent resumePendingIntent = PendingIntent.getService(this, 0, resumeIntent, PendingIntent.FLAG_MUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.play_button, resumePendingIntent);

            // Next
            Intent NextIntent = new Intent(this, MusicService.class);
            NextIntent.setAction(ACTION_NEXT);
            NextIntent.putExtra("isPending", true);
            PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, NextIntent, PendingIntent.FLAG_MUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.next_button, nextPendingIntent);

            // Prev
            Intent prevIntent = new Intent(this, MusicService.class);
            prevIntent.setAction(ACTION_PREV);
            prevIntent.putExtra("isPending", true);
            PendingIntent prevPendingIntent = PendingIntent.getService(this, 0, prevIntent, PendingIntent.FLAG_MUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.prev_button, prevPendingIntent);

            // Stop
            Intent stopIntent = new Intent(this, MusicService.class);
            stopIntent.setAction(ACTION_STOP);
            stopIntent.putExtra("isPending", true);
            PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_MUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.exit_button2, stopPendingIntent);

            /**
             *  [Notification 옵션들]
             *
             // Bitmap
             Bitmap bitmap = BitmapFactory.decodeResource(
             getResources(), R.mipmap.ic_launcher);
             builder.setLargeIcon(bitmap);

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
             */

            /**
             * 권한 체크
             */
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 없음");
                return;
            }

            /**
             * 알림 표시
             */
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(Util.NOTIFICATION_ID, builder.build());

            notification = builder.build();

            /**
             * [이미지]
             * Glide라이브러리를 이용하여 이미지 추가가
             *
             * [Glide]
             * 이미지 로딩과 캐싱을 처리하기 위한 오픈 소스 라이브러리입니다.
             * 이미지를 비동기적으로 로드하고 디스크 캐시와 메모리 캐시를 사용하여 이미지 로딩 속도를 높입니다.
             * 이미지 크기를 자동으로 조정.
             * 이미지 로딩 중에 애니메이션을 적용가능.
             */
            NotificationTarget notificationTarget = new NotificationTarget(
                    this,
                    R.id.album_image,
                    remoteViews,
                    notification,
                    Util.NOTIFICATION_ID);
            Glide.with(this)
                    .asBitmap()
                    .load(mSong.imageData)
                    .into(notificationTarget);

            /**
             * 포그라운드 서비스를 시작하면
             * 자동으로 Notification을 띄워준다.
             */
            startForeground(Util.NOTIFICATION_ID, notification);
        }
    }

    private void notificationUIUpdate(String action) {
        // 재시작 & 중지
        if(ACTION_RESUME.equals(action)){
            if (isPlaying()) {
                remoteViews.setImageViewResource(R.id.play_button, R.drawable.pause);
            } else {
                remoteViews.setImageViewResource(R.id.play_button, R.drawable.play);
            }
        // 다음곡, 이전곡
        }else if(ACTION_PREV.equals(action) || ACTION_NEXT.equals(action)){
            // Image 변경
            NotificationTarget notificationTarget = new NotificationTarget(
                    this,
                    R.id.album_image,
                    remoteViews,
                    notification,
                    Util.NOTIFICATION_ID);
            Glide.with(this)
                    .asBitmap()
                    .load(mSong.imageData)
                    .into(notificationTarget);
        }

        // 권한체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // 알림 변경경
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Util.NOTIFICATION_ID, builder.build());
    }

    private boolean isInProgressNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = notificationManager.getActiveNotifications();

        if (notifications.length > 0) {
            for(StatusBarNotification n : notifications){
                if(n.getId() == Util.NOTIFICATION_ID) return true;
            }
        }
        return false;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
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
        Log.d(TAG, "서비스 onBind");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "서비스 onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "서비스 onDestroy");
        EventBus.getDefault().unregister(this);
    }
    public MediaPlayer getMediaPlayer(){
        return mMediaPlayer;
    }
}