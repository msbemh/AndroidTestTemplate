package com.example.test.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.test.R;
import com.example.test.services.MyIntentService;
import com.example.test.services.MyService;

public class ServiceTestActivity extends AppCompatActivity {

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
         * Main 스레드에서 돈다. 그래서 Thread 를 별도 생성해서 실행해야 한다.
         * 병렬 실행이 가능 하다.
         */
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }
}