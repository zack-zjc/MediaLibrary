package com.zack.media.loader

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.loader.content.Loader
import com.zack.media.model.AlbumBean
import com.zack.media.model.MediaBean
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author zack
 * @Date 2019/7/26
 * @Description 图片文件的loader
 * @Version 1.0
 */
class ImageLoader(context:Context,val loaderCompleteCallback:(List<MediaBean>,List<AlbumBean>)->Unit) : MediaBaseLoader<Pair<List<MediaBean>,List<AlbumBean>>>(context){

    override fun loadInBackground(): Pair<List<MediaBean>,List<AlbumBean>>? {
        val imageList = ArrayList<MediaBean>()
        val albumList = ArrayList<AlbumBean>()
        val albumId = getBundleArgs()?.getString(MediaLoaderConstant.ALBUM_ID)
        val imageCursor :Cursor?
        if (albumId.isNullOrEmpty()){
            imageCursor = context.applicationContext.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaLoaderConstant.IMAGE_PROJECTION,MediaLoaderConstant.IMAGE_SELECTION,
                null,MediaLoaderConstant.IMAGE_ORDER_BY)
        }else{
            val selection = String.format(MediaLoaderConstant.IMAGE_BUCKET_SELECTION,albumId)
            imageCursor = context.applicationContext.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaLoaderConstant.IMAGE_PROJECTION,selection,
                null,MediaLoaderConstant.IMAGE_ORDER_BY)
        }
        if (imageCursor != null && imageCursor.count > 0 && imageCursor.moveToFirst()){
            do {
                val id = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media._ID))
                val bucketId = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                val bucketName = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val data = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA))
                val time = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN))
                val orientation = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION))
                val imageBean = MediaBean(id, bucketId, bucketName, data, time,orientation)
                if (!imageList.contains(imageBean)) {
                    imageList.add(imageBean)
                    val albumBean = AlbumBean(imageBean.bucketId,imageBean.bucketName,imageBean.filePath)
                    if (!albumList.contains(albumBean)){
                        albumList.add(albumBean)
                    }
                }
            } while (imageCursor.moveToNext())
            imageCursor.close()
        }
        return Pair(imageList,albumList)
    }

    override fun onLoadFinished(loader: Loader<Pair<List<MediaBean>,List<AlbumBean>>>, data: Pair<List<MediaBean>,List<AlbumBean>>?) {
        val imageList = data?.first?:Collections.emptyList()
        val albumList = data?.second?:Collections.emptyList()
        loaderCompleteCallback(imageList,albumList)
    }

}