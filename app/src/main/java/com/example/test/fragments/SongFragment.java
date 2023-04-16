package com.example.test.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.adapters.CursorRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SongFragment extends Fragment {

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private static final String TAG = SongFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public SongFragment() {
        // Required empty public constructor
    }

    public class MessageEvent{
        public String title;
        public String artist;
        public byte[] imageData;
        public long duration;

        public Uri uri;

        public MessageEvent(String title, String artist, byte[] imageData, long duration, Uri uri) {
            this.title = title;
            this.artist = artist;
            this.imageData = imageData;
            this.duration = duration;
            this.uri = uri;
        }
    }

    public static SongFragment newInstance(String param1, String param2) {
        SongFragment fragment = new SongFragment();
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
        return inflater.inflate(R.layout.fragment_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // 레이아웃 설정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        String[] projection = {
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID};

        /**
         * Cursor
         */
        Cursor cursor = getActivity().getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);

        SongRecyclerAdapter adapter = new SongRecyclerAdapter(getActivity(), cursor);
        recyclerView.setAdapter(adapter);

    }

    private class SongRecyclerAdapter extends CursorRecyclerViewAdapter<SongRecyclerAdapter.ViewHolder> {

        private Context mContext;

        public SongRecyclerAdapter(Context context, Cursor cursor) {
            super(context, cursor);
            mContext = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.song_row_item, parent, false);

            return new ViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
            int uriIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String uriString = cursor.getString(uriIndex);
            final Uri uri = Uri.parse(uriString);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mContext, uri);

            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            @SuppressLint("Range") String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            @SuppressLint("Range") long mDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

            // 미디어 정보
//            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            // 오디오 앨범 이미지
            byte imageData[] = retriever.getEmbeddedPicture();
//            if(albumImage != null){
//                Bitmap bitmap = BitmapFactory.decodeByteArray(albumImage, 0 , albumImage.length);
//            }

            viewHolder.textViewTitle.setText(title);
            viewHolder.textViewArtist.setText(artist);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /**
                     * {@link com.example.test.activities.MusicPlayerActivity#playMusic(MessageEvent) }
                     * {@link com.example.test.fragments.MusicControllerFragment#updateUI(MessageEvent) }
                     */
                    EventBus.getDefault().post(new MessageEvent(title, artist, imageData, mDuration, uri));
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView textViewTitle;
            public TextView textViewArtist;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {

                        }
                    }
                });

                textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                textViewArtist = (TextView) itemView.findViewById(R.id.textViewArtist);
            }
        }
    }
}