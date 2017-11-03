package com.realcloud.media.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.realcloud.media.model.Album;
import com.realcloud.media.model.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zack on 2017/9/29.
 */

public class AlbumLoader extends MediaBaseLoader<List<Album>> {

    private static final String[] PROJECTION = {MediaStore.Images.Media._ID,MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA};
    private static final String BUCKET_GROUP_BY = ") GROUP BY  1,(2";
    private static final String BUCKET_ORDER_BY = "MAX("+ MediaStore.Images.Media.DATE_TAKEN+") DESC";
    private static final String IS_LARGE_SIZE = MediaStore.Images.Media.SIZE+" > 0 or "+ MediaStore.Images.Media.SIZE+" is null";

    private OnAlbumLoaderListener onAlbumLoaderListener;

    //判断是否只选择图片
    private boolean onlyPicture;


    public AlbumLoader(Context context,OnAlbumLoaderListener onAlbumLoaderListener) {
        super(context);
        this.onAlbumLoaderListener = onAlbumLoaderListener;
    }

    public AlbumLoader(Context context,OnAlbumLoaderListener onAlbumLoaderListener,boolean onlyPicture) {
        super(context);
        this.onAlbumLoaderListener = onAlbumLoaderListener;
        this.onlyPicture = onlyPicture;
    }

    /**
     * 返回数据
     * @param loader
     * @param albums
     */
    @Override
    public void onLoadFinished(Loader<List<Album>> loader, List<Album> albums) {
        if (onAlbumLoaderListener != null){
            onAlbumLoaderListener.onAlbumLoaderFinish(albums);
        }
    }

    /**
     * 合并查询出来的视频和图片文件夹
     * @return
     */
    @Override
    public List<Album> loadInBackground() {
        List<Album> albums = new ArrayList<>();
        //获取封面
        String videoCover = null,imageCover = null;
        Cursor imageCoverCursor = getContext().getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ,null,IS_LARGE_SIZE,null, null);
        if (imageCoverCursor != null && imageCoverCursor.getCount() > 0 && imageCoverCursor.moveToFirst()){
            imageCover = imageCoverCursor.getString(imageCoverCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imageCoverCursor.close();
        }
        albums.add(Album.getAllImageAndPhoto(TextUtils.isEmpty(imageCover) ? videoCover :imageCover));

        if (!onlyPicture){
            Cursor videoCoverCusrsor = getContext().getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    ,null,IS_LARGE_SIZE ,null, null);
            if (videoCoverCusrsor != null && videoCoverCusrsor.getCount() > 0 && videoCoverCusrsor.moveToFirst()){
                videoCover = videoCoverCusrsor.getString(videoCoverCusrsor.getColumnIndex(MediaStore.Video.Media.DATA));
                videoCoverCusrsor.close();
            }
            albums.add(Album.getAllVideo(TextUtils.isEmpty(videoCover) ? imageCover : videoCover));
        }

        //获取所有图片文件夹
        Cursor imageCursor = getContext().getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ,PROJECTION,IS_LARGE_SIZE + BUCKET_GROUP_BY,null, BUCKET_ORDER_BY);
        if (imageCursor != null && imageCursor.getCount() > 0 && imageCursor.moveToFirst()){
            do {
                long id = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
                long buketId = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                String name = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String data = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Album album = new Album(id,name,buketId,data);
                if (!albums.contains(album)){
                    albums.add(album);
                }
            }while (imageCursor.moveToNext());
            imageCursor.close();
        }
        return albums;
    }

    public interface OnAlbumLoaderListener{
        void onAlbumLoaderFinish(List<Album> list);
    }
}
