package com.realcloud.media.listener;

import com.realcloud.media.model.Photo;

import java.util.List;

/**
 * Created by zack on 2017/9/30.
 * 选择媒体的接口
 */

public interface MediaSelectListener {

    /**
     * 添加媒体选择
     * @param photo
     */
    void selectMedia(Photo photo);

    /**
     * 删除选择的媒体
     * @param photo
     */
    void unSelectMedia(Photo photo);

    /**
     * 判断媒体是否被选择
     * @param photo 媒体数据
     * @return
     */
    boolean isMediaSelect(Photo photo);

    /**
     * 图片选择完成 返回
     */
    void selectComplete();

    /**
     * 获取当前已选择的图片
     * @return
     */
    List<Photo> getSelectPhoto();

    /**
     * 获取当前最大选择数量
     * @return
     */
    int getMaxSelectCount();

    /**
     * 判断是否单选图片
     * @return
     */
    boolean isSingleSelect();

    /**
     * 获取当前选择状态 0 -只选图片 1- 只选视频 2-所有都选
     * 参照 MediaConstant SELECT_PICTURE-SELECT_VIDEO-SELECT_ALL
     * @return
     */
    int getSelectType();

}
