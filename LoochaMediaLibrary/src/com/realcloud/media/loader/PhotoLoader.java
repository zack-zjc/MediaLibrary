package com.realcloud.media.loader;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.realcloud.loochadroid.LoochaApplication;
import com.realcloud.media.MediaConstant;
import com.realcloud.media.model.Album;
import com.realcloud.media.model.Photo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zack on 2017/9/29.
 */

public class PhotoLoader extends MediaBaseLoader<List<Photo>> {

    private static final String[] PROJECTION = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,MediaStore.Images.Media.DATA};
    private static final String[] PROJECTION_VIDEO = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME,MediaStore.Video.Media.DATA,MediaStore.Video.Media.DURATION
            ,MediaStore.Video.Media.WIDTH,MediaStore.Video.Media.HEIGHT};
    private static final String ORDER_BY = MediaStore.Images.Media.DATE_ADDED + " DESC";
    private static final String IS_LARGE_SIZE = MediaStore.Images.Media.SIZE+" > 0 ";

    private OnPhotoLoaderListener onPhotoLoaderListener;

    //是否只展示图片
    private boolean onlyPicture;

    public PhotoLoader(Context context,OnPhotoLoaderListener onPhotoLoaderListener) {
        super(context);
        this.onPhotoLoaderListener = onPhotoLoaderListener;
    }

    public PhotoLoader(Context context,OnPhotoLoaderListener onPhotoLoaderListener,boolean onlyPicture) {
        super(context);
        this.onPhotoLoaderListener = onPhotoLoaderListener;
        this.onlyPicture = onlyPicture;
    }

    /**
     * 返回数据
     * @param loader
     * @param pictures
     */
    @Override
    public void onLoadFinished(Loader<List<Photo>> loader, List<Photo> pictures) {
        if (onPhotoLoaderListener != null){
            onPhotoLoaderListener.onPhotoLoaderFinish(pictures);
        }
    }

    /**
     * 查询同一个文件夹下的视频和图片并合并
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public List<Photo> loadInBackground() {
        List<Photo> photos = new ArrayList<>();
        String albumId = getBundleArgs().getString(MediaConstant.ALBUMID);
        if (!TextUtils.isEmpty(albumId)){ //对应图片文件夹
            if (albumId.equals(String.valueOf(Album.ALL_VIDEO)) && !onlyPicture){ //所有视频文件
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
            }else if (albumId.equals(String.valueOf(Album.ALL_PHOTO_VIDEO))){  //所有文件包含视频和图片
                if (!onlyPicture){
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
                }
                Cursor imageCursor = getContext().getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        ,PROJECTION,IS_LARGE_SIZE,null, ORDER_BY);
                if (imageCursor != null && imageCursor.getCount() > 0 && imageCursor.moveToFirst()){
                    do {
                        long id = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
                        String name = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        String data = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        Photo photo = new Photo(id,albumId,name,data);
                        if (!photos.contains(photo)){
                            photos.add(photo);
                        }
                    }while (imageCursor.moveToNext());
                    imageCursor.close();
                }
            }else{
                Cursor imageCursor = getContext().getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        ,PROJECTION,MediaStore.Images.Media.BUCKET_ID + " = ? and (" + IS_LARGE_SIZE + ")",new String[]{albumId}, ORDER_BY);
                if (imageCursor != null && imageCursor.getCount() > 0 && imageCursor.moveToFirst()){
                    do {
                        long id = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
                        String name = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        String data = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        Photo photo = new Photo(id,albumId,name,data);
                        if (!photos.contains(photo)){
                            photos.add(photo);
                        }
                    }while (imageCursor.moveToNext());
                    imageCursor.close();
                }
            }
        }
        return photos;
    }

    String getVideoThumbPath(long id){
        Cursor cursor = null;
        try{
            cursor = LoochaApplication.getInstance().getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,new String[]{MediaStore.Video.Thumbnails.VIDEO_ID, MediaStore.Video.Thumbnails.DATA },
                    MediaStore.Video.Thumbnails.VIDEO_ID +"=?",new String[]{String.valueOf(id)},null);
            if (cursor != null && cursor.moveToFirst()){
                return  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){
                cursor.close();
            }
        }
        return "";
    }

    public interface OnPhotoLoaderListener{
        void onPhotoLoaderFinish(List<Photo> list);
    }
}
