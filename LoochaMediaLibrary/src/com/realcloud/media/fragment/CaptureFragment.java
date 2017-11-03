package com.realcloud.media.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.realcloud.loochadroid.LoochaApplication;
import com.realcloud.loochadroid.LoochaCookie;
import com.realcloud.loochadroid.fragmentation.BaseSupportFragment;
import com.realcloud.media.MediaConstant;
import com.realcloud.media.R;
import com.realcloud.media.listener.MediaSelectListener;
import com.realcloud.media.model.Photo;
import com.realcloud.media.view.CustomMediaTouchView;
import com.realcloud.media.view.SimpleLocalImageView;

import net.bither.util.NativeUtil;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zack on 2017/10/9.
 */

public class CaptureFragment extends BaseSupportFragment implements View.OnClickListener,SurfaceHolder.Callback,MediaRecorder.OnInfoListener,CustomMediaTouchView.OnCustomPressListener {

    private static final long MAX_TIME = 10000L;
    private static final int DEFAULT_PREVIEW_WIDTH = 480;
    private static final int DEFAULT_PREIVEW_HEIGHT = 320;
    private static final int DEFAULT_PICTURE_WIDTH = LoochaApplication.getScreenWidth();
    private static final int DEFAULT_PICTURE_HEIGHT = LoochaApplication.getScreenHeight();

    //主页面回调事件
    private MianViewPagerFragment.MainPagerListener mainPagerListener;

    //生成的媒体文件
    private Photo mediaPhoto;

    //相机
    private Camera camera;

    //闪光灯
    private ImageView flashView;

    //录像机
    private MediaRecorder mediaRecorder;

    //预览拍照的界面
    private SurfaceHolder surfaceHolder;

    //媒体文件处理listener
    private MediaSelectListener mediaSelectListener;

    //摄像头选择
    private int cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;

    // 当前相机ID
    private int currentCameraId = 0;

    //展示的操作提示文字
    private TextView textHint;

    //展示拍摄完成的布局
    private RelativeLayout mResultLayout;

    //底部选项背景
    private View mFooterBackground;

    //拍摄完成图片
    private SimpleLocalImageView mResultView;

    //确认按钮
    private ImageView mConfirm;

    //取消按钮
    private ImageView mCancel;

    //拍摄按钮
    private CustomMediaTouchView capture;

    //当前fragment是否可见
    private boolean isFragmentVisible;

    //更新视频时间所需task
    private Timer myTimer;

    //初始为-300第一次加为0开始计算百分比
    private int mCurrentRecordingTime = -300;

