package com.zack.media.loader

import android.provider.MediaStore

/**
 * @Author zack
 * @Date 2019/7/26
 * @Description loader对应的静态常量
 * @Version 1.0
 */
object MediaLoaderConstant {

    const val ALBUM_ID = "albumId"

    val IMAGE_PROJECTION = arrayOf(MediaStore.Images.Media._ID,MediaStore.Images.Media.BUCKET_ID,MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.DATA,MediaStore.Images.Media.DATE_TAKEN,MediaStore.Images.Media.ORIENTATION)

    const val IMAGE_ORDER_BY = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    const val IMAGE_SELECTION = "${MediaStore.Images.Media.SIZE} > 0 "

    const val IMAGE_BUCKET_SELECTION = "${MediaStore.Images.Media.BUCKET_ID} = %s AND ${MediaStore.Images.Media.SIZE} > 0 "

    val VIDEO_PROJECTION = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_ID,MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_TAKEN,MediaStore.Video.Media.DURATION)

    const val VIDEO_ORDER_BY =  "${MediaStore.Video.Media.DATE_ADDED} DESC"

    const val VIDEO_SELECTION = "${MediaStore.Video.Media.SIZE} > 0 "

    const val VIDEO_BUCKET_SELECTION = "${MediaStore.Video.Media.BUCKET_ID} = %s AND ${MediaStore.Video.Media.SIZE} > 0 "

}