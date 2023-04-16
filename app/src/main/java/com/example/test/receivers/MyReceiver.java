package com.example.test.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.test.MainActivity;

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = BroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())){
            Log.d(TAG, "전원 연결 됨");
        }
    }
}