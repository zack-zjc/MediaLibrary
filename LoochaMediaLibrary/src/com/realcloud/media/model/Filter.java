package com.realcloud.media.model;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by zack on 2017/10/13.
 */

public class Filter {

    //滤镜对应的resource文件
    public int filterRes;

    //滤镜的名字
    public String name;

    //滤镜对应的filter
    public GPUImageFilter gpuImageFilter;


    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Filter && ((Filter) obj).filterRes == filterRes;
    }
}
