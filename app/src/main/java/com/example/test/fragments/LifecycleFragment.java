package com.example.test.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test.R;

public class LifecycleFragment extends Fragment {
    /**
     * 액티비티의 라이프사이클에 맞추기 위해서
     * 생성자에 파라미터는 받지 않는다.
     */
    public LifecycleFragment(){

    }

    /**
     * 액티비티에 붙을 때 호출 된다.
     * @param context : Activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    // 프래그먼트가 생성될 때
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // savedInstanceState 로 복원 처리 가능
        super.onCreate(savedInstanceState);
    }

    // View 생성
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lifecycle, container, false);
    }

    // View 소멸
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // 프래그먼트 소멸
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 액티비티와 연결 해제
    @Override
    public void onDetach() {
        super.onDetach();
    }
}