    //处理视频播放时间更新的handler
    private Handler mHander = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            mCurrentRecordingTime += 300;
            float percent = mCurrentRecordingTime *1f / MAX_TIME;
            capture.setProgressPercent(percent);
        }
    };

    public static CaptureFragment newInstance(MediaSelectListener mediaSelectListener,MianViewPagerFragment.MainPagerListener mainPagerListener) {
        Bundle args = new Bundle();
        CaptureFragment fragment = new CaptureFragment();
        fragment.setArguments(args);
        fragment.setMediaSelectListener(mediaSelectListener);
        fragment.setMainPagerListener(mainPagerListener);
        return fragment;
    }

    public void setMediaSelectListener(MediaSelectListener mediaSelectListener){
        this.mediaSelectListener = mediaSelectListener;
    }

    public void setMainPagerListener(MianViewPagerFragment.MainPagerListener mainPagerListener){
        this.mainPagerListener = mainPagerListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_capture_view,container,false);
        textHint = view.findViewById(R.id.id_hint);
        mFooterBackground = view.findViewById(R.id.id_footer);
        ImageView switchCmaera = view.findViewById(R.id.id_change_camera);
        switchCmaera.setOnClickListener(this);
        flashView = view.findViewById(R.id.id_flash);
        flashView.setOnClickListener(this);
        ImageView back = view.findViewById(R.id.id_back);
        back.setOnClickListener(this);
        capture = view.findViewById(R.id.id_take_picture);
        capture.setOnCustomPressListener(this);
        SurfaceView surfaceView = view.findViewById(R.id.id_surface);
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().addCallback(this);
        mResultLayout = view.findViewById(R.id.id_capture_result);
        mResultView = view.findViewById(R.id.id_image_view);
        mConfirm = view.findViewById(R.id.id_confirm);
        mConfirm.setOnClickListener(this);
        mCancel = view.findViewById(R.id.id_cancel);
        mCancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mResultLayout.setVisibility(View.INVISIBLE);
        setFootVisibility(View.VISIBLE);
        if (mediaSelectListener != null){
            if (mediaSelectListener.getSelectType() == MediaConstant.SELECT_PICTURE){
                textHint.setText(getResources().getString(R.string.str_capture_hint_picture));
            }else if (mediaSelectListener.getSelectType() == MediaConstant.SELECT_VIDEO){
                textHint.setText(getResources().getString(R.string.str_capture_hint_video));
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.id_back){
            if (mainPagerListener != null){
                mainPagerListener.jumpToPhoto();
            }else{
                getActivity().onBackPressed();
            }
        }else if (view.getId() == R.id.id_change_camera){
            cameraPosition = cameraPosition == Camera.CameraInfo.CAMERA_FACING_BACK ?
                    Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for (int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == cameraPosition) {
                    currentCameraId = i;
                    break;
                }
            }
            camera.stopPreview();
            camera.release();
            initCamera(surfaceHolder);
        }else if (view.getId() == R.id.id_flash){
            Camera.Parameters mParameters = camera.getParameters();
            mParameters.setFlashMode(TextUtils.equals(mParameters.getFlashMode(),Camera.Parameters.FLASH_MODE_TORCH)
                    ? Camera.Parameters.FLASH_MODE_OFF : Camera.Parameters.FLASH_MODE_TORCH);
            flashView.setImageResource(TextUtils.equals(mParameters.getFlashMode(),Camera.Parameters.FLASH_MODE_TORCH)
                    ? R.drawable.icon_flash_off : R.drawable.icon_flash_on);
            camera.setParameters(mParameters);
        }else if (view.getId() == R.id.id_cancel){
            mediaPhoto = null;
            mResultLayout.setVisibility(View.INVISIBLE);
            setFootVisibility(View.VISIBLE);
            initCamera(surfaceHolder);
        }else if (view.getId() == R.id.id_confirm){
            if (mediaSelectListener != null){
                mediaSelectListener.selectMedia(mediaPhoto);
                mediaSelectListener.selectComplete();
            }
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        if (mainPagerListener != null){
            mainPagerListener.jumpToPhoto();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        isFragmentVisible = true;
        initCamera(this.surfaceHolder);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        isFragmentVisible = false;
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private void setFootVisibility(int visibiilty){
        mFooterBackground.setVisibility(visibiilty);
        if(mainPagerListener != null){
            mainPagerListener.setFootVisibility(visibiilty);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        if (isFragmentVisible && camera == null){ //如果visible先走未初始化则初始化
            initCamera(this.surfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = null;
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    /**
     * 初始化照相机
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder != null){
            // 此处默认打开后置摄像头
            // 通过传入参数可以打开前置摄像头
            camera = Camera.open(currentCameraId);
            camera.setDisplayOrientation(90);
            Camera.Parameters parameters = camera.getParameters();
            // 设置照片的格式
            parameters.setPictureFormat(ImageFormat.JPEG);
            List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
            Camera.Size pictureSize = null;
            if (sizeList != null && !sizeList.isEmpty()){
                pictureSize = sizeList.get(0);
                for (int i =0 ;i<sizeList.size();i++){
                    if (sizeList.get(i).width <= DEFAULT_PICTURE_WIDTH){
                        pictureSize = sizeList.get(i);
                        break;
                    }
                }
            }
            // 设置照片的大小
            parameters.setPictureSize(pictureSize != null ? pictureSize.width :DEFAULT_PICTURE_WIDTH , pictureSize != null ? pictureSize.height:DEFAULT_PICTURE_HEIGHT);
            camera.setParameters(parameters);
            // 通过SurfaceView显示取景画面
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 开始预览
            camera.startPreview();
            camera.autoFocus(null);
        }
    }

    /**
     * 拍照调用方法
     */
    public void capture() {
        if (camera != null) {
            // 控制摄像头自动对焦后才拍摄
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean focus, Camera camera) {
                    if (focus) {
                        // takePicture()方法需要传入三个监听参数
                        // 第一个监听器；当用户按下快门时激发该监听器
                        // 第二个监听器；当相机获取原始照片时激发该监听器
                        // 第三个监听器；当相机获取JPG照片时激发该监听器
                        camera.takePicture(new Camera.ShutterCallback() {

                            @Override
                            public void onShutter() {
                                // 按下快门瞬间会执行此处代码
                            }
                        }, new Camera.PictureCallback() {

                            @Override
                            public void onPictureTaken(byte[] arg0, Camera arg1) {
                                // 此处代码可以决定是否需要保存原始照片信息
                            }
                        }, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] bytes, Camera camera) {
                                if (CaptureFragment.this.camera != null) {
                                    CaptureFragment.this.camera.release();
                                    CaptureFragment.this.camera = null;
                                }
                                //获取图片
                                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                                        bytes.length);
                                String filePath = getMediaImageFilePath();
                                NativeUtil.compressBitmap(rotateBitmap(bitmap),filePath,false);
                                mediaPhoto = new Photo(System.currentTimeMillis(),"","",filePath);
                                captureCompleteAnimation();
                            }
                        });
                    }
                }
            });
        }
    }

    private String getMediaImageFilePath() {
        return LoochaCookie.LOOCHA_TMP_PATH + "orig_" + "mini_image_" + System.currentTimeMillis() + ".jpg";
    }

    /**
     * 旋转图片，因为拍摄得到的图片是旋转过的
     * @param bmp
     * @return
     */
    public Bitmap rotateBitmap(Bitmap bmp){
        Matrix matrix = new Matrix();
        matrix.postRotate(cameraPosition == Camera.CameraInfo.CAMERA_FACING_BACK ? 90 : -90);
        return Bitmap.createBitmap(bmp, 0,0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    };

    /**
     * 录像机
     */
    private void startRecordVideo() {
        if (mediaRecorder != null) {
            releaseRecoder();
        }
        if (camera != null) {
            camera.unlock();
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        int quality = CamcorderProfile.QUALITY_LOW;
        if (CamcorderProfile.hasProfile(currentCameraId, CamcorderProfile.QUALITY_480P)) {
            quality = CamcorderProfile.QUALITY_480P;
        }
        CamcorderProfile profile = CamcorderProfile.get(currentCameraId, quality);
        profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
        profile.videoCodec = MediaRecorder.VideoEncoder.H264;
        profile.audioCodec = MediaRecorder.AudioEncoder.AAC;
        if (Build.VERSION.SDK_INT >= 14) {
            profile.audioBitRate = 16 * 1024;
            profile.audioSampleRate = 15;
        }
        profile.videoFrameWidth = DEFAULT_PREVIEW_WIDTH;
        profile.videoFrameHeight = DEFAULT_PREIVEW_HEIGHT;

        profile.videoBitRate = 600 * 1024;
        mediaRecorder.setProfile(profile);

        mediaRecorder.setOrientationHint(90);  // 输出文件旋转
        mediaRecorder.setMaxDuration(Math.round(MAX_TIME));// 录制时间
        mediaRecorder.setOnInfoListener(this);
        String filePath = getMediaVideoFilePath();
        mediaPhoto = new Photo(System.currentTimeMillis(),"","",filePath,MAX_TIME,DEFAULT_PREVIEW_WIDTH,DEFAULT_PREIVEW_HEIGHT,"");
        mediaRecorder.setOutputFile(filePath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private String getMediaVideoFilePath() {
        return LoochaCookie.LOOCHA_TMP_PATH + "orig_" + "mini_video_" + System.currentTimeMillis() + ".mp4";
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            releaseRecoder();
            captureCompleteAnimation();
            stopTimeTask();
        }
    }

    /**
     * 释放mediarecoder
     */
    private void releaseRecoder() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.setOnErrorListener(null);
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mediaRecorder = null;
                if (camera != null) {
                    camera.release();
                    camera = null;
                }
            }
        }
    }

    private void captureCompleteAnimation(){
        mResultLayout.setVisibility(View.VISIBLE);
        setFootVisibility(View.INVISIBLE);
        mResultView.loadImageWithCallback(mediaPhoto.getFilePath(), new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mResultView.setImageBitmap(resource);
                TranslateAnimation translateRightAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,-1f,Animation.RELATIVE_TO_SELF,0f
                        ,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
                translateRightAnimation.setDuration(250);
                TranslateAnimation translateLeftAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,1f,Animation.RELATIVE_TO_SELF,0f
                        ,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
                translateLeftAnimation.setDuration(250);
                mCancel.startAnimation(translateLeftAnimation);
                mConfirm.startAnimation(translateRightAnimation);
            }
        });
    }

    @Override
    public void onClick() {
        if (mediaSelectListener != null && mediaSelectListener.getSelectType() != MediaConstant.SELECT_VIDEO){
            capture();
        }else{
            Toast.makeText(getContext(),R.string.str_capture_video_time_short,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLongPressStart() {
        if (mediaSelectListener != null && (mediaSelectListener.getSelectType() == MediaConstant.SELECT_VIDEO
                || mediaSelectListener.getSelectType() == MediaConstant.SELECT_ALL)){
            startRecordVideo();
            startTimeTask();
        }else{
            capture();
        }

    }

    @Override
    public void onLongPressEnd() {
        if (mediaSelectListener != null && (mediaSelectListener.getSelectType() == MediaConstant.SELECT_VIDEO
                || mediaSelectListener.getSelectType() == MediaConstant.SELECT_ALL)){
            releaseRecoder();
            captureCompleteAnimation();
            stopTimeTask();
        }
    }

    //开始时间计时
    private void startTimeTask(){
        if (myTimer != null){
            myTimer.cancel();
        }
        mCurrentRecordingTime = 0;
        myTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mHander.sendEmptyMessage(0);
            }
        };
        myTimer.schedule(timerTask,0,300);
    }

    //结束时间计时
    private void stopTimeTask(){
        mHander.removeCallbacksAndMessages(null);
        if (myTimer != null){
            myTimer.cancel();
            myTimer = null;
        }
        capture.resetState();
    }

}
