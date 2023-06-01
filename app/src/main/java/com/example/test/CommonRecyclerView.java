package com.example.test;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.example.test.activities.RecyclerViewTestActivity;
import com.example.test.databinding.TextRowItemBinding;

/**
 * [기존 리사이클러뷰 구현 순서]
 * 1. RecyclerView.ViewHolder를 상속 받는 ViewHolder Class 정의
 *  - 생성자에서 클릭 이벤트 구현
 *  - getBindingAdapterPosition 를 이용하여 position 받을 수 있음
 *
 * 2. Adapter<VH extends ViewHolder>를 상속 받는 Adapter Class 정의
 *  - onCreateViewHolder Override 구현
 *  - onBindViewHolder Override 구현
 *  - getItemCount Override 구현
 *
 * 3. List Data 생성
 *
 * 4. List Data를 ViewHolder 와 Adapter 에 적용
 *
 * 5. RecyclerView 객체 가져옴
 *
 * 6. Layout 매니저 생성하여 RecyclerView에 적용
 *
 * 7. Adapter 생성하여 RecyclerView에 적용
 *
 * ====================================
 * [위 작업을 class 하나로 공통으로 묶었을때, 넘겨줘야할 요소들]
 *
 * 1. RecyclerView
 *
 * 2. ListData
 *
 * 3. 클릭 이벤트 리스너
 *
 * 4. Layout 매니저
 *
 * 5. ViewHolder binding
 *
 *
 */

import java.util.List;

public class CommonRecyclerView {
    private List<?> dataList;
    private RecyclerView recyclerView;
    private View.OnClickListener onClickListener;
    private Context context;
    private OnItemClickEventListener onItemClickListener;
    private OnBindViewHolder onBindViewHolder;

    public interface OnItemClickEventListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public interface OnBindViewHolder {
        void onBindViewHolder(MyRecyclerAdapter.ViewHolder holder, int position);
    }

    public CommonRecyclerView(OnBindViewHolder onViewHolder){
        this.onBindViewHolder = onViewHolder;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void setRecyclerView(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
    }

    public void setDataList(List<?> dataList){
        this.dataList = dataList;
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public void setOnItemClickListener(OnItemClickEventListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void adapt(){
        // 레이아웃 적용
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        // 어댑터 적용
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(dataList);
        recyclerView.setAdapter(adapter);
    }

    public class MyRecyclerAdapter<E> extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>{

        private List<?> dataList;

        public MyRecyclerAdapter(List<?> dataList) {
            this.dataList = dataList;
        }

        public class ViewHolder<T extends ViewBinding> extends RecyclerView.ViewHolder{
            private T binding;

            public ViewHolder(T binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void bindData() {
                // ViewBinding을 사용하여 뷰 요소에 접근하는 로직을 작성합니다.
                // binding.<뷰 ID>로 뷰 요소에 접근할 수 있습니다.
            }

//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//
//                binding = T.bind(itemView);
//
//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        final int position = getBindingAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            if(onItemClickListener != null) {
//                                onItemClickListener.onItemClick(view, position);
//                                onItemClickListener.onItemLongClick(view, position);
//                            }
//                        }
//                    }
//                });
//
//            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(parent.getContext()).inflate((R.layout.text_row_item),parent,false);
            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        }


        @Override
        public int getItemCount() {
            return dataList.size();
        }


    }

}
