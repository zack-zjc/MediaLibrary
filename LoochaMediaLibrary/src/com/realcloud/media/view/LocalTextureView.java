package com.realcloud.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by zack on 2017/9/30.
 * 自定义展示视频的textureview
 */

public class LocalTextureView extends TextureView {

    private float mRatioSize = 0;

    public LocalTextureView(Context context) {
        super(context);
    }

    public LocalTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRationSize(int videoWith,int videoHeight){
        if (videoHeight > 0 && videoWith > 0){
            mRatioSize = videoHeight *1f/ videoWith;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mRatioSize > 0){ //按视频大小设置texture的大小
            int with = getMeasuredWidth();
            int height = (int) (mRatioSize * with);
            if (height > getMeasuredHeight()){
                height = getMeasuredHeight();
                with = (int) (height / mRatioSize);
            }else{
                height = (int) (with * mRatioSize);
            }
            setMeasuredDimension(with,height);
        }

    }
}
