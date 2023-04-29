package com.example.test.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.test.R;
import com.example.test.Util;
import com.example.test.databinding.ActivityLiveDataTest2Binding;
import com.example.test.databinding.ActivityMemoDetailBinding;
import com.example.test.models.LiveDataModel;
import com.example.test.models.Memo;
import com.example.test.models.MyCalendar;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class LiveDataTestActivity2 extends AppCompatActivity {
    private static final int CONTEXT_MENU_DELETE = 0;
    public static final int ACTION_MEMO_CREATE = 1;
    public static final int ACTION_MEMO_EDIT = 2;
    public static final int RESULT_EDIT_OK = 3;

    private LiveDataModel liveDataModel;

    private String mMode;
    private ActivityLiveDataTest2Binding binding;

    private static final String TAG = LiveDataTestActivity2.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 활동과 뷰 결합
        binding = ActivityLiveDataTest2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /**
         * 전달된 Intent 받는 부분
         */
        Intent receiveIntent = getIntent();
        int actionNum = receiveIntent.getIntExtra("action", 0);
        // 생성 모드
        if(actionNum == LiveDataTestActivity2.ACTION_MEMO_CREATE){
            mMode = "C";
        // 수정 모드
        }else if(actionNum == LiveDataTestActivity2.ACTION_MEMO_EDIT){
            mMode = "U";
        }

        binding.button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 저장
                Intent intent = new Intent(getApplicationContext(), LiveDataTestActivity.class);
                intent.putExtra("test", "test");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        binding.button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalDate localDate = LocalDate.of(2023, 4, 29);
                MyCalendar myCalendar = new MyCalendar("제목", "내용", "설명", localDate, false, R.color.blue_800);
                liveDataModel.setCalendar(localDate, myCalendar);
            }
        });

        liveDataModel = new ViewModelProvider(this).get(LiveDataModel.class);
        //Map<LocalDate, List<MyCalendar>> data = Util.generateCalendars();
        //liveDataModel.setMap(data);

        liveDataModel.getLiveData().observe(this, new Observer<Map<LocalDate, List<MyCalendar>>>() {
            @Override
            public void onChanged(Map<LocalDate, List<MyCalendar>> localDateListMap) {
                Log.d(TAG, "데이터 변경 됐습니다.");
                LocalDate localDate = LocalDate.of(2023, 4, 20);
                List<MyCalendar> list = liveDataModel.getLiveData().getValue().get(localDate);
                if(list != null) Log.d(TAG, list.toString());

            }
        });
    }
}