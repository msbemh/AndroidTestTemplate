package com.example.test.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.databinding.ActivityCustomDialogBinding;
import com.example.test.databinding.DialogCustomBinding;

public class CustomDialogActivity extends AppCompatActivity {

    private static final String TAG = CustomDialogActivity.class.getSimpleName();
    private ActivityCustomDialogBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 활동과 뷰 결합
        mBinding = ActivityCustomDialogBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        mBinding.button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });
    }

    private void showCustomDialog(){
        DialogCustomBinding dialogCustomBinding = DialogCustomBinding.inflate(getLayoutInflater());
        View view = dialogCustomBinding.getRoot();
        final EditText idEditText = dialogCustomBinding.idEdit;
        final EditText passWordEditText = dialogCustomBinding.passwordEdit;

        /**
         * [AlertDialog]
         * 삭제, 확인 Dialog 띄우기
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("테스트")
                .setTitle("확인")
                .setIcon(R.mipmap.ic_launcher)
                .setView(view);

        // 긍정 버튼
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "확인 ID : " + idEditText.getText());
                Log.d(TAG, "확인 Password : " + passWordEditText.getText());

            }
        });
        // 부정 버튼
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "취소 ID : " + idEditText.getText());
                Log.d(TAG, "취소 Password : " + passWordEditText.getText());
            }
        });
        builder.show();
    }
}