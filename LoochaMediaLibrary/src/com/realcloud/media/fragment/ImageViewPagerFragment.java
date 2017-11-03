package com.realcloud.media.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liu.hz.view.RecyclerViewPager;
import com.realcloud.loochadroid.fragmentation.BaseSupportFragment;
import com.realcloud.loochadroid.utils.ConvertUtil;
import com.realcloud.media.MediaConstant;
import com.realcloud.media.R;
import com.realcloud.media.adapter.PhotoDetailAdapter;
import com.realcloud.media.adapter.PhotoSelectAdapter;
import com.realcloud.media.listener.MediaSelectListener;
import com.realcloud.media.loader.PictureLoader;
import com.realcloud.media.model.Photo;

import java.util.List;

/**
 * Created by zack on 2017/9/29.
 */

public class ImageViewPagerFragment extends BaseSupportFragment implements View.OnClickListener{

    private static final int PhotoLoaderId = R.id.picture_id;

    //点击做动画隐藏的头部和底部
    private View headerView;
    private View footerView;

    //媒体文件处理listener
    private MediaSelectListener mediaSelectListener;

    //展示的标题
    private TextView title;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    //循环滑动viewpager
    private RecyclerViewPager recyclerViewPager;

    //底部已选择recyclerview
    private RecyclerView mSelectImagePager;

    //所有图片的adpter
    private PhotoDetailAdapter mPhotoDetailAdapter;

    //底部已经选择的图片的adpter
    private PhotoSelectAdapter mPhotoSelectAdapter;

    //完成选择按钮
    private TextView complete;

    //当前图片是否被选择
    private CheckBox mStatusBox;

    private View.OnClickListener onSelectItenClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Photo photo = (Photo) view.getTag();
            final int index = mPhotoDetailAdapter.getPhotoPosition(photo);
            //滑动到对应页面
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    recyclerViewPager.scrollToPosition(index);
                }
            });
        }
    };

    //点击时显示或隐藏头部和底部
    private View.OnClickListener onViewPagerItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            animationHeaderAndFooter();
        }
    };

    //当滑动图片时切换标题
    private RecyclerViewPager.OnPageChangedListener onPageChangedListener = new RecyclerViewPager.OnPageChangedListener() {
        @Override
        public void OnPageChanged(int oldPosition, int newPosition) {
            updatePosition(newPosition);
        }
    };

    //图片加载完成回调
    private PictureLoader.OnPictureLoaderListener pictureLoaderListener = new PictureLoader.OnPictureLoaderListener() {
        @Override
        public void onPictureLoaderFinish(List<Photo> list) {
            setViewPagerList(list);
        }
    };

    public static ImageViewPagerFragment newInstance(MediaSelectListener mediaSelectListener) {
        Bundle args = new Bundle();
        ImageViewPagerFragment fragment = new ImageViewPagerFragment();
        fragment.setArguments(args);
        fragment.setMediaSelectListener(mediaSelectListener);
        return fragment;
    }

    public void setMediaSelectListener(MediaSelectListener mediaSelectListener){
        this.mediaSelectListener = mediaSelectListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_image_pager_view,null);
        headerView = view.findViewById(R.id.id_layout_header);
        headerView.setOnClickListener(this); //不处理只是做截取点击事件
        footerView = view.findViewById(R.id.id_footer);
        footerView.setOnClickListener(this); //不处理只是做截取点击事件
        recyclerViewPager = view.findViewById(R.id.id_view_pager);
        mPhotoDetailAdapter = new PhotoDetailAdapter(getContext());
        mPhotoDetailAdapter.setOnItemClickListener(onViewPagerItemClickListener);
        recyclerViewPager.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recyclerViewPager.setAdapter(mPhotoDetailAdapter);
        recyclerViewPager.addOnPageChangedListener(onPageChangedListener);
        mSelectImagePager = view.findViewById(R.id.id_select_view);
        mPhotoSelectAdapter = new PhotoSelectAdapter(getContext());
        mPhotoSelectAdapter.setOnItemClickListener(onSelectItenClickListener);
        mSelectImagePager.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        mSelectImagePager.setAdapter(mPhotoSelectAdapter);
        TextView edit = view.findViewById(R.id.id_edit);
        edit.setOnClickListener(this);
        mStatusBox = view.findViewById(R.id.id_check);
        mStatusBox.setOnClickListener(this);
        Drawable checkDrawable = getResources().getDrawable(R.drawable.bg_checkbox_selector);
        checkDrawable.setBounds(0, 0, ConvertUtil.convertDpToPixel(20), ConvertUtil.convertDpToPixel(20));
        mStatusBox.setCompoundDrawables(checkDrawable,null,null,null);
        title = view.findViewById(R.id.id_title);
        ImageView back = view.findViewById(R.id.id_back);
        back.setOnClickListener(this);
        complete = view.findViewById(R.id.id_complete);
        complete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateView();
        if (mediaSelectListener != null){
            mPhotoSelectAdapter.setDatas(mediaSelectListener.getSelectPhoto());
        }
        Bundle arguments = getArguments();
        boolean  fromAlbum = arguments.getBoolean(MediaConstant.PHOTO_FROM,true);
        String albumId = arguments.getString(MediaConstant.ALBUMID,"");
        //判断当前是预览已选择的图片还是查看所有图片
        if (fromAlbum && !TextUtils.isEmpty(albumId)){
            Bundle bundle = new Bundle();
            bundle.putString(MediaConstant.ALBUMID,albumId);
            PictureLoader pictureLoader = new PictureLoader(getContext(),pictureLoaderListener);
            getLoaderManager().restartLoader(PhotoLoaderId,bundle,pictureLoader);
        }else{
            if (mediaSelectListener != null){
                setViewPagerList(mediaSelectListener.getSelectPhoto());
            }
        }
    }

    /**
     * 设置上面viewpager显示的图片
     * @param list 数据
     */
    private void setViewPagerList(List<Photo> list){
        Bundle arguments = getArguments();
        Photo photo = (Photo) arguments.getSerializable(MediaConstant.PHOTO);
        mPhotoDetailAdapter.setData(list);
        final int index = photo != null ? list.indexOf(photo) : 0;
        //滑动到对应页面
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String titleText = (index+1) +"/"+ mPhotoDetailAdapter.getBaseItemCount();
                title.setText(titleText);
                recyclerViewPager.scrollToPosition(index >= 0 ? index : 0);
            }
        });
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
            mSelectImagePager.setVisibility(count > 0 ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * 滑动viewpager更新界面
     * @param newPosition
     */
    private void updatePosition(int newPosition){
        String titleText = (newPosition+1) +"/"+ mPhotoDetailAdapter.getBaseItemCount();
        title.setText(titleText);
        Photo photo = mPhotoDetailAdapter.getPhoto(newPosition);
        updatePhotoStatus(photo);
        int position = mPhotoSelectAdapter.getPhotoPosition(photo);
        if (position > 0){
            mSelectImagePager.smoothScrollToPosition(position);
        }
        mPhotoSelectAdapter.setCurrentPhoto(photo);
    }

    /**
     * 更具当前photo更新checkbox状态
     * @param photo 数据
     */
    private void updatePhotoStatus(Photo photo){
        if (mediaSelectListener != null){
            mStatusBox.setChecked(mediaSelectListener.isMediaSelect(photo));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.id_back){
            updateImageFragment();
            getActivity().onBackPressed();
        }else if (view.getId() == R.id.id_edit){
            Photo photo = mPhotoDetailAdapter.getPhoto(recyclerViewPager.getCurrentPosition());
            BaseSupportFragment editFragment = findFragment(EditFragment.class);
            if (editFragment == null) {
                editFragment = EditFragment.newInstance(mediaSelectListener);
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(MediaConstant.PHOTO_CONTENT,photo);
            editFragment.setArguments(bundle);
            startForResult(editFragment,MediaConstant.REQUEST_EDIT);
        }else if (view.getId() == R.id.id_complete){
            //完成
            if (mediaSelectListener != null){
                mediaSelectListener.selectComplete();
            }
        }else if (view.getId() == R.id.id_check){
            //获取当前photo
            Photo photo = mPhotoDetailAdapter.getPhoto(recyclerViewPager.getCurrentPosition());
            if (mediaSelectListener != null){
                if (mediaSelectListener.getSelectPhoto().size() >= mediaSelectListener.getMaxSelectCount()){
                    Toast.makeText(getContext(),R.string.str_max_count,Toast.LENGTH_SHORT).show();
                    return ;
                }
                if (mediaSelectListener.isMediaSelect(photo)){
                    //如果选择了删除媒体文件
                   mediaSelectListener.unSelectMedia(photo);
                    //适配器删除对应条目
                   mPhotoSelectAdapter.removeData(photo);
                }else { //添加媒体文件
                    mediaSelectListener.selectMedia(photo);
                    //适配器添加对应条目
                    mPhotoSelectAdapter.addData(photo);
                }
            }
            updatePhotoStatus(photo);
            updateView();
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        if (resultCode == Activity.RESULT_OK && requestCode == MediaConstant.REQUEST_EDIT){
            Photo photo = (Photo) data.getSerializable(MediaConstant.PHOTO_CONTENT);
            if (mediaSelectListener != null && photo != null){
                int position = mPhotoDetailAdapter.getPhotoPosition(photo);
                mPhotoDetailAdapter.notifyItemChanged(position);
                if(mediaSelectListener.isMediaSelect(photo)){
                    updatePhotoStatus(photo);
                    updateView();
                    int selectedPosition = mPhotoSelectAdapter.getPhotoPosition(photo);
                    mPhotoSelectAdapter.notifyItemChanged(selectedPosition);
                }else{
                    //首先判断是否可选
                    if (mediaSelectListener.getSelectPhoto().size() >= mediaSelectListener.getMaxSelectCount()){
                        Toast.makeText(getContext(),R.string.str_max_count,Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    mediaSelectListener.selectMedia(photo);
                    updatePhotoStatus(photo);
                    updateView();
                    //适配器添加对应条目
                    mPhotoSelectAdapter.addData(photo);
                }
            }
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        updateImageFragment();
        return super.onBackPressedSupport();
    }

    /**
     * 点击时隐藏或显示头部和底部
     */
    private void animationHeaderAndFooter(){
        int visibility = headerView.getVisibility();
        if (visibility == View.VISIBLE){
            headerView.setVisibility(View.INVISIBLE);
            footerView.setVisibility(View.INVISIBLE);
            mSelectImagePager.setVisibility(View.INVISIBLE);
        }else{
            headerView.setVisibility(View.VISIBLE);
            footerView.setVisibility(View.VISIBLE);
            int count = mediaSelectListener.getSelectPhoto().size();
            mSelectImagePager.setVisibility(count > 0 ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * 需要更新图片列表时调用
     */
    private void updateImageFragment(){
        ImageFragment  imageFragment= findFragment(ImageFragment.class);
        if (imageFragment != null){
            imageFragment.updateAdapterView();
        }
    }

}
