package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.adapters.CursorRecyclerViewAdapter;
import com.example.test.models.Song;
import com.example.test.models.SongListMessage;
import com.example.test.services.MusicService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SongFragment extends Fragment {

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private static final String TAG = SongFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public List<Song> songList = new ArrayList<>();
    public SongListMessage songListMessage;
    private Boolean mBound;
    private MusicService mService;

    private String mParam1;
    private String mParam2;

    public SongFragment() {
        // Required empty public constructor
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
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST};

        /**
         * Cursor
         */
        Cursor cursor = getActivity().getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        /**
         * 가져온 Audio 정보 SongList에 담기
         */
        while (cursor.moveToNext()) {
            Song song = getSongFromCursor(cursor);
            // 오디오 파일 정보 출력 (예시)
            songList.add(song);
        }
        // 서비스로 List<Song>을 넘겨주기 위한 Wrapper용 객체
        songListMessage = new SongListMessage(songList);

        SongRecyclerAdapter adapter = new SongRecyclerAdapter(getActivity(), cursor);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(connection);
        mBound = false;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    /**
     * Cursor로 부터 Song 객체를 얻어온다.
     */
    private Song getSongFromCursor(Cursor cursor){
        // 오디오 파일 정보 추출
        @SuppressLint("Range") String audioId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
        @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        @SuppressLint("Range") String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        @SuppressLint("Range") String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

        Uri uri = Uri.parse(filePath);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getActivity(), uri);
        byte imageData[] = retriever.getEmbeddedPicture();

        title = (title == null ? "타이틀" : title);
        artist = "<unknown>".equals(artist) ? "[아티스트 없음]" : artist;

        return new Song(audioId, title, artist, imageData, duration, uri);
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
            @SuppressLint("Range") String audioId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            Song song = songList.stream().filter(s->s.getAudioId().equals(audioId)).findAny().orElse(null);

            viewHolder.textViewTitle.setText(song.title);
            viewHolder.textViewArtist.setText(song.artist);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, MusicService.class);
                    intent.setAction(MusicService.ACTION_PLAY);
                    intent.putExtra("song", (Parcelable) song);
                    // songList는 1번만 보낸다.
                    if(mService != null && mService.songList == null ){
                        intent.putExtra("songList", (Parcelable) songListMessage);
                    }
                    mContext.startService(intent);
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView textViewTitle;
            public TextView textViewArtist;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                textViewArtist = (TextView) itemView.findViewById(R.id.textViewArtist);
            }
        }
    }
}