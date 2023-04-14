package com.example.test.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test.R;

public class ColorFragment extends Fragment {

    private int mColor = Color.WHITE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color, container, false);
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