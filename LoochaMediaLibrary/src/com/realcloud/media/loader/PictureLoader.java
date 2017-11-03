package com.realcloud.media.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.realcloud.media.MediaConstant;
import com.realcloud.media.model.Album;
import com.realcloud.media.model.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zack on 2017/9/29.
 * 只包含图片的loader
 */

public class PictureLoader extends MediaBaseLoader<List<Photo>>{

    private static final String[] PROJECTION = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,MediaStore.Images.Media.DATA};
    private static final String ORDER_BY = MediaStore.Images.Media.DATE_ADDED + " DESC";
    private static final String IS_LARGE_SIZE = MediaStore.Images.Media.SIZE+" > 0 ";

    private OnPictureLoaderListener onPictureLoaderListener;

    public PictureLoader(Context context, OnPictureLoaderListener onPictureLoaderListener) {
        super(context);
        this.onPictureLoaderListener = onPictureLoaderListener;
    }

    /**
     * 返回数据
     * @param loader
     * @param pictures
     */
    @Override
    public void onLoadFinished(Loader<List<Photo>> loader, List<Photo> pictures) {
        if (onPictureLoaderListener != null){
            onPictureLoaderListener.onPictureLoaderFinish(pictures);
        }
    }

    /**
     * 查询同一个文件夹下的图片
     * @return
     */
    @Override
    public List<Photo> loadInBackground() {
        List<Photo> photos = new ArrayList<>();
        String albumId = getBundleArgs().getString(MediaConstant.ALBUMID);
        if (!TextUtils.isEmpty(albumId)){
            if (albumId.equals(String.valueOf(Album.ALL_PHOTO_VIDEO))){
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

    public interface OnPictureLoaderListener{
        void onPictureLoaderFinish(List<Photo> list);
    }

}
