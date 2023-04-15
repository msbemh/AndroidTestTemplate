package com.example.test.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.MainActivity;
import com.example.test.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GalleryFragment extends Fragment {
    private static final String TAG = GalleryFragment.class.getSimpleName();

    private GridView mGridView;
    private MyCursorAdapter adapter;

    private onFragmentInteractionListener mListener;

    public GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * 필요한 권한
     */
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    /**
     * 권한 요청에 대한 Callback
     */
    private ActivityResultLauncher activityResultLauncher =  registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            Log.d(TAG, ""+result.toString());

            Boolean areAllGranted = true;
            // 모든 권한에 동의 했는지 확인
            for(Boolean b : result.values()) {
                areAllGranted = areAllGranted && b;
            }

            // 모든 권한에 동의
            if(areAllGranted) {
                if(getView() != null){
                    loadImage(getView());
                }
            // 모든 권한에 동의 하지 않음
            }else{
                getActivity().finish();
            }
        }
    });


    // 이미지 로드 메서드
    private void loadImage(View view) {
        mGridView = (GridView) view.findViewById(R.id.gridView);
        /**
         * 사진 정보
         * 미디어(사진, 동영상, 음악) media db
         * provider 로 media db 정보를 가져와야 됨
         */
        Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );

        Log.d(TAG, "count:" + cursor.getCount());

        /**
         * 사진을 뿌릴 어댑터
         */
        adapter = new MyCursorAdapter(getActivity(), cursor);
        mGridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    // 액티비티와 상호작용 하기 위한 인터페이스
    public interface onFragmentInteractionListener{
        public void onFragmentInteraction(Uri uri);
    }

    // 프레그먼트 생성 팩토리
    public static GalleryFragment newInstance(String param1, String param2) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 리스너 연결
        if(context instanceof onFragmentInteractionListener){
            mListener = (onFragmentInteractionListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + "onFragmentInteractionListener 를 구현 해야 합니다.");
        }

        /**
         * 권한이 없을 경우에는 권한을 요청한다.
         */
        if (!checkPermissions()) {
            this.activityResultLauncher.launch(PERMISSIONS);
        }
    }

    private boolean checkPermissions(){
        // 허용 되지 않은 권한이 있는지 체크
        for(String permission : PERMISSIONS){
            if(ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bundle 받을 거임
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 뷰 가져오기
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 뷰 가져온 이후 할 것
        loadImage(view);
    }

    private class MyCursorAdapter extends CursorAdapter{

        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_gallery, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);

            return convertView;
        }

        // cursor 가 자동으로 next 되어져서 와진다.
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            Log.d(TAG, "path:" + path);
            Log.d(TAG, "Uri.parse(path):" + Uri.parse(path));
            viewHolder.imageView.setImageURI(Uri.parse(path));
        }

        private class ViewHolder {
            ImageView imageView;
        }
    }
}