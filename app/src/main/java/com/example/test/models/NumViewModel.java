package com.example.test.models;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.test.MyApplication;
import com.example.test.activities.ModernBackgroundActivity;
import com.example.test.repository.RepositoryCallback;
import com.example.test.repository.NumberRepository;
import com.example.test.repository.Result;

public class NumViewModel extends AndroidViewModel {
    private final NumberRepository repository;

    public MutableLiveData<Integer> progressLiveData = new MutableLiveData<>(0);

    public NumViewModel(@NonNull Application application) {
        super(application);

        repository = new NumberRepository(
                ((MyApplication) application).executorService,
                ((MyApplication) application).mainThreadHandler
        );
    }

    public void longTask() {
        repository.longTask(new RepositoryCallback<Integer>() {
            @Override
            public void onComplete(Result<Integer> result) {
                if(result instanceof Result.Success){
                    progressLiveData.postValue(((Result.Success<Integer>) result).data);
                }else if(result instanceof Result.Error){
                    Log.e("Test", "에러");
                }
            }
        });
    }

}
