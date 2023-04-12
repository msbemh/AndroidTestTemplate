package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerViewTestActivity2 extends AppCompatActivity {

    private static final String TAG = RecyclerViewTestActivity2.class.getSimpleName();
    private ArrayList<Map<String, Object>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_test2);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewTest2);

        data = new ArrayList<>();
        for(int i=0; i<30; i++){
            Map map = new HashMap<String, Object>();
            map.put("title", "Title" + i);
            map.put("desc", "Desc" + i);
            data.add(map);
        }

        MyRecyclerAdapter adapter = new MyRecyclerAdapter(data);
        recyclerView.setAdapter(adapter);
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
     * EventBus 에서 보내는 이벤트 수신 하는 콜백 메서드
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        // Do something
        Toast.makeText(this, event.position+"", Toast.LENGTH_SHORT).show();
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

    private class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerViewTestActivity2.MyRecyclerAdapter.ViewHolder>{

        private final ArrayList<Map<String, Object>> mData;

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param mData String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        private MyRecyclerAdapter(ArrayList<Map<String, Object>> mData) {
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
                    .inflate(R.layout.text_row_item, parent, false);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            holder.textViewTitle.setText(mData.get(position).get("title").toString());
            holder.textViewSubTitle.setText(mData.get(position).get("desc").toString());
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder{
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
                            /**
                             * EventBus 를 통해 Message 전달
                             * RecyclerView#onMessageEvent
                             */
                            EventBus.getDefault().post(new MessageEvent(view, position));
                        }
                    }
                });

                textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                textViewSubTitle = (TextView) itemView.findViewById(R.id.textViewSubTitle);
            }
        }
    }
}