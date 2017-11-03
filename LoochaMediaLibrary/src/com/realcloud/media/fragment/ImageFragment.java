package com.realcloud.media.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.realcloud.loochadroid.fragmentation.BaseSupportFragment;
import com.realcloud.loochadroid.utils.ConvertUtil;
import com.realcloud.media.MediaConstant;
import com.realcloud.media.R;
import com.realcloud.media.adapter.PhotoAdapter;
import com.realcloud.media.listener.MediaSelectListener;
import com.realcloud.media.loader.PhotoLoader;
import com.realcloud.media.loader.VideoLoader;
import com.realcloud.media.model.Album;
import com.realcloud.media.model.Photo;

import java.util.List;


/**
 * Created by zack on 2017/9/29.
 */

public class ImageFragment extends BaseSupportFragment implements View.OnClickListener{

    private static final int PhotoLoaderId = R.id.phto_id;

    //主页面回调事件
    private MianViewPagerFragment.MainPagerListener mainPagerListener;

    //完成选择按钮
    private TextView complete;

    //预览按钮
    private TextView preview;

    //媒体文件处理listener
    private MediaSelectListener mediaSelectListener;

    private PhotoAdapter photoAdapter;

    //相册名称
    private TextView mAlbumName;

    //监听loader结束时设置数据
    private PhotoLoader.OnPhotoLoaderListener onPhotoLoaderListener = new PhotoLoader.OnPhotoLoaderListener(){

        @Override
        public void onPhotoLoaderFinish(List<Photo> list) {
            photoAdapter.setData(list);
        }
    };

