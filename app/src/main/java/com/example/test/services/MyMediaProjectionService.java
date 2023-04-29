package com.example.test.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.test.R;
import com.example.test.activities.ReceiverActivity;

public class MyMediaProjectionService extends Service {
    private static final int NOTIFICATION_ID = 1;

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            super.onStop();
            // 스크린샷 캡처가 완료되었을 때 호출되는 콜백 메서드
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the notification for the foreground service
        Notification notification = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("My Media Projection Service")
                .setContentText("Running...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        // Start the foreground service with the notification
        startForeground(NOTIFICATION_ID, notification);

        // TODO: Implement your media projection logic here
        final MediaProjectionManager mediaProjectionManager = getSystemService(MediaProjectionManager.class);

        final MediaProjection[] mediaProjection = new MediaProjection[1];

        mediaProjection[0] = mediaProjectionManager.getMediaProjection(-1, intent);
        Log.d("TEST", String.valueOf(mediaProjection[0]));

        MyMediaProjectionService.MediaProjectionCallback callback = new MyMediaProjectionService.MediaProjectionCallback();
        mediaProjection[0].registerCallback(callback, null);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
