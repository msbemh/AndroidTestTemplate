package com.example.test.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyIntentService extends IntentService {
    private static final String TAG = MyIntentService.class.getSimpleName();

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent.getAction().equals("play")){
            String path = intent.getStringExtra("path");
            for (int i = 0; i < 10; i++){
                try {
                    Thread.sleep(1000);
                    Log.d(TAG, "음악 실행중  : " + path);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{

        }
    }
}