package com.example.test.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.models.Memo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MemoListActivity extends AppCompatActivity {

    private static final String TAG = MemoListActivity.class.getSimpleName();
    private static final int CONTEXT_MENU_DELETE = 0;
    public static final int ACTION_MEMO_CREATE = 1;
    public static final int ACTION_MEMO_EDIT = 2;
    public static final int RESULT_EDIT_OK = 3;

    private List<Memo> data;
    private MyRecyclerAdapter adapter;
    private ActivityResultLauncher activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // 데이터
        data = new ArrayList<Memo>();

        // 레이아웃 설정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new MyRecyclerAdapter(data);
        recyclerView.setAdapter(adapter);

        /**
         * [활동 결과 콜백]
         * 메모리 부족으로 결과를 받을 Activity 가 사라지고 다시 생성 됐을때에도
         * 결과를 받을 수 있도록 처리해줌.
         * 결과 콜백과 Activity 를 분리시켜 줬기 때문에 가능
         */
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                // 생성 성공
                if(result.getResultCode() == RESULT_OK){
                    Intent intent = result.getData();
                    String title = intent.getStringExtra("title");
                    String content = intent.getStringExtra("content");
                    data.add(new Memo(getUUID(), title, content));
                    adapter.notifyDataSetChanged();
                // 취소
                }else if(result.getResultCode() == RESULT_CANCELED){
                    Log.d(TAG, "취소");
                // 수정 성공
                }else if(result.getResultCode() == RESULT_EDIT_OK){
                    Intent intent = result.getData();
                    String title = intent.getStringExtra("title");
                    String content = intent.getStringExtra("content");
                    String uuid = intent.getStringExtra("uuid");
                    Memo memo = data.stream().filter(m -> m.getUuid().equals(uuid)).findFirst().orElseGet(null);
                    memo.setContent(content);
                    memo.setTitle(title);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        /**
         * [플로팅 버튼]
         * 클릭 이벤트 Callback
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemoListActivity.this, MemoDetailActivity.class);
                intent.putExtra("action", ACTION_MEMO_CREATE);
                // 다른 활동 시작 및 결과 받기 호출
                activityResultLauncher.launch(intent);
            }
        });

    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * [Context 메뉴]
     * Select Callback
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int position = item.getOrder();
        switch (item.getItemId()) {
            case CONTEXT_MENU_DELETE:
                /**
                 * [AlertDialog]
                 * 삭제, 확인 Dialog 띄우기
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("정말 삭제 하시겠습니까?")
                        .setTitle("확인")
                        .setIcon(R.mipmap.ic_launcher);
                // 긍정 버튼
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 삭제 시키기
                        deleteMemo(position);
                    }
                });
                // 부정 버튼
                builder.setNegativeButton("취소", null);
                builder.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * EventBus 에서 보내는 이벤트 수신 하는 콜백 메서드
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        int position = event.position;
        // Do something
        Intent intent = new Intent(MemoListActivity.this, MemoDetailActivity.class);
        intent.putExtra("action", ACTION_MEMO_EDIT);
        intent.putExtra("data", data.get(position));
        // 다른 활동 시작 및 결과 받기 호출
        activityResultLauncher.launch(intent);
        //Toast.makeText(this, event.position+"", Toast.LENGTH_SHORT).show();
    }

    /**
     * EventBus 에서 발송할 이벤트
     */
    private class MessageEvent{
        View view;
        int position;
        public MessageEvent(View view, int position) {
            this.view = view;
            this.position = position;
        }
    }

    private void deleteMemo(int position){
        data.remove(data.get(position));
        adapter.notifyDataSetChanged();
        Toast.makeText(MemoListActivity.this, "삭제가 완료 됐습니다.",Toast.LENGTH_SHORT).show();
    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>{

        private final List<Memo> mData;

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param mData String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        private MyRecyclerAdapter(List<Memo> mData) {
            this.mData = mData;
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            /**
             * Create a new view, which defines the UI of the list item
             * parent : View가 생성 되고 Binding된 이후에 어느 parent에 추가 될지를 나타낼 때 사용 하는 parent임.
             */
            View convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.memo_row_item, parent, false);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            holder.textViewTitle.setText(mData.get(position).getTitle());
            holder.textViewContent.setText(mData.get(position).getContent());
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
            public TextView textViewTitle;
            public TextView textViewContent;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                // Define click listener for the ViewHolder's View
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            /**
                             * EventBus 를 통해 Message 전달
                             * RecyclerView#onMessageEvent
                             */
                            EventBus.getDefault().post(new MessageEvent(view, position));
                        }
                    }
                });

                // Context Menu Listener 등록
                itemView.setOnCreateContextMenuListener(this);

                textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                textViewContent = (TextView) itemView.findViewById(R.id.textViewContent);
            }

            /**
             * [Context 메뉴]
             * 생성
             */
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                /**
                 * param1 : 해당 menu id
                 * param2 : order 인데 나는 position 을 대신 보내고 있음.
                 */
                menu.add(CONTEXT_MENU_DELETE, getAdapterPosition(), Menu.NONE, "삭제");
            }
        }
    }

}