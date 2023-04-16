package com.example.test.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.fragments.ArtistFragment;
import com.example.test.fragments.GalleryFragment;
import com.example.test.fragments.PlayerFragment;
import com.example.test.fragments.SongFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Map;

public class MusicPlayerActivity extends AppCompatActivity {

    private static final String TAG = MusicPlayerActivity.class.getSimpleName();
    private ViewPager2 mViewPager;
    private MusicPlayerActivity.MyPagerAdapter mAdapter;

    /**
     * 필요한 권한
     */
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_MEDIA_AUDIO
    };

    /**
     * 권한 요청에 대한 Callback
     */
    private ActivityResultLauncher activityResultLauncher =  registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            Log.d(TAG, ""+result.toString());

            Boolean areAllGranted = true;
            // 모든 권한에 동의 했는지 확인
            for(Boolean b : result.values()) {
                areAllGranted = areAllGranted && b;
            }

            // 모든 권한에 동의
            if(areAllGranted) {

            // 모든 권한에 동의 하지 않음
            }else{
                finish();
            }
        }
    });

    /**
     * 권한 체크
     */
    private boolean checkPermissions(){
        // 허용 되지 않은 권한이 있는지 체크
        for(String permission : PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        /**
         * 권한이 없을 경우에는 권한을 요청한다.
         */
        if (!checkPermissions()) {
            this.activityResultLauncher.launch(PERMISSIONS);
        }

        /**
         * ViewPager 에 어댑터 세팅
         */
        mViewPager = (ViewPager2) findViewById(R.id.viewPager);
        mAdapter = new MusicPlayerActivity.MyPagerAdapter(getSupportFragmentManager(), getLifecycle());
        mViewPager.setAdapter(mAdapter);

        /**
         * TabLayout 을 ViewPager 와 연결
         */
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, mViewPager, (tab, position) -> {
            String title = "";
            switch(position){
                case  0:
                    title = "플레이어";
                    break;
                case 1:
                    title = "아티스트";
                    break;
                case 2:
                    title = "노래";
                    break;
            }
            tab.setText(title);
        }).attach();


    }

    public class MyPagerAdapter extends FragmentStateAdapter {
        private static final int NUM_PAGES = 3;

        public MyPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = null;
            switch(position){
                case 0 :
                    fragment = new PlayerFragment();
                    break;
                case 1 :
                    fragment = new ArtistFragment();
                    break;
                case 2 :
                    fragment = new SongFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }

    }

}