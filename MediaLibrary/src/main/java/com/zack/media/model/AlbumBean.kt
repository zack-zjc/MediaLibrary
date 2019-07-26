package com.zack.media.model

/**
 * @Author zack
 * @Date 2019/7/26
 * @Description 媒体封面文件夹bean
 * @Version 1.0
 */
data class AlbumBean(val bucketId:Long,val bucketName:String,val coverPath:String,val mediaType:MediaType = MediaType.TYPE_IMAGE){

    override fun equals(other: Any?): Boolean = other is AlbumBean && other.bucketId == bucketId

    override fun hashCode(): Int = super.hashCode()

}