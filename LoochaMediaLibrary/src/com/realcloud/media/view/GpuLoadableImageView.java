package com.realcloud.media.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.realcloud.loochadroid.executor.PriorityRunnable;
import com.realcloud.loochadroid.util.AppExecutors;
import com.realcloud.media.R;
import com.realcloud.media.model.Filter;
import com.realcloud.media.util.MediaCacheUtil;

/**
 * Created by zack on 2017/11/3.
 * 用于加载gpuFilter显示的view
 */

public class GpuLoadableImageView extends ImageView {

    public GpuLoadableImageView(Context context) {
        super(context);
    }

    public GpuLoadableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 加载对应的滤镜图片
     * @param filter
     */
    public void loadFilterImage(final Filter filter){
        setImageResource(0);
        setTag(R.id.id_filter,filter.filterRes);
        AppExecutors.getInstance().diskIO().execute(new PriorityRunnable() {
            @Override
            public void run() {
                filter.gpuImageFilter = MediaCacheUtil.getFilter(filter.filterRes);
                final Bitmap resultBitmap = MediaCacheUtil.getFilterBitmap(filter.filterRes);
                AppExecutors.getInstance().mainThread().execute(new PriorityRunnable() {
                    @Override
                    public void run() {
                        setLoadImage(resultBitmap,filter.filterRes);
                    }
                });
            }
        });
    }

    /**
     * 设置显示图片
     * @param bitmap
     * @param targetRes
     */
    private void setLoadImage(Bitmap bitmap,int targetRes){
        int originalRes = (int) getTag(R.id.id_filter);
        if (originalRes == targetRes){
            setImageBitmap(bitmap);
        }
    }
}