    //处理在选择图片是点击图片处理
    private PhotoAdapter.PhotoListener photoListener = new PhotoAdapter.PhotoListener() {
        @Override
        public void jumpPhoto(Photo photo) {
            //判断是否是图片
            if (photo.isImage()){
                if (mediaSelectListener != null && mediaSelectListener.isSingleSelect()){ //如果是单选则直接跳转结束
                    mediaSelectListener.selectMedia(photo);
                    mediaSelectListener.selectComplete();
                }else{
                    //跳转到查看详细图片界面
                    BaseSupportFragment imageViewPagerFragment = findFragment(ImageViewPagerFragment.class);
                    if (imageViewPagerFragment == null) {
                        imageViewPagerFragment = ImageViewPagerFragment.newInstance(mediaSelectListener);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString(MediaConstant.ALBUMID,String.valueOf(photo.getAlbumId()));
                    bundle.putSerializable(MediaConstant.PHOTO,photo);
                    imageViewPagerFragment.setArguments(bundle);
                    if (mainPagerListener != null){
                        mainPagerListener.startFragment(imageViewPagerFragment);
                    }
                }
            }else {
                //跳转到视频查看界面
                BaseSupportFragment videoDetailFragment = findFragment(VideoDetailFragment.class);
                if (videoDetailFragment == null) {
                    videoDetailFragment = VideoDetailFragment.newInstance(mediaSelectListener);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(MediaConstant.PHOTO,photo);
                videoDetailFragment.setArguments(bundle);
                if (mainPagerListener != null){
                    mainPagerListener.startFragmentForResult(videoDetailFragment,MediaConstant.REQUEST_ALBUM);
                }
            }
        }
        //选择图片
        @Override
        public void selectPhoto(Photo photo) {
            if (mediaSelectListener != null){
                if (mediaSelectListener.isMediaSelect(photo)){
                    mediaSelectListener.getSelectPhoto().remove(photo);
                }else{
                    mediaSelectListener.selectMedia(photo);
                }
            }
            updateView();
        }

        //判断图片是否被选择
        @Override
        public boolean isPhotoSelect(Photo photo) {
            return mediaSelectListener != null && mediaSelectListener.getSelectPhoto().contains(photo);
        }

        //判断视频是否可以选择
        @Override
        public boolean canVideoSelect() {
            return mediaSelectListener != null && mediaSelectListener.getSelectPhoto().isEmpty();
        }
    };

    public static ImageFragment newInstance(MediaSelectListener mediaSelectListener,MianViewPagerFragment.MainPagerListener mainPagerListener) {
        Bundle args = new Bundle();
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setMediaSelectListener(mediaSelectListener);
        fragment.setMainPagerListener(mainPagerListener);
        return fragment;
    }

    public void setMediaSelectListener(MediaSelectListener mediaSelectListener){
        this.mediaSelectListener = mediaSelectListener;
    }

    public void setMainPagerListener(MianViewPagerFragment.MainPagerListener mainPagerListener){
        this.mainPagerListener = mainPagerListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_grid_view,null);
        preview = view.findViewById(R.id.id_preview);
        preview.setOnClickListener(this);
        complete = view.findViewById(R.id.id_complete);
        complete.setOnClickListener(this);
        TextView back = view.findViewById(R.id.id_back);
        back.setOnClickListener(this);
        mAlbumName = view.findViewById(R.id.id_title);
        mAlbumName.setOnClickListener(this);
        RecyclerView recyclerView = view.findViewById(R.id.id_grid_view);
        recyclerView.addItemDecoration(new SpaceItemDecoration(ConvertUtil.convertDpToPixel(3)));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        photoAdapter = new PhotoAdapter(getContext(),photoListener,mediaSelectListener.isSingleSelect());
        recyclerView.setAdapter(photoAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateView();
        updateLoader();
    }

    /**
     * 从其他页面返回需要确定当前页面状态
     */
    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        updateView();
    }

    public void updateAdapterView(){
        photoAdapter.notifyDataSetChanged();
    }

    /**
     * 更新展示文字等信息
     */
    private void updateView(){
        if (mediaSelectListener != null){
            complete.setEnabled(!mediaSelectListener.getSelectPhoto().isEmpty());
            int count = mediaSelectListener.getSelectPhoto().size();
            int maxcount = mediaSelectListener.getMaxSelectCount();
            complete.setText(count > 0 ? getResources().getString(R.string.str_complete_with_count,String.valueOf(count),String.valueOf(maxcount))
                    : getResources().getString(R.string.str_complete));
            preview.setEnabled(!mediaSelectListener.getSelectPhoto().isEmpty());
        }
    }

    /**
     * 更新相册内图片（切换相册等）
     */
    private void updateLoader(){
        Bundle arguments = getArguments();
        mAlbumName.setText(arguments.getString(MediaConstant.ALBUMNAME,getResources().getString(R.string.str_all_photo_and_video)));
        if (mediaSelectListener.getSelectType() == MediaConstant.SELECT_VIDEO){
            mAlbumName.setText(getResources().getString(R.string.str_all_video));
        }
        Bundle bundle = new Bundle();
        bundle.putString(MediaConstant.ALBUMID,arguments.getString(MediaConstant.ALBUMID,String.valueOf(Album.ALL_PHOTO_VIDEO)));
        PhotoLoader photoLoader = null;
        if (mediaSelectListener != null){
            if (mediaSelectListener.getSelectType() == MediaConstant.SELECT_VIDEO){
                photoLoader = new VideoLoader(getContext(),onPhotoLoaderListener);
            }else if (mediaSelectListener.getSelectType() == MediaConstant.SELECT_PICTURE){
                photoLoader = new PhotoLoader(getContext(),onPhotoLoaderListener,true);
            }
        }
        if (photoLoader == null){
            photoLoader = new PhotoLoader(getContext(),onPhotoLoaderListener);
        }
        getLoaderManager().restartLoader(PhotoLoaderId,bundle,photoLoader);
    }

    /**
     * 列表分割线
     */
    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.bottom = space;
            outRect.right = space;
            outRect.top = space;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.id_back){
            getActivity().finish();
        }else if (view.getId() == R.id.id_preview){
            //跳转到查看详细图片界面
            BaseSupportFragment imageViewPagerFragment = findFragment(ImageViewPagerFragment.class);
            if (imageViewPagerFragment == null) {
                imageViewPagerFragment = ImageViewPagerFragment.newInstance(mediaSelectListener);
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean(MediaConstant.PHOTO_FROM,false);
            imageViewPagerFragment.setArguments(bundle);
            if (mainPagerListener != null){
                mainPagerListener.startFragment(imageViewPagerFragment);
            }
        }else if (view.getId() == R.id.id_complete){
            //完成
            if (mediaSelectListener != null){
                mediaSelectListener.selectComplete();
            }
        }else if (view.getId() == R.id.id_title){
            //跳转相册选择界面
            BaseSupportFragment albumFragment = findFragment(AlbumFragment.class);
            if (albumFragment == null) {
                albumFragment = AlbumFragment.newInstance(mediaSelectListener);
            }
            if (mainPagerListener != null){
                mainPagerListener.startFragment(albumFragment);
            }
        }
    }

    /**
     * 切换相册切换相册数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        if (requestCode == MediaConstant.REQUEST_ALBUM && resultCode == RESULT_OK && data != null){
            setArguments(data);
            updateLoader();
        }
    }
}
