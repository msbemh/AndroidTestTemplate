package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.activities.AsyncTaskActivity;
import com.example.test.activities.CustomDialogActivity;
import com.example.test.activities.FragmentActivity;
import com.example.test.activities.MemoListActivity;
import com.example.test.activities.RecyclerViewTestActivity;
import com.example.test.activities.RecyclerViewTestActivity2;
import com.example.test.activities.ServiceTestActivity;
import com.example.test.activities.ThreadHandlerActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Map<String, Object>> mMenuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMain);

        mMenuList = new ArrayList<>();
        addItem("리사이클러뷰", "클릭 인터페이스 직접 구현", RecyclerViewTestActivity.class);
        addItem("리사이클러뷰", "Event Bus를 이용한 클릭 리스너", RecyclerViewTestActivity2.class);
        addItem("서비스", "IntentService, Service, BindService", ServiceTestActivity.class);
        addItem("스레드", "Thread에서 UI 갱신은 Handler를 이용", ThreadHandlerActivity.class);
        addItem("AsyncTask", "(deprecated) Background 작업 및 UI 갱신 방법 중 하나", AsyncTaskActivity.class);
        addItem("메모 만들어 보기", "activity 결과 받기(신규), Context Menu(RecyclerView), FloatButton, Option Menu", MemoListActivity.class);
        addItem("커스텀 다이얼 로그", "custom dialog", CustomDialogActivity.class);
        addItem("프래그먼트", "Fragment Test", FragmentActivity.class);

        MyRecyclerAdapter adapter = new MyRecyclerAdapter(mMenuList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View view, int position) {
                String title = mMenuList.get(position).get("title").toString();
                String desc = mMenuList.get(position).get("desc").toString();
                Intent intent = (Intent) mMenuList.get(position).get("intent");
                startActivity(intent);
            }
        });
    }

    private void addItem(String title, String desc, Class cls){
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("desc", desc);
        map.put("intent", new Intent(this, cls));
        mMenuList.add(map);
    }

    // 우측 상단 메뉴를 붙이는 부분
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_setting:
                Toast.makeText(MainActivity.this, "설정1 클릭", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_setting2:
                Toast.makeText(MainActivity.this, "설정2 클릭", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_setting3:
                Toast.makeText(MainActivity.this, "설정3 클릭", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 리사이클러뷰 어댑터
     */
    private static class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>{

        private final ArrayList<Map<String, Object>> mData;
        /**
         * Initialize the dataset of the Adapter.
         *
         * @param mData String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        public MyRecyclerAdapter(ArrayList<Map<String, Object>> mData) {
            this.mData = mData;
        }

        public static OnItemClickEventListener mItemClickListener;

        public interface OnItemClickEventListener {
            void onItemClick(View view, int position);
        }

        public void setOnItemClickListener(OnItemClickEventListener listener) {
            mItemClickListener = listener;
        }

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        private static class ViewHolder extends RecyclerView.ViewHolder{
            public TextView textViewTitle;
            public TextView textViewSubTitle;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                // Define click listener for the ViewHolder's View
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mItemClickListener.onItemClick(view, position);
                        }
                    }
                });

                textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                textViewSubTitle = (TextView) itemView.findViewById(R.id.textViewSubTitle);
            }
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Create a new view, which defines the UI of the list item
            View convertView = LayoutInflater.from(parent.getContext()).inflate((R.layout.text_row_item),parent,false);
            return new ViewHolder(convertView);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            holder.textViewTitle.setText(mData.get(position).get("title").toString());
            holder.textViewSubTitle.setText(mData.get(position).get("desc").toString());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}