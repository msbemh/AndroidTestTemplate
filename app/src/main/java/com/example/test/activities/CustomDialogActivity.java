package com.example.test.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.test.MainActivity;
import com.example.test.R;

public class CustomDialogActivity extends AppCompatActivity {

    private static final String TAG = CustomDialogActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dialog);
    }
}