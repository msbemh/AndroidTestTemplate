package com.example.test.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.models.MyCalendar;
import com.example.test.models.TestModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferenceActivity extends AppCompatActivity {

    private static final String TAG = SharedPreferenceActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_preference);

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        List<TestModel> testList = new ArrayList<TestModel>();
        testList.add(new TestModel("test1", 1, LocalDate.now()));
        testList.add(new TestModel("test2", 2, LocalDate.now()));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(testList);
            Log.d(TAG, "jsonString:" + jsonString);
            editor.putString("testList", jsonString);
            editor.apply();

            testList = objectMapper.readValue(jsonString, new TypeReference<List<TestModel>>(){});
            Log.d(TAG, "testList:" + testList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }



    }
}