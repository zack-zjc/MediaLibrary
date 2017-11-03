package com.realcloud.media.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.realcloud.loochadroid.fragmentation.BaseSupportFragment;
import com.realcloud.media.MediaConstant;
import com.realcloud.media.R;
import com.realcloud.media.adapter.AlbumAdapter;
import com.realcloud.media.listener.MediaSelectListener;
import com.realcloud.media.loader.AlbumLoader;
import com.realcloud.media.loader.VideoAlbumLoader;
import com.realcloud.media.model.Album;

import java.util.List;

/**
 * Created by zack on 2017/9/29.
 */

public class AlbumFragment extends BaseSupportFragment implements View.OnClickListener {

    private static final int PhotoLoaderId = R.id.album_id;

    //媒体文件处理listener
    private MediaSelectListener mediaSelectListener;

    private AlbumAdapter mAlbumAdapter;

    //监听loader结束时设置数据
    private AlbumLoader.OnAlbumLoaderListener albumLoaderListener = new AlbumLoader.OnAlbumLoaderListener() {
        @Override
        public void onAlbumLoaderFinish(List<Album> list) {
            mAlbumAdapter.setData(list);
        }
    };

    //监听点击列表时处理跳转
    private AlbumAdapter.AlbumListener albumListener = new AlbumAdapter.AlbumListener() {
        @Override
        public void jumpFragment(Album album) {
            Bundle bundle = new Bundle();
            bundle.putString(MediaConstant.ALBUMID,String.valueOf(album.getBucketId()));
            bundle.putString(MediaConstant.ALBUMNAME,album.getDisplayName());
            BaseSupportFragment  imageFragment= findFragment(ImageFragment.class);
            if (imageFragment != null){
                imageFragment.onFragmentResult(getTargetRequestCode(),RESULT_OK,bundle);
            }
            pop();
        }
    };

    public static AlbumFragment newInstance(MediaSelectListener mediaSelectListener) {
        Bundle args = new Bundle();
        AlbumFragment fragment = new AlbumFragment();
        fragment.setMediaSelectListener(mediaSelectListener);
        fragment.setArguments(args);
        return fragment;
    }

    public void setMediaSelectListener(MediaSelectListener mediaSelectListener){
        this.mediaSelectListener = mediaSelectListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_album_view,null);
        ImageView imageView = view.findViewById(R.id.id_back);
        imageView.setOnClickListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.id_album_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAlbumAdapter = new AlbumAdapter(getContext(),albumListener);
        recyclerView.setAdapter(mAlbumAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlbumLoader albumLoader = null;
        if (mediaSelectListener != null){
            if (mediaSelectListener.getSelectType() ==MediaConstant.SELECT_PICTURE){ //只选图片
                albumLoader = new AlbumLoader(getContext(),albumLoaderListener,true);
            }else if (mediaSelectListener.getSelectType() ==MediaConstant.SELECT_VIDEO){ //只选视频
                albumLoader = new VideoAlbumLoader(getContext(),albumLoaderListener);
            }
        }
        if (albumLoader == null){
            albumLoader = new AlbumLoader(getContext(),albumLoaderListener);
        }
        getLoaderManager().restartLoader(PhotoLoaderId,null,albumLoader);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.id_back){
            getActivity().onBackPressed();
        }
    }
}
