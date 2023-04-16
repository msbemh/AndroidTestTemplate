package com.example.test.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.models.Song;

import java.util.ArrayList;
import java.util.List;

public class ArtistFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private List<Song> mData;

    public ArtistFragment() {
        // Required empty public constructor
    }

    public static ArtistFragment newInstance(String param1, String param2) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // 데이터
        mData = new ArrayList<Song>();

        // 임시 데이터 설정
        for (int i = 0; i < 50; i++){
            mData.add(new Song("제목" + i, "아티스트" + i));
        }

        // 레이아웃 설정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArtistFragment.MyRecyclerAdapter adapter = new ArtistFragment.MyRecyclerAdapter(mData);
        recyclerView.setAdapter(adapter);

    }

    private class MyRecyclerAdapter extends RecyclerView.Adapter<ArtistFragment.MyRecyclerAdapter.ViewHolder>{

        private final List<Song> mData;

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param mData String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        private MyRecyclerAdapter(List<Song> mData) {
            this.mData = mData;
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ArtistFragment.MyRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.artist_row_item, parent, false);
            return new ArtistFragment.MyRecyclerAdapter.ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull ArtistFragment.MyRecyclerAdapter.ViewHolder holder, int position) {
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            holder.textViewArtist.setText(mData.get(position).getArtist());
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView textViewArtist;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                // Define click listener for the ViewHolder's View
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {

                        }
                    }
                });

                textViewArtist = (TextView) itemView.findViewById(R.id.textViewArtist);
            }
        }
    }
}