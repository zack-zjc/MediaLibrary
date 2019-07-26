package com.zack.media.loader

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.loader.content.Loader
import com.zack.media.model.AlbumBean
import com.zack.media.model.MediaBean
import com.zack.media.model.MediaType
import java.util.*

/**
 * @Author zack
 * @Date 2019/7/26
 * @Description 视频loader
 * @Version 1.0
 */
class VideoLoader(context: Context,val loaderCompleteCallback:(List<MediaBean>,List<AlbumBean>)->Unit) : MediaBaseLoader<Pair<List<MediaBean>,List<AlbumBean>>>(context) {

    override fun loadInBackground(): Pair<List<MediaBean>,List<AlbumBean>>? {
        val videoList = ArrayList<MediaBean>()
        val albumList = ArrayList<AlbumBean>()
        val albumId = getBundleArgs()?.getString(MediaLoaderConstant.ALBUM_ID)
        val videoCursor : Cursor?
        if (albumId.isNullOrEmpty()){
            videoCursor = context.applicationContext.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,MediaLoaderConstant.VIDEO_PROJECTION,MediaLoaderConstant.VIDEO_SELECTION,
                null,MediaLoaderConstant.VIDEO_ORDER_BY)
        }else{
            val selection = String.format(MediaLoaderConstant.VIDEO_BUCKET_SELECTION,albumId)
            videoCursor = context.applicationContext.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,MediaLoaderConstant.VIDEO_PROJECTION,selection,
                null,MediaLoaderConstant.VIDEO_ORDER_BY)
        }
        if (videoCursor != null && videoCursor.count > 0 && videoCursor.moveToFirst()){
            do {
                val id = videoCursor.getLong(videoCursor.getColumnIndex(MediaStore.Video.Media._ID))
                val bucketId = videoCursor.getLong(videoCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))
                val bucketName = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                val data = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val time = videoCursor.getLong(videoCursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN))
                val duration = videoCursor.getLong(videoCursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                val videoBean = MediaBean(id, bucketId, bucketName, data, time,duration = duration,mediaType = MediaType.TYPE_VIDEO)
                if (!videoList.contains(videoBean)) {
                    videoList.add(videoBean)
                    val albumBean = AlbumBean(videoBean.bucketId,videoBean.bucketName,videoBean.filePath,MediaType.TYPE_VIDEO)
                    if (!albumList.contains(albumBean)){
                        albumList.add(albumBean)
                    }
                }
            } while (videoCursor.moveToNext())
            videoCursor.close()
        }
        return Pair(videoList,albumList)
    }

    override fun onLoadFinished(loader: Loader<Pair<List<MediaBean>,List<AlbumBean>>>, data: Pair<List<MediaBean>,List<AlbumBean>>?) {
        val videoList = data?.first?:Collections.emptyList()
        val albumList = data?.second?:Collections.emptyList()
        loaderCompleteCallback(videoList,albumList)
    }
}