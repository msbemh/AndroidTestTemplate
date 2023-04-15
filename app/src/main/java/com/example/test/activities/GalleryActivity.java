package com.example.test.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.example.test.R;
import com.example.test.fragments.GalleryFragment.onFragmentInteractionListener;

public class GalleryActivity extends AppCompatActivity implements onFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}