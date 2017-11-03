package com.realcloud.media.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zack on 2017/10/10.
 * 用于展示封面蒙层
 */

public class LocalCoverView extends View {

    public LocalCoverView(Context context) {
        super(context);
    }

    public LocalCoverView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalCoverView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
}
