package com.realcloud.media.loader;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.realcloud.media.model.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zack on 2017/10/12.
 */

public class VideoAlbumLoader extends AlbumLoader {

    private static final String[] PROJECTION = {MediaStore.Video.Media._ID,MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.DATA};
    private static final String BUCKET_GROUP_BY = ") GROUP BY  1,(2";
    private static final String BUCKET_ORDER_BY = "MAX("+ MediaStore.Video.Media.DATE_TAKEN+") DESC";
    private static final String IS_LARGE_SIZE = MediaStore.Video.Media.SIZE+" > 0 or "+ MediaStore.Video.Media.SIZE+" is null";

    public VideoAlbumLoader(Context context,OnAlbumLoaderListener onAlbumLoaderListener) {
        super(context,onAlbumLoaderListener);
    }

    @Override
    public List<Album> loadInBackground() {
        List<Album> albums = new ArrayList<>();
        //获取封面
        String videoCover = null;
        Cursor videoCoverCusrsor = getContext().getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                ,null,IS_LARGE_SIZE ,null, null);
        if (videoCoverCusrsor != null && videoCoverCusrsor.getCount() > 0 && videoCoverCusrsor.moveToFirst()){
            videoCover = videoCoverCusrsor.getString(videoCoverCusrsor.getColumnIndex(MediaStore.Video.Media.DATA));
            videoCoverCusrsor.close();
        }
        albums.add(Album.getAllVideo(videoCover));

        //获取所有图片文件夹
        Cursor videoCursor = getContext().getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                ,PROJECTION,IS_LARGE_SIZE + BUCKET_GROUP_BY,null, BUCKET_ORDER_BY);
        if (videoCursor != null && videoCursor.getCount() > 0 && videoCursor.moveToFirst()){
            do {
                long id = videoCursor.getLong(videoCursor.getColumnIndex(MediaStore.Video.Media._ID));
                long buketId = videoCursor.getLong(videoCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID));
                String name = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                String data = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                Album album = new Album(id,name,buketId,data);
                if (!albums.contains(album)){
                    albums.add(album);
                }
            }while (videoCursor.moveToNext());
            videoCursor.close();
        }
        return albums;
    }
}
