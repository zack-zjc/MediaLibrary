package com.realcloud.media;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.realcloud.loochadroid.fragmentation.BaseFragmentWrapperActivity;
import com.realcloud.loochadroid.fragmentation.BaseSupportFragment;
import com.realcloud.media.fragment.CutSingleFragment;
import com.realcloud.media.fragment.MianViewPagerFragment;
import com.realcloud.media.listener.MediaSelectListener;
import com.realcloud.media.model.Photo;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by zack on 2017/9/29.
 */

public class ActMediaStore extends BaseFragmentWrapperActivity implements MediaSelectListener {

    //默认最大选择个数
    private static final int MAX_COUNT = 9;
    //选择媒体存储区域
    private ArrayList<Photo> mediaList = new ArrayList<>();
    //媒体文件选择类型
    private int mSelectType = MediaConstant.SELECT_ALL;
    //可选图片个数
    private int mSelectCount = MAX_COUNT;
    //裁剪x和y比例（目前只有单张需要裁剪比例）
    private int ratioX,ratioY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_media_store);
        mSelectType = getIntent().getIntExtra(MediaConstant.PICK_TYPE,MediaConstant.SELECT_ALL);
        mSelectCount = getIntent().getIntExtra(MediaConstant.PICK_COUNT,MAX_COUNT);
        ratioX = getIntent().getIntExtra(MediaConstant.RATIO_X,0);
        ratioY = getIntent().getIntExtra(MediaConstant.RATIO_Y,0);
        BaseSupportFragment  mainFragment= findFragment(MianViewPagerFragment.class);
        if (mainFragment == null) {
            mainFragment = MianViewPagerFragment.newInstance(this);
        }
        loadRootFragment(R.id.id_layout_container, mainFragment);
    }

    @Override
    protected void initWindowFeature() {
        super.initWindowFeature();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#333333"));
            window.setNavigationBarColor(Color.BLACK);
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            ActivityCompat.finishAfterTransition(this);
        }
    }

    /**
     * 选择图片回调
     * @param photo
     */
    @Override
    public void selectMedia(Photo photo) {
        if (!mediaList.contains(photo)){
            if (mediaList.size() >= mSelectCount){
                Toast.makeText(this,R.string.str_max_count,Toast.LENGTH_SHORT).show();
            }else{
                mediaList.add(photo);
            }
        }
    }

    /**
     * 取消选择图片回调
     * @param photo
     */
    @Override
    public void unSelectMedia(Photo photo) {
        if (mediaList.contains(photo)){
            mediaList.remove(photo);
        }
    }

    /**
     * 判断图片是否被选择
     * @param photo
     */
    @Override
    public boolean isMediaSelect(Photo photo) {
        return mediaList.contains(photo);
    }

    /**
     * 选择图片结束
     */
    @Override
    public void selectComplete() {
        if (ratioX > 0 && ratioY >0){ //需要裁剪
            loadSingleEdit();
        }else{
            selectFinish();
        }
    }

    /**
     * 获取所有被选择的图片
     */
    @Override
    public List<Photo> getSelectPhoto() {
        return mediaList;
    }

    @Override
    public int getMaxSelectCount() {
        return mSelectCount;
    }

    @Override
    public boolean isSingleSelect() {
        return getMaxSelectCount() <= 1;
    }

    @Override
    public int getSelectType() {
        return mSelectType;
    }

    /**
     * 选择完成
     */
    private void selectFinish(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MediaConstant.MEDIA_SELECT_RESULT,mediaList);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK,intent);
        ActivityCompat.finishAfterTransition(this);
    }

    private void loadSingleEdit(){
        BaseSupportFragment  mainFragment= findFragment(MianViewPagerFragment.class);
        BaseSupportFragment  cutSingleFragment= findFragment(CutSingleFragment.class);
        if (mainFragment != null){
            if (cutSingleFragment == null){
                cutSingleFragment = CutSingleFragment.newInstance(ratioX,ratioY,mediaList.get(0).getFilePath());
            }
            mainFragment.start(cutSingleFragment);
        }
    }

}
