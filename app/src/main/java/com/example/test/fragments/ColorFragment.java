package com.example.test.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test.MainActivity;
import com.example.test.R;

public class ColorFragment extends Fragment {

    private static final String TAG = ColorFragment.class.getSimpleName();

    private int mColor = Color.WHITE;

    /**
     * 프래그먼트는 생성자로 파라미터를 전달하지 못한다
     * 프래그먼트를 생성할 때 파라미터를 넘기고 싶다면, 2가지 방법이 있다
     * 1. bundle, argument 를 이용하여 넘긴다.
     * 2. 팩토리 메소드를 만들어서 생성 한다.
     */
    public static ColorFragment newInstance(int color){
        ColorFragment colorFragment = new ColorFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("color", color);
        colorFragment.setArguments(bundle);
        return colorFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            int color = bundle.getInt("color");
            String text = bundle.getString("text");
            mColor = color;
            Log.d(TAG, "color:" + color);
            Log.d(TAG, "text:" + text);
        }

        view.setBackgroundColor(mColor);


        return view;
    }

    public void setColor(int color){
        mColor = color;
        if(getView() == null){
            return;
        }
        getView().setBackgroundColor(mColor);
    }
}