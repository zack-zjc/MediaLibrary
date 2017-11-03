package com.realcloud.media.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by zack on 2017/9/29.
 */

public class Photo implements Serializable {


    private final long mId;   //图片id
    private final String albumId; //相册id
    private String mDisplayName; //名字
    private String mFilePath; //路径
    private boolean isImage; //是否图片
    private long duration; //视频时间
    private int videoWith; //视频的宽度
    private int videoHeight;//视频的高度
    private String videoThumb;//视频的缩略图
    private String imageChangedPath;//图片编辑完后的样式

    //图片初始化构造
    public Photo(long id,String albumId,String displayName,String mFilePath) {
        this.mId = id;
        this.albumId = albumId;
        this.mDisplayName = displayName;
        this.mFilePath = mFilePath;
        this.isImage = true;
    }

    //视频初始化构造
    public Photo(long id,String albumId,String displayName,String mFilePath,long duration,int videoWith,int videoHeight,String videoThumb) {
        this.mId = id;
        this.albumId = albumId;
        this.mDisplayName = displayName;
        this.mFilePath = mFilePath;
        this.isImage = false;
        this.duration =duration;
        this.videoWith = videoWith;
        this.videoHeight = videoHeight;
        this.videoThumb = videoThumb;
    }


    public long getId() {
        return mId;
    }

    public String getAlbumId(){
        return albumId;
    }

    public String getDisplayName(){
        return mDisplayName;
    }

    public String getDuration(){
        return isImage ? "" : formatTime(duration);
    }

    public void setImageChangedPath(String imageChangedPath) {
        this.imageChangedPath = imageChangedPath;
    }

    /**
     * format时间为hh:mm:ss格式
     * @param time
     * @return
     */
    private String formatTime(long time){
        if (time < 1000 ){
            return "00:00:0"+String.valueOf(1);
        }else{
            long seconds = time / 1000;
            if (seconds < 60 ){
                return seconds >= 10 ? "00:00:"+seconds : "00:00:0"+seconds;
            }else{
                long minutes = seconds / 60;
                long second = seconds % 60;
                if (minutes < 60 ){
                    return minutes >= 10 ? second >=10 ? "00:"+minutes+":"+second : "00:"+minutes+":0"+second
                            : second >=10 ? "00:0"+minutes+":"+second : "00:0"+minutes+":0"+second;
                }else{
                    long hour = minutes / 60;
                    long minute = minutes % 60;
                    return hour >= 10 ? minute >= 10 ? second >=10 ? hour + ":"+minute+":"+seconds : hour + ":"+minute+":0"+seconds
                            : second >=10 ? hour +":0"+minute+":"+seconds : hour +":0"+minute+":0"+seconds
                            :minute >= 10 ? second >=10 ? "0"+hour + ":"+minute+":"+seconds : "0"+hour + ":"+minute+":0"+seconds
                            : second >=10 ? "0"+hour +":0"+minute+":"+seconds : "0"+hour +":0"+minute+":0"+seconds;
                }
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Photo && this.mFilePath != null && ((Photo)o).mFilePath != null && this.mFilePath.equals(((Photo)o).mFilePath);
    }

    public String getFilePath() {
        return mFilePath;
    }

    public String getEditImagePath(){
        return TextUtils.isEmpty(imageChangedPath) ? mFilePath : imageChangedPath;
    }

    public String getVideoThumb(){
        return videoThumb;
    }

    public boolean isImage(){
        return isImage;
    }

    public int getVideoWith(){
        return videoWith;
    }

    public int getVideoHeight(){
        return videoHeight;
    }
}
