package com.realcloud.media.fragment;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.realcloud.loochadroid.fragmentation.BaseSupportFragment;
import com.realcloud.media.MediaConstant;
import com.realcloud.media.R;
import com.realcloud.media.listener.MediaSelectListener;
import com.realcloud.media.model.Photo;
import com.realcloud.media.view.LocalTextureView;
import com.realcloud.media.view.SimpleLocalImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zack on 2017/9/30.
 */

public class VideoDetailFragment extends BaseSupportFragment implements View.OnClickListener,SeekBar.OnSeekBarChangeListener,TextureView.SurfaceTextureListener,MediaPlayer.OnCompletionListener{

    //点击做动画隐藏的头部和底部
    private View headerView;

    //媒体文件处理listener
    private MediaSelectListener mediaSelectListener;

    //中间点击开始的view
    private ImageView playView;

    //下边点击开始暂停的view
    private ImageView playStatusView;

    //进度条
    private SeekBar mProgressView;

    //总时间显示的view
    private TextView mTimeView;

    //播放时间显示的view
    private TextView mCurrentTimeView;

    //播放视频的textureview
    private LocalTextureView mTextureView;

    //展示缩略图的view
    private SimpleLocalImageView mCoverView;

    //展示的标题
    private TextView mTitle;

    //操作的整个view
    private RelativeLayout mControlView;

    //播放器
    private MediaPlayer mediaPlayer;

    //播放器播放的surface
    private SurfaceTexture mSurfaceTexture;

    //更新视频时间所需task
    private Timer myTimer;

