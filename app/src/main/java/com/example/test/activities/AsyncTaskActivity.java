package com.example.test.activities;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.test.R;

public class AsyncTaskActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);

        mTextView = (TextView) findViewById(R.id.textView);

        /**
         * [제약사항]
         * AsyncTask Class 는 반드시 UI 스레드에서 로드해야 한다.
         * AsyncTask 인스턴스는 반드시 UI 스레드에서 생성해야 한다.
         * execute() 도 반드시 UI 스레드에서 호출해야 한다.
         * 모든 콜백들은 직접 호출하면 안된다.
         * 태스크는 오직 1번만 실행할 수 있다. => new AsyncTask를 변수에 넣고 재활용 안된다는 것.
         */
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute(0);
        // 캔슬도 가능
//        myAsyncTask.cancel(true);

        // 순차적 실행
//        new MyAsyncTask().execute(0);
//        new MyAsyncTask().execute(0);
//        new MyAsyncTask().execute(0);

        /**
         * AsyncTask 는 순차적으로만 실행된다.
         * 여러개 실행 해도 1개씩 동작함.
         * 동시 실행 되게 하려면 아래와 같이 하면 된다.
         */
        // 병렬로 수행되는 AsyncTask
//        new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
//        new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);


    }


    private class MyAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            // 최초 실행 되는 부분
            Log.d("AsyncTask", "최초 실행");
        }

        /**
         * [...] 은 배열로도 받지만 그냥 값도 받는다.
         */
        @WorkerThread
        @Override
        protected Integer doInBackground(Integer... params) {
            int number = params[0];

            try{
                // 오래 걸리는 처리
                for (int i = 0; i < 10; i++){
                    Thread.sleep(1000);
                    number++;
                    // UI 갱신
                    publishProgress(number); // OnProgressUpdate 로 넘어감
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            return number; // OnPostExecute 로 넘어감
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // UI 갱신
            mTextView.setText(values[0] + "");
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Log.d("AsyncTask", "onPostExecute : " + integer);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            // 취소 후 작업 처리
        }
    }
}