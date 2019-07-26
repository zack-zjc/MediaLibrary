package com.zack.media.model

/**
 * @Author zack
 * @Date 2019/7/26
 * @Description 媒体文件bean
 * @Version 1.0
 */
data class MediaBean(val id :Long,val bucketId :Long,val bucketName:String,val filePath:String,val timeToken:Long = 0,
                     val orientation:Int = 0,val duration :Long = 0,val mediaType:MediaType = MediaType.TYPE_IMAGE){

    fun isImage() = mediaType == MediaType.TYPE_IMAGE

    override fun equals(other: Any?): Boolean = other is MediaBean && other.id == id && other.mediaType == mediaType

    override fun hashCode(): Int = super.hashCode()

}