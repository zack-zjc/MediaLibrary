package com.realcloud.media.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.realcloud.media.MediaConstant;
import com.realcloud.media.model.Album;
import com.realcloud.media.model.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zack on 2017/10/12.
 * 查询视频的loader
 */

public class VideoLoader extends PhotoLoader {

    private static final String[] PROJECTION_VIDEO = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME,MediaStore.Video.Media.DATA,MediaStore.Video.Media.DURATION
            ,MediaStore.Video.Media.WIDTH,MediaStore.Video.Media.HEIGHT};
    private static final String ORDER_BY = MediaStore.Video.Media.DATE_ADDED + " DESC";
    private static final String IS_LARGE_SIZE = MediaStore.Video.Media.SIZE+" > 0 ";

    public VideoLoader(Context context,OnPhotoLoaderListener onPhotoLoaderListener) {
        super(context,onPhotoLoaderListener);
    }

    @Override
    public List<Photo> loadInBackground() {
        List<Photo> photos = new ArrayList<>();
        String albumId = getBundleArgs().getString(MediaConstant.ALBUMID);
        if (!TextUtils.isEmpty(albumId)){
            if (albumId.equals(String.valueOf(Album.ALL_VIDEO)) || albumId.equals(String.valueOf(Album.ALL_PHOTO_VIDEO))){ //所有视频文件
                Cursor videoCusrsor = getContext().getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        ,PROJECTION_VIDEO,IS_LARGE_SIZE ,null, ORDER_BY);
                if (videoCusrsor != null && videoCusrsor.getCount() > 0 && videoCusrsor.moveToFirst()){
                    do {
                        long id = videoCusrsor.getLong(videoCusrsor.getColumnIndex(MediaStore.Video.Media._ID));
                        String name = videoCusrsor.getString(videoCusrsor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        String data = videoCusrsor.getString(videoCusrsor.getColumnIndex(MediaStore.Video.Media.DATA));
                        long duration = videoCusrsor.getLong(videoCusrsor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        int width = videoCusrsor.getInt(videoCusrsor.getColumnIndex(MediaStore.Video.Media.WIDTH));
                        int height = videoCusrsor.getInt(videoCusrsor.getColumnIndex(MediaStore.Video.Media.HEIGHT));
                        Photo photo = new Photo(id,albumId,name,data,duration,width,height,getVideoThumbPath(id));
                        if (!photos.contains(photo)){
                            photos.add(photo);
                        }
                    }while (videoCusrsor.moveToNext());
                    videoCusrsor.close();
                }
            }else{
                Cursor videoCusrsor = getContext().getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        ,PROJECTION_VIDEO,MediaStore.Video.Media.BUCKET_ID + " = ? and (" + IS_LARGE_SIZE + ")",new String[]{albumId}, ORDER_BY);
                if (videoCusrsor != null && videoCusrsor.getCount() > 0 && videoCusrsor.moveToFirst()){
                    do {
                        long id = videoCusrsor.getLong(videoCusrsor.getColumnIndex(MediaStore.Video.Media._ID));
                        String name = videoCusrsor.getString(videoCusrsor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        String data = videoCusrsor.getString(videoCusrsor.getColumnIndex(MediaStore.Video.Media.DATA));
                        long duration = videoCusrsor.getLong(videoCusrsor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        int width = videoCusrsor.getInt(videoCusrsor.getColumnIndex(MediaStore.Video.Media.WIDTH));
                        int height = videoCusrsor.getInt(videoCusrsor.getColumnIndex(MediaStore.Video.Media.HEIGHT));
                        Photo photo = new Photo(id,albumId,name,data,duration,width,height,getVideoThumbPath(id));
                        if (!photos.contains(photo)){
                            photos.add(photo);
                        }
                    }while (videoCusrsor.moveToNext());
                    videoCusrsor.close();
                }
            }
        }
        return photos;
    }
}
