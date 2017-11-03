package com.realcloud.media.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;

/**
 * Created by zack on 2017/9/30.
 * 用于加载本地图片
 */

public class SimpleLocalImageView extends ImageView {

    public SimpleLocalImageView(Context context) {
        super(context);
    }

    public SimpleLocalImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleLocalImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 加载本地图片
     * @param filePath
     */
    public void loadImage(String filePath){
        if (TextUtils.isEmpty(filePath)) return;
        File file = new File(filePath);
        Glide.with(getContext()).load(file).asBitmap().into(this);
    }

    /**
     * 包含回调的本地加载
     * @param filePath
     * @param target
     */
    public void loadImageWithCallback(String filePath,SimpleTarget<Bitmap> target){
        if (TextUtils.isEmpty(filePath)) return;
        File file = new File(filePath);
        Glide.with(getContext()).load(file).asBitmap().into(target);
    }

}