    //处理视频播放时间更新的handler
    private Handler mHander = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            mTimeView.setText(getFormatTime(mediaPlayer.getDuration()));
            mCurrentTimeView.setText(getFormatTime(mediaPlayer.getCurrentPosition()));
            float percent = mediaPlayer.getCurrentPosition() *1f / mediaPlayer.getDuration();
            mProgressView.setProgress((int) (percent*mProgressView.getMax()));
        }
    };

    public static VideoDetailFragment newInstance(MediaSelectListener mediaSelectListener) {
        Bundle args = new Bundle();
        VideoDetailFragment fragment = new VideoDetailFragment();
        fragment.setArguments(args);
        fragment.setMediaSelectListener(mediaSelectListener);
        return fragment;
    }

    public void setMediaSelectListener(MediaSelectListener mediaSelectListener){
        this.mediaSelectListener = mediaSelectListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_video_detail_view,null);
        View layoutView = view.findViewById(R.id.id_container);
        layoutView.setOnClickListener(this);
        headerView = view.findViewById(R.id.id_layout_header);
        headerView.setOnClickListener(this); //不处理只是做截取点击事件
        playView = view.findViewById(R.id.id_play_view);
        playView.setOnClickListener(this);
        playStatusView = view.findViewById(R.id.id_play_status_view);
        playStatusView.setOnClickListener(this);
        mProgressView = view.findViewById(R.id.id_progress_view);
        mProgressView.setOnSeekBarChangeListener(this);
        mTimeView = view.findViewById(R.id.id_time);
        mCurrentTimeView = view.findViewById(R.id.id_current_time);
        mTextureView = view.findViewById(R.id.id_texture_view);
        mTextureView.setSurfaceTextureListener(this);
        mCoverView = view.findViewById(R.id.id_image_view);
        mTitle = view.findViewById(R.id.id_title);
        mControlView = view.findViewById(R.id.id_control_view);
        mControlView.setOnClickListener(this); //不处理只是做截取点击事件
        ImageView back = view.findViewById(R.id.id_back);
        back.setOnClickListener(this);
        TextView complete = view.findViewById(R.id.id_complete);
        complete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Photo photo = (Photo) getArguments().getSerializable(MediaConstant.PHOTO);
        if (photo != null){
            mControlView.setVisibility(View.GONE);
            playView.setVisibility(View.VISIBLE);
            playView.setEnabled(false);
            mCoverView.setVisibility(View.VISIBLE);
            mCoverView.loadImageWithCallback(photo.getFilePath(), new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    mCoverView.setImageBitmap(resource);
                    if (photo.getVideoWith() > 0 && photo.getVideoHeight() > 0 ){
                        mTextureView.setRationSize(photo.getVideoWith(),photo.getVideoHeight());
                    }else{
                        mTextureView.setRationSize(resource.getWidth(),resource.getHeight());
                    }
                    playView.setEnabled(true);
                }
            });
            mTitle.setText(photo.getDisplayName());
            //初始化播放器
            if (mediaPlayer == null){
                mediaPlayer = new MediaPlayer();
            }
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(photo.getFilePath());
                mediaPlayer.setScreenOnWhilePlaying(true);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        stopTimeTask();
        if (mediaPlayer != null){
            mediaPlayer.pause();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null){
            mediaPlayer.pause();
        }
        stopTimeTask();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.id_back){
            getActivity().onBackPressed();
        }else if (view.getId() == R.id.id_container){
            animationHeaderAndFooter();
        }else if (view.getId() == R.id.id_complete){
            Photo photo = (Photo) getArguments().getSerializable(MediaConstant.PHOTO);
            mediaPlayer.pause();
            stopTimeTask();
            if (photo != null){
                //选择完成
                if (mediaSelectListener != null){
                    mediaSelectListener.selectMedia(photo);
                    mediaSelectListener.selectComplete();
                }
            }
        }else if (view.getId() == R.id.id_play_view){
            mControlView.setVisibility(View.VISIBLE);
            playView.setVisibility(View.GONE);
            mCoverView.setVisibility(View.GONE);
            if (mSurfaceTexture != null){
                mediaPlayer.setSurface(new Surface(mSurfaceTexture));
            }
            mediaPlayer.start();
            startTimeTask();
        }else if (view.getId() == R.id.id_play_status_view){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                stopTimeTask();
            }else{
                mediaPlayer.start();
                startTimeTask();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        if (fromUser){
            mediaPlayer.seekTo(mediaPlayer.getDuration() * i / seekBar.getMax());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        mSurfaceTexture = surfaceTexture;
        if (mediaPlayer != null){
            mediaPlayer.setSurface(new Surface(surfaceTexture));
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        mSurfaceTexture = surfaceTexture;
        if (mediaPlayer != null){
            mediaPlayer.setSurface(new Surface(surfaceTexture));
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = null;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
        if (mediaPlayer != null){
            mediaPlayer.setSurface(new Surface(surfaceTexture));
        }
    }

    /**
     * 获取如1:00时间字符串
     * @param time 单位毫秒
     * @return
     */
    private String getFormatTime(long time){
        if (time > 1000){
            long seconds = time/1000;
            if (seconds > 60){
                long minutes = seconds / 60;
                long letSeconds = seconds % 60;
                if (letSeconds >= 10 && letSeconds < 60){
                    return minutes >= 10 ? minutes+":"+letSeconds : "0"+minutes+":"+letSeconds;
                }
                return minutes >= 10 ? minutes+":0"+letSeconds : "0"+minutes+":0"+letSeconds;
            }else{
                if (seconds>=10){
                    return "00:"+seconds;
                }
                return "00:0"+seconds;
            }
        }
        return "00:00";
    }

    //开始时间计时
    private void startTimeTask(){
        playStatusView.setImageResource(R.drawable.icon_status_pause);
        if (myTimer != null){
            myTimer.cancel();
        }
        myTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mHander.sendEmptyMessage(0);
            }
        };
        myTimer.schedule(timerTask,0,1000);
    }

    //结束时间计时
    private void stopTimeTask(){
        playStatusView.setImageResource(R.drawable.icon_status_play);
        mHander.removeCallbacksAndMessages(null);
        if (myTimer != null){
            myTimer.cancel();
            myTimer = null;
        }
    }

    /**
     * 播放结束重置状态
     * @param mediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopTimeTask();
        long duration = mediaPlayer.getDuration() > 0 ? mediaPlayer.getDuration() : 0;
        mTimeView.setText(getFormatTime(duration));
        mCurrentTimeView.setText(getFormatTime(0));
        mProgressView.setProgress(0);
    }

    /**
     * 点击时隐藏或显示头部和底部
     */
    private void animationHeaderAndFooter(){
        int visibility = headerView.getVisibility();
        if (visibility == View.VISIBLE){
            headerView.setVisibility(View.INVISIBLE);
            mControlView.setVisibility(View.INVISIBLE);
        }else{
            headerView.setVisibility(View.VISIBLE);
            if (playView.getVisibility() != View.VISIBLE){
                mControlView.setVisibility(View.VISIBLE);
            }
        }
    }
}
