package com.example.test.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.test.R;
import com.example.test.databinding.ActivityMemoDetailBinding;
import com.example.test.models.Memo;

public class MemoDetailActivity extends AppCompatActivity {

    private static final String TAG = MemoDetailActivity.class.getSimpleName();
    private ActivityMemoDetailBinding binding;
    private String mMode = "";
    private Memo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 활동과 뷰 결합
        binding = ActivityMemoDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        /**
         * 전달된 Intent 받는 부분
         */
        Intent receiveIntent = getIntent();
        int actionNum = receiveIntent.getIntExtra("action", 0);
        // 생성 모드
        if(actionNum == MemoListActivity.ACTION_MEMO_CREATE){
            mMode = "C";
        // 수정 모드
        }else if(actionNum == MemoListActivity.ACTION_MEMO_EDIT){
            memo = (Memo) receiveIntent.getSerializableExtra("data");
            binding.editTextTitle.setText(memo.getTitle());
            binding.editTextContent.setText(memo.getContent());
            mMode = "U";
        }
    }

    /**
     * [옵션 메뉴] Create
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_memo, menu);
        return true;
    }

    /**
     * [옵션 메뉴] Select Callback
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.memo_cancel_menu:
                // 취소
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.memo_save_menu:
                if("C".equals(mMode)){
                    // 저장
                    Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                    intent.putExtra("title", binding.editTextTitle.getText().toString());
                    intent.putExtra("content", binding.editTextContent.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }else if("U".equals(mMode)){
                    // 수정
                    Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                    intent.putExtra("title", binding.editTextTitle.getText().toString());
                    intent.putExtra("content", binding.editTextContent.getText().toString());
                    intent.putExtra("uuid", memo.getUuid());
                    setResult(MemoListActivity.RESULT_EDIT_OK, intent);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}