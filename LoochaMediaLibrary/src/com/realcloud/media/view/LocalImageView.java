package com.realcloud.media.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.io.File;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * Created by zack on 2017/9/29.
 * 高度等于宽度，用于加载本地图片
 */

public class LocalImageView extends ImageView {

    public LocalImageView(Context context) {
        super(context);
    }

    public LocalImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置高度等于宽度
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        if (measuredWidth != 0) {
            setMeasuredDimension(measuredWidth, measuredWidth);
        }
    }

    public void loadImage(String filePath){
        if (TextUtils.isEmpty(filePath)) return;
        File file = new File(filePath);
        Glide.with(getContext()).load(file).asBitmap().into(this);
    }

    public void loadVideoImage(String filePath,String thumFile){
        if (TextUtils.isEmpty(thumFile) || !new File(thumFile).exists()){
            loadImage(filePath);
        }else{
            File file = new File(filePath);
            Glide.with(getContext()).load(file).asBitmap().into(this);
        }
    }

    /**
     * 加载圆角图片--centercrop
     * @param filePath
     * @param radius
     */
    public void loadRoundImage(String filePath,int radius){
        if (TextUtils.isEmpty(filePath)) return;
        File file = new File(filePath);
        Glide.with(getContext())
                .load(file)
                .bitmapTransform(new CenterCrop(getContext()),new RoundedCornersTransformation(getContext(), radius,0))
                .into(this);
    }

}
