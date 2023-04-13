package com.example.test.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.test.R;

import org.w3c.dom.Text;

public class ThreadHandlerActivity extends AppCompatActivity {

    private static final String TAG = ThreadHandlerActivity.class.getSimpleName();

    private TextView mTextView;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Handler mHandler2 = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            mTextView.setText(mNumber + "");
        }
    };

    private int mNumber = 0;

    /**
     * 별도의 스레드
     * 스레드에서 Handler를 생성할 수 없다.
     * Handler 를 사용해서 UI 갱신을 해야 됨
     * Main Looper : 화면 갱신을 위한 메인 Looper.
     * Message Queue 에서 Message 가 있다면 순차적으로 Message 를 꺼내 온다.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_handler);

        mTextView = (TextView) findViewById(R.id.textView);

        // 메인 스레드에서는 UI 갱신 가능
        Log.d(TAG, "메인 스레드:" + Thread.currentThread() + "");
        Log.d(TAG, "메인 스레드 명:" + Thread.currentThread().getName() + "");

        /**
         * 동작 안해야 하는데 왜 동작하는지 모르겠다...
         * Worker 스레드에서 UI 수정이 되고 있다...
         * 안되야 하는데... 이상함
         */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "스레드 시작");
//                Log.d(TAG, Thread.currentThread() + "");
//                Log.d(TAG, Thread.currentThread().getName() + "");
//                try {
//                    for (int i = 0; i < 100000; i++){
//                        int finalI = i;
//
//                        Log.d(TAG, "[Worker Thread]" + Thread.currentThread() + "");
//                        Log.d(TAG, "[Worker Thread]" + Thread.currentThread().getName() + "");
//                        mTextView.setText(finalI +"");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Log.d(TAG, "스레드 끝");
//            }
//        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    for(int i=0; i<10; i++){
                        // Handler 를 이용하여 UI 수정
                        mNumber++;

                        /**
                         * 핸들러에서 post 호출
                         */
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mTextView.setText(mNumber + "");
                            }
                        });

                        /**
                         * 위와 같은 코드
                         * handler2 는 handleMessage 를 override 정의해서
                         * send 에 대한 Message 를 받고 있음.
                         */
//                        mHandler2.sendEmptyMessageAtTime(0, 0);

                        /**
                         * 위와 같은 코드
                         * 핸들러를 직접 생성하지 않고, View 에서 제공하는 post 기능을 이용
                         * 내부적으로 핸들러가 있을 것으로 추정 됨.
                         */
//                        mTextView.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                mTextView.setText(mNumber + "");
//                            }
//                        });

                        /**
                         * 위와 같은 코드
                         * Activity 에서만 가능
                         * Fragment 는 따로 Handler 를 만들어야겠지?
                         */
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mTextView.setText(mNumber + "");
//                            }
//                        });

                        Thread.sleep(1000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onButtonClick(View view) {
        Log.d(TAG, "버튼 클릭");
    }
}