package com.example.test.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.test.Util;

public class MusicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        /**
         * [뮤직플레이어]
         * 재시작 & 중지
         */
        if(Util.ACTION_MUSIC_RESUME.equals(action)){

        }else if(Util.ACTION_MUSIC_NEXT.equals(action)){

        }else if(Util.ACTION_MUSIC_PREV.equals(action)){

        }
    }
}
