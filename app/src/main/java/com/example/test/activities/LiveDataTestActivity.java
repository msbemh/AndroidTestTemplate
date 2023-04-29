package com.example.test.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.load.model.ModelLoader;
import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.Util;
import com.example.test.databinding.ActivityLiveDataTestBinding;
import com.example.test.models.LiveDataModel;
import com.example.test.models.Memo;
import com.example.test.models.MyCalendar;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveDataTestActivity extends AppCompatActivity {
    private static final String TAG = LiveDataTestActivity.class.getSimpleName();
    private Map<String, Integer> map;
    private ActivityLiveDataTestBinding binding;
    private LiveDataModel liveDataModel;
    private ActivityResultLauncher activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLiveDataTestBinding.inflate(getLayoutInflater());
        binding.button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalDate localDate = LocalDate.of(2023, 4, 20);
                MyCalendar myCalendar = new MyCalendar("제목", "내용", "설명", localDate, false, R.color.blue_800);
                liveDataModel.setCalendar(localDate, myCalendar);
            }
        });

        setContentView(binding.getRoot());

        liveDataModel = new ViewModelProvider(this).get(LiveDataModel.class);
        Map<LocalDate, List<MyCalendar>> data = Util.generateCalendars();
        liveDataModel.setMap(data);

        liveDataModel.getLiveData().observe(this, new Observer<Map<LocalDate, List<MyCalendar>>>() {
            @Override
            public void onChanged(Map<LocalDate, List<MyCalendar>> localDateListMap) {
                Log.d(TAG, "데이터 변경 됐습니다.");
                LocalDate localDate = LocalDate.of(2023, 4, 20);
                List<MyCalendar> list = liveDataModel.getLiveData().getValue().get(localDate);
                if(list != null) Log.d(TAG, list.toString());

            }
        });

        binding.button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LiveDataTestActivity2.class);
                intent.putExtra("action", LiveDataTestActivity2.ACTION_MEMO_CREATE);
                // 다른 활동 시작 및 결과 받기 호출
                activityResultLauncher.launch(intent);
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                // 생성 성공
                if(result.getResultCode() == RESULT_OK){
                    Intent intent = result.getData();
                    String test = intent.getStringExtra("test");
                    Log.d(TAG, test);
                }
            }
        });

    }
}