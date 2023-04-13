package com.example.test.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private static final String TAG = MyService.class.getSimpleName();

    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    private int num = 5;

    public MyService() {
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public MyService getService() {
            // Return this instance of MyService so clients can call public methods
            return MyService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++){
                    try {
                        Thread.sleep(1000);
                        Log.d(TAG, "onStartCommand :" + i);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

        // 죽으면 다시 생성 되지 않게 동작
        return START_NOT_STICKY;
    }

    public int getNum(){
        return this.num;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}