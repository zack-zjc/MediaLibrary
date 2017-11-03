package com.realcloud.media.util;

import android.app.Activity;
import android.content.Intent;

import com.realcloud.media.ActMediaStore;
import com.realcloud.media.MediaConstant;

/**
 * Created by zack on 2017/10/12.
 * 用于图库相关操作
 */

public class MediaUtil {

    /**
     * 选择视频，只能选择一个
     * @param activity
     * @param requestCode
     */
    public static void pickVideo(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ActMediaStore.class);
        intent.putExtra(MediaConstant.PICK_TYPE, MediaConstant.SELECT_VIDEO);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 只选择图片，默认最多9个
     * @param activity
     * @param requestCode
     */
    public static void pickImage(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ActMediaStore.class);
        intent.putExtra(MediaConstant.PICK_TYPE, MediaConstant.SELECT_PICTURE);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void pickImage(Activity activity, int pickCount, int requestCode) {
        Intent intent = new Intent(activity, ActMediaStore.class);
        intent.putExtra(MediaConstant.PICK_COUNT, pickCount);
        intent.putExtra(MediaConstant.PICK_TYPE, MediaConstant.SELECT_PICTURE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 选择媒体文件，默认视频只能选一个，图片默认只能最多9个
     * @param activity
     * @param requestCode
     */
    public static void pickMedia(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ActMediaStore.class);
        intent.putExtra(MediaConstant.PICK_TYPE, MediaConstant.SELECT_ALL);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 只选择图片，选一个
     * @param activity
     * @param requestCode
     */
    public static void pickSingleImage(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ActMediaStore.class);
        intent.putExtra(MediaConstant.PICK_TYPE, MediaConstant.SELECT_PICTURE);
        intent.putExtra(MediaConstant.PICK_COUNT, 1);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 只选择图片，选一个并裁剪
     * @param activity
     * @param requestCode
     */
    public static void pickSingleImageWithRatio(Activity activity, int requestCode,int ratioX,int ratioY) {
        Intent intent = new Intent(activity, ActMediaStore.class);
        intent.putExtra(MediaConstant.PICK_TYPE, MediaConstant.SELECT_PICTURE);
        intent.putExtra(MediaConstant.PICK_COUNT, 1);
        intent.putExtra(MediaConstant.RATIO_X, ratioX);
        intent.putExtra(MediaConstant.RATIO_Y, ratioY);
        activity.startActivityForResult(intent, requestCode);
    }

}
