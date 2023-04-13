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

public class MemoDetailActivity extends AppCompatActivity {

    private static final String TAG = MemoDetailActivity.class.getSimpleName();
    private ActivityMemoDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 활동과 뷰 결합
        binding = ActivityMemoDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

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
                // 저장
                Intent intent = new Intent(getApplicationContext(), MemoListActivity.class);
                intent.putExtra("title", binding.editTextTitle.getText().toString());
                intent.putExtra("content", binding.editTextContent.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}