package com.example.test;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {
    /*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    public ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CORES);
    public Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());



}
