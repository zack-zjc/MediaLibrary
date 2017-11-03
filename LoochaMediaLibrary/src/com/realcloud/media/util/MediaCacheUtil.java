package com.realcloud.media.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.realcloud.loochadroid.LoochaApplication;
import com.realcloud.media.R;
import com.realcloud.media.model.Filter;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLookupFilter;

/**
 * Created by zack on 2017/11/3.
 */

public class MediaCacheUtil {

    //原始图像
    private static Bitmap needFilteredBitmap;

    private static LruCache<Integer,GPUImageFilter> filterLruCache = new LruCache<Integer,GPUImageFilter>(20){
        @Override
        protected GPUImageFilter create(Integer key) {
            //0代表原始图像
            if (key == 0) return new GPUImageFilter();
            Bitmap table = BitmapFactory.decodeResource(LoochaApplication.getInstance().getResources(),key);
            GPUImageLookupFilter lookUpFilter = new GPUImageLookupFilter();
            lookUpFilter.setBitmap(table);
            return lookUpFilter;
        }
    };

    private static LruCache<Integer,Bitmap> filteredBitmapLruCache = new LruCache<Integer,Bitmap>(20){
        @Override
        protected Bitmap create(Integer key) {
            //0代表原始图像
            if (key == 0) return needFilteredBitmap;
            GPUImage gpuImage = new GPUImage(LoochaApplication.getInstance());
            gpuImage.setFilter(filterLruCache.get(key));
            return gpuImage.getBitmapWithFilterApplied(needFilteredBitmap);
        }
    };

    /**
     * 清理
     */
    public static void clear(){
        needFilteredBitmap = null;
        filteredBitmapLruCache.evictAll();
        filterLruCache.evictAll();
    }

    /**
     * 设置原始图片
     * @param bitmap
     */
    public static void setBitmap(Bitmap bitmap){
        needFilteredBitmap = bitmap;
    }

    /**
     * 获取滤镜后图片
     * @param filterRes
     * @return
     */
    public static Bitmap getFilterBitmap(int filterRes){
        return filteredBitmapLruCache.get(filterRes);
    }

    /**
     * 湖区滤镜
     * @param filterRes
     * @return
     */
    public static GPUImageFilter getFilter(int filterRes){
        return filterLruCache.get(filterRes);
    }

    /**
     * 所有滤镜
     * @return
     */
    public static List<Filter> initFilter(){
        List<Filter> filters = new ArrayList<>();
        Filter filter0 = new Filter();
        filter0.filterRes = 0;
        filter0.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ori);
        filters.add(filter0);

        Filter filter1 = new Filter();
        filter1.filterRes = R.drawable.ue1;
        filter1.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue1);
        filters.add(filter1);

        Filter filter2 = new Filter();
        filter2.filterRes = R.drawable.ue2;
        filter2.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue2);
        filters.add(filter2);

        Filter filter3 = new Filter();
        filter3.filterRes = R.drawable.ue3;
        filter3.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue3);
        filters.add(filter3);

        Filter filter4 = new Filter();
        filter4.filterRes = R.drawable.ue4;
        filter4.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue4);
        filters.add(filter4);

        Filter filter5 = new Filter();
        filter5.filterRes = R.drawable.ue5;
        filter5.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue5);
        filters.add(filter5);

        Filter filter6 = new Filter();
        filter6.filterRes = R.drawable.ue6;
        filter6.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue6);
        filters.add(filter6);

        Filter filter7 = new Filter();
        filter7.filterRes = R.drawable.ue7;
        filter7.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue7);
        filters.add(filter7);

        Filter filter8 = new Filter();
        filter8.filterRes = R.drawable.ue8;
        filter8.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue8);
        filters.add(filter8);

        Filter filter9 = new Filter();
        filter9.filterRes = R.drawable.ue9;
        filter9.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue9);
        filters.add(filter9);

        Filter filter10 = new Filter();
        filter10.filterRes = R.drawable.ue10;
        filter10.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue10);
        filters.add(filter10);

        Filter filter11 = new Filter();
        filter11.filterRes = R.drawable.ue11;
        filter11.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue11);
        filters.add(filter11);

        Filter filter12 = new Filter();
        filter12.filterRes = R.drawable.ue12;
        filter12.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue12);
        filters.add(filter12);

        Filter filter13 = new Filter();
        filter13.filterRes = R.drawable.ue13;
        filter13.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue13);
        filters.add(filter13);

        Filter filter14 = new Filter();
        filter14.filterRes = R.drawable.ue14;
        filter14.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue14);
        filters.add(filter14);

        Filter filter15 = new Filter();
        filter15.filterRes = R.drawable.ue15;
        filter15.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue15);
        filters.add(filter15);

        Filter filter16 = new Filter();
        filter16.filterRes = R.drawable.ue16;
        filter16.name = LoochaApplication.getInstance().getResources().getString(R.string.filter_ue16);
        filters.add(filter16);
        return filters;
    }


}
