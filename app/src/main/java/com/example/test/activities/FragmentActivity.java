package com.example.test.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.test.R;
import com.example.test.fragments.ColorFragment;

import java.util.Random;

public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        // 프래그먼트 가져오기
        FragmentManager fragmentManager = getSupportFragmentManager();
        ColorFragment colorFragment = (ColorFragment) fragmentManager.findFragmentById(R.id.color_frg);
        colorFragment.setColor(Color.GREEN);

        /**
         * Transaction Manager 를 이용하여 Fragment 를 동적으로 추가
         */
        ColorFragment colorFragment2 = new ColorFragment();

        // 이렇게 bundle 과 argument 를 이용해서 전달할 수 있다.
        Bundle bundle = new Bundle();
        bundle.putInt("color", Color.YELLOW);
        bundle.putString("text", "글자");
        colorFragment2.setArguments(bundle);

        // 팩토리 패턴을 활용한 프래그먼트 생성
        ColorFragment colorFragment3 = ColorFragment.newInstance(Color.BLUE);

        fragmentManager.beginTransaction()
                .add(R.id.container, colorFragment2)
                .commit();
    }

    public void onClick(View view) {
        ColorFragment colorFragment = new ColorFragment();

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        colorFragment.setColor(color);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, colorFragment)
                .commit();
    }
}