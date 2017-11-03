package com.realcloud.media.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.realcloud.loochadroid.LoochaApplication;
import com.realcloud.loochadroid.fragmentation.BaseSupportFragment;
import com.realcloud.loochadroid.utils.ConvertUtil;
import com.realcloud.media.R;
import com.realcloud.media.listener.MediaSelectListener;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Created by zack on 2017/10/11.
 */

public class MianViewPagerFragment extends BaseSupportFragment implements ViewPager.OnPageChangeListener,View.OnClickListener{

    private static final int MOVE_LENGTH = ConvertUtil.convertDpToPixel(60);
    private static final int PAGER_CAPTURE = 0;
    private static final int PAGER_IMAGE = 1;

    //媒体文件处理listener
    private MediaSelectListener mediaSelectListener;

    //当前滑动界面
    private ViewPager viewPager;

    //相册view
    private TextView album;

    //拍摄view
    private TextView capture;

    //底部view
    private View mFooter;

    private MainPagerListener mainPagerListener = new MainPagerListener() {
        @Override
        public void jumpToCapture() {
            viewPager.setCurrentItem(PAGER_CAPTURE,true);
        }

        @Override
        public void jumpToPhoto() {
            viewPager.setCurrentItem(PAGER_IMAGE,true);
        }

        @Override
        public void setFootVisibility(int visibility) {
            mFooter.setVisibility(visibility);
        }

        @Override
        public void startFragment(ISupportFragment toFragment) {
            start(toFragment);
        }

        @Override
        public void startFragmentForResult(ISupportFragment toFragment,int requestCode) {
            startForResult(toFragment,requestCode);
        }
    };

    public static MianViewPagerFragment newInstance(MediaSelectListener mediaSelectListener) {
        Bundle args = new Bundle();
        MianViewPagerFragment fragment = new MianViewPagerFragment();
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
        View view = inflater.inflate(R.layout.layout_main_pager_view,container,false);
        mFooter = view.findViewById(R.id.id_footer);
        album = view.findViewById(R.id.id_album);
        album.setOnClickListener(this);
        capture = view.findViewById(R.id.id_capture);
        capture.setOnClickListener(this);
        viewPager = view.findViewById(R.id.id_pager);
        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(new MainPagerAdapter(getFragmentManager(),mediaSelectListener,mainPagerListener));
        viewPager.setCurrentItem(1);
        return view;
    }

    /**
     * 当切换回当前页面时需要调用通知子fragment
     */
    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (viewPager.getCurrentItem() == PAGER_CAPTURE){
            BaseSupportFragment  captureFragment= findFragment(CaptureFragment.class);
            if (captureFragment != null){
                captureFragment.onSupportVisible();
            }
        }else if (viewPager.getCurrentItem() == PAGER_IMAGE){
            BaseSupportFragment  imageFragment= findFragment(ImageFragment.class);
            if (imageFragment != null){
                imageFragment.onSupportVisible();
            }
        }
    }

    /**
     * 当离开当前页面时需要调用通知子fragment
     */
    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (viewPager.getCurrentItem() == PAGER_CAPTURE){
            BaseSupportFragment  captureFragment= findFragment(CaptureFragment.class);
            if (captureFragment != null){
                captureFragment.onSupportInvisible();
            }
        }else if (viewPager.getCurrentItem() == PAGER_IMAGE){
            BaseSupportFragment  imageFragment= findFragment(ImageFragment.class);
            if (imageFragment != null){
                imageFragment.onSupportInvisible();
            }
        }
    }

    @Override
    public void onPageScrolled(int i, float per, int i1) {
        float percent = i1 *1f / LoochaApplication.getScreenWidth();
        float length = percent * MOVE_LENGTH;
        if (length > 0){
            mFooter.scrollTo((int) length, 0);
        }
    }

    @Override
    public void onPageSelected(int i) {
        if (i == PAGER_CAPTURE){ //当切换tab时需要切换文字颜色
            album.setTextColor(Color.parseColor("#fffffe"));
            capture.setTextColor(Color.parseColor("#ffde08"));
            mFooter.scrollTo(0, 0);
        }else if (i == PAGER_IMAGE){
            album.setTextColor(Color.parseColor("#ffde08"));
            capture.setTextColor(Color.parseColor("#fffffe"));
            mFooter.scrollTo(MOVE_LENGTH, 0);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.id_album){
            if (viewPager.getCurrentItem() == PAGER_CAPTURE){
                viewPager.setCurrentItem(PAGER_IMAGE,true);
            }
        }else if (view.getId() == R.id.id_capture){
            if (viewPager.getCurrentItem() == PAGER_IMAGE){
                viewPager.setCurrentItem(PAGER_CAPTURE,true);
            }
        }
    }

    /**
     * 对应fragmnet的集合
     */
    static class MainPagerAdapter extends FragmentPagerAdapter{

        private MediaSelectListener mediaSelectListener;
        private MainPagerListener mainPagerListener;

        public MainPagerAdapter(FragmentManager fm,MediaSelectListener mediaSelectListener,MainPagerListener mainPagerListener) {
            super(fm);
            this.mediaSelectListener = mediaSelectListener;
            this.mainPagerListener = mainPagerListener;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return CaptureFragment.newInstance(mediaSelectListener,mainPagerListener);
            } else {
                return ImageFragment.newInstance(mediaSelectListener,mainPagerListener);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public interface MainPagerListener{
        void jumpToCapture();
        void jumpToPhoto();
        void setFootVisibility(int visibility);
        void startFragment(ISupportFragment toFragment);
        void startFragmentForResult(ISupportFragment toFragment,int requestCode);
    }
}
