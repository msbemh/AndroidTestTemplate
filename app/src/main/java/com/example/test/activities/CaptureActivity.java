package com.example.test.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.test.R;
import com.example.test.databinding.ActivityCaptureBinding;
import com.example.test.models.DetectText;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class CaptureActivity extends AppCompatActivity {
    private static final String TAG = CaptureActivity.class.getSimpleName();

    private ContentObserver contentObserver;
    private ActivityCaptureBinding binding;
    private Boolean isRock = false;
    private Bitmap bitmap;

    private FirebaseFunctions mFunctions;

    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);


    /**
     * 필요한 권한
     */
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_MEDIA_IMAGES,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    /**
     * 권한 요청에 대한 Callback
     */
    private ActivityResultLauncher activityResultLauncher =  registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            Log.d(TAG, ""+result.toString());

            Boolean areAllGranted = true;
            // 모든 권한에 동의 했는지 확인
            for(Boolean b : result.values()) {
                areAllGranted = areAllGranted && b;
            }

            // 모든 권한에 동의
            if(areAllGranted) {
                setting();
            // 모든 권한에 동의 하지 않음
            }else{
                Log.d(TAG, "권한이 부족합니다.");
                finish();
            }
        }
    });

    private boolean checkPermissions(){
        // 허용 되지 않은 권한이 있는지 체크
        for(String permission : PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCaptureBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);


        /**
         * 권한이 없을 경우에는 권한을 요청한다.
         */
        if (!checkPermissions()) {
            this.activityResultLauncher.launch(PERMISSIONS);
            return;
        }

        setting();

//        final MediaProjectionManager mediaProjectionManager = getSystemService(MediaProjectionManager.class);
//
//        ActivityResultLauncher<Intent> startMediaProjection = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        Intent intent = new Intent(this, MyMediaProjectionService.class);
//                        //startService(intent);
//
//                    }
//                }
//        );
//
//        //MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
//        // 다른 활동 시작 및 결과 받기 호출
//        startMediaProjection.launch(captureIntent);
    }

    private void setting(){
        ContentResolver contentResolver = getContentResolver();
        contentObserver = new ContentObserver(new Handler()){
            public void onChange( boolean selfChange , Uri uri){
                super.onChange(selfChange, uri);
                Log.d("TEST", "=================================");
                Log.d("TEST", "onChange 동작");
                Log.d("TEST", "uri:" + uri);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    queryRelativeDataColumn(uri);
                } else {
                    queryDataColumn(uri);
                }
            }
        };
        contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver );

        binding.button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRock = false;
            }
        });
    }

    private void queryDataColumn(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            do{
                String path = cursor.getString(dataColumn);
                if(path.contains("screenshot")){
                    Log.d("TEST", "들어옴");
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void queryRelativeDataColumn(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.RELATIVE_PATH, MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
        );

        int relativePathColumn = cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH);
        int displayNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

        Log.d("개수 : ", cursor.getCount() + "");
        while (cursor.moveToNext()) {
            String name = cursor.getString(displayNameColumn);
            String relativePath = cursor.getString(relativePathColumn);

            if(name.contains("Screenshot") || relativePath.contains("Screenshot")){
                Log.d("TEST", "스크린샷 입니다.");
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                Log.d("TEST", "path:" + path);
                Log.d("TEST", "Uri.parse(path):" + Uri.parse(path));
                ImageView imageView = findViewById(R.id.imageView3);

                Bitmap bitmap = BitmapFactory.decodeFile(path);
                imageView.setImageBitmap(bitmap);

                if(bitmap != null){
                    synchronized (bitmap){
                        if(isRock) return;
                        Log.d("TEST", "비트맵 존재");

                        int rotationDegree = 0;
                        InputImage image = InputImage.fromBitmap(bitmap, rotationDegree);

                        recognizeText(image);

                        isRock = true;
                    }
                }

//                File file = new File(path);
//                if(file.exists()){
//                    Log.d("TEST", "파일있음");
//                    Glide.with(this)
//                            .load(path)
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .into(imageView);
//                }else{
//                    Log.d("TEST", "파일없음");
//                }


            }
        }
    }

    private void recognizeText(InputImage image) {

        // [START get_detector_default]
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        // [END get_detector_default]

        // [START run_detector]
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                // [START_EXCLUDE]
                                // [START get_text]
                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                    Rect boundingBox = block.getBoundingBox();
                                    Point[] cornerPoints = block.getCornerPoints();
                                    String text = block.getText();
                                    Log.d(TAG, "text:" + text);
                                    for (Text.Line line: block.getLines()) {
                                        // ...
                                        for (Text.Element element: line.getElements()) {
                                            // ...
                                            Log.d(TAG, "element:" + element.getText());
                                            for (Text.Symbol symbol: element.getSymbols()) {
                                                // ...
                                            }
                                        }
                                    }
                                }
                                isRock = false;
                                // [END get_text]
                                // [END_EXCLUDE]
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        isRock = false;
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
        // [END run_detector]
    }

    @Override
    protected void onStop() {
        super.onStop();
//        getContentResolver().unregisterContentObserver(contentObserver);
    }
}