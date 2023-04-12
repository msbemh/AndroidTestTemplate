package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewTestActivity extends AppCompatActivity {

    private static final String TAG = RecyclerViewTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_test);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewTest);

        List<String> data = new ArrayList<>();
        for(int i=0; i<100; i++){
            data.add(i+"");
        }

        RecyclerViewTestActivity.MyRecyclerAdapter adapter = new RecyclerViewTestActivity.MyRecyclerAdapter(data);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerViewTestActivity.MyRecyclerAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View view, int position) {
                String str = data.get(position);
                Log.d("test", str);
                Toast.makeText(RecyclerViewTestActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerViewTestActivity.MyRecyclerAdapter.ViewHolder>{

        private final List<String> mData;

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param mData String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        public MyRecyclerAdapter(List<String> mData) {
            this.mData = mData;
        }

        public static RecyclerViewTestActivity.MyRecyclerAdapter.OnItemClickEventListener mItemClickListener;

        public interface OnItemClickEventListener {
            void onItemClick(View view, int position);
        }

        public void setOnItemClickListener(RecyclerViewTestActivity.MyRecyclerAdapter.OnItemClickEventListener listener) {
            mItemClickListener = listener;
        }

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        private static class ViewHolder extends RecyclerView.ViewHolder{
            public TextView textView;

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

                textView = (TextView) itemView.findViewById(R.id.textViewTitle);
            }
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public RecyclerViewTestActivity.MyRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Create a new view, which defines the UI of the list item
            View convertView = LayoutInflater.from(parent.getContext()).inflate((R.layout.text_row_item),parent,false);
            return new ViewHolder(convertView);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull RecyclerViewTestActivity.MyRecyclerAdapter.ViewHolder holder, int position) {
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            holder.textView.setText(mData.get(position));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}