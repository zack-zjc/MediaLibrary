package net.bither.util;

import android.graphics.Bitmap;

/**
 * Created by zack on 2017/2/20.
 */
public class NativeUtil {

    private static int DEFAULT_QUALITY = 80;

    public static void compressBitmap(Bitmap bit, String fileName,
                                      boolean optimize) {
        compressBitmap(bit, DEFAULT_QUALITY, fileName, optimize);
    }

    public static void compressBitmap(Bitmap bit, int quality, String fileName,
                                      boolean optimize) {
        compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality,
                fileName.getBytes(), optimize);
    }

    public static void compressBitmapOptimize(Bitmap bitmap,String fileName){ //缩略图压缩到150k以下
        int quality = DEFAULT_QUALITY;
        compressBitmapOptimize(bitmap, fileName, quality);
    }

    public static void compressBitmapOptimize(Bitmap bitmap,String fileName,int quality){//不循环压缩图片
        compressBitmap(bitmap,quality,fileName,true);
    }

    public static void compressLongBitmapOptimize(Bitmap bitmap,String fileName){ //长图压缩最大优化为70
        int quality = DEFAULT_QUALITY;
        int minquality = 70;
        compressBitmapOptimize(bitmap, fileName, quality,minquality);
    }

    public static void compressBitmapOptimize(Bitmap bitmap,String fileName,int quality,int minquality){//不循环压缩图片
        compressBitmap(bitmap,quality,fileName,true);
    }

    public static void compressBitmapOriginal(Bitmap bitmap,String fileName){//原图压缩到1M以下
        int quality = 90;
        compressBitmapOriginal(bitmap,fileName,quality);
    }

    public static void compressBitmapOriginal(Bitmap bitmap,String fileName,int quality){ //不循环压缩图片
        compressBitmap(bitmap,quality,fileName,true);
    }

    private static native String compressBitmap(Bitmap bit, int w, int h,
                                                int quality, byte[] fileNameBytes, boolean optimize);

    static {
        System.loadLibrary("jpegbither");
        System.loadLibrary("bitherjni");
    }

}
