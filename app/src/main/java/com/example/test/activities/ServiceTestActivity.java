package com.example.test.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.example.test.R;
import com.example.test.services.MyIntentService;
import com.example.test.services.MyService;

public class ServiceTestActivity extends AppCompatActivity {

    private static final String TAG = ServiceTestActivity.class.getSimpleName();
    private MyService mService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);


    }

    public void onStartIntentService(View view) {
        /**
         * 순차적으로 실행된다.
         * Worker 스레드로 실행 된다.
         * Queue에 쌓여서 여러번 실행 해도 순차적으로 실행 된다.
         */
        Intent intent = new Intent(this, MyIntentService.class);
        intent.setAction("play");
        intent.putExtra("path", "file://sdfsdf");
        startService(intent);
    }

    public void onStartService(View view) {
        /**
         * 1. 기본적으로 Main 스레드에서 돈다.
         * 그래서 Thread 를 별도로 생성해서 실행해야 한다.
         * 2. 병렬 실행 가능
         */
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    public void onBindService(View view) {
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void getNumber(View view){
        if(mBound){
            Log.d(TAG, mService.getNum() + "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 바인딩 끊기
        if(mBound){
            unbindService(connection);
            mBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // 바인딩 성공
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            /**
             * 시스템이 바인딩 강제 종료 시에 호출
             * unbindService() 로는 호출 되지 않음.
             */
            mBound = false;
        }
    };
}