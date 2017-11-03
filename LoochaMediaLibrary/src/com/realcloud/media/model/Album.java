package com.realcloud.media.model;

import com.realcloud.loochadroid.LoochaApplication;
import com.realcloud.media.R;

import java.io.Serializable;

/**
 * Created by zack on 2017/8/30.
 */

public class Album implements Serializable {

    public static final int ALL_PHOTO_VIDEO = 0;
    public static final int ALL_VIDEO = 1;


    private final long mId;       //id
    private final String mDisplayName;//展示的名字
    private final long bucketId;  //列表id
    private final String coverImage; //封面图片

    public Album(long id,String albumName, long bucketId, String coverImage) {
        this.mId = id;
        this.mDisplayName = albumName;
        this.bucketId = bucketId;
        this.coverImage = coverImage;
    }

    public long getId() {
        return mId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getCover(){
        return  coverImage;
    }

    public long getBucketId(){
        return bucketId;
    }

    /**
     * 获取所有视频和图片的条目
     * @return
     */
    public static Album getAllImageAndPhoto(String coverImage){
        return new Album(ALL_PHOTO_VIDEO, LoochaApplication.getInstance().getResources().getString(R.string.str_all_photo_and_video)
                ,ALL_PHOTO_VIDEO,coverImage);
    }

    /**
     * 获取所有视频的条目
     * @return
     */
    public static Album getAllVideo(String coverImage){
        return new Album(ALL_VIDEO, LoochaApplication.getInstance().getResources().getString(R.string.str_all_video)
                ,ALL_VIDEO,coverImage);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Album && ((Album)obj).bucketId == this.bucketId;
    }
}
