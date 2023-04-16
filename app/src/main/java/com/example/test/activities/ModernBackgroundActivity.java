package com.example.test.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.databinding.ActivityModernBackgroundBinding;
import com.example.test.models.NumViewModel;
import com.example.test.repository.RepositoryCallback;
import com.example.test.repository.Result;

public class ModernBackgroundActivity extends AppCompatActivity {

    private static final String TAG = ModernBackgroundActivity.class.getSimpleName();

    private NumViewModel viewmodel;
    private ActivityModernBackgroundBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityModernBackgroundBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        viewmodel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(NumViewModel.class);

        viewmodel.progressLiveData.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.progressBar2.setProgress(integer);
            }
        });

        binding.button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewmodel.longTask();
            }
        });
    }
}