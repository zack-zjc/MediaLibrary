package com.realcloud.media.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.realcloud.loochadroid.utils.ConvertUtil;

/**
 * Created by zack on 2017/10/9.
 * 处理点击与长按时间的view
 */

public class CustomMediaTouchView extends ImageView implements View.OnTouchListener{

    //当前view状态
    private static final int STATE_NORMAL = 0;
    private static final int STATE_RECODING = 1;

    //进度条宽度
    private static final int STROKEN_WIDTH = ConvertUtil.convertDpToPixel(5);

    private static final int TIME = 500;

    private long mDownTime;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private OnCustomPressListener onCustomPressListener;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //当前状态
    private int mState = STATE_NORMAL;

    //当前录制视频进度条百分比
    private float mProgressPercent = 0f;

    //画进度条的区域
    private RectF arcRect = new RectF();

    public CustomMediaTouchView(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public CustomMediaTouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public CustomMediaTouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    public void setOnCustomPressListener(OnCustomPressListener onCustomPressListener) {
        this.onCustomPressListener = onCustomPressListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        arcRect.left = STROKEN_WIDTH/2;
        arcRect.top = STROKEN_WIDTH/2;
        arcRect.right = getMeasuredWidth() - STROKEN_WIDTH/2;
        arcRect.bottom = getMeasuredHeight()- STROKEN_WIDTH/2;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            mDownTime = motionEvent.getEventTime();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (onCustomPressListener != null){
                        mState = STATE_RECODING;
                        mProgressPercent = 0;
                        onCustomPressListener.onLongPressStart();
                    }
                }
            },TIME);
        }else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL){
            long time = motionEvent.getEventTime();
            if (time - mDownTime > TIME){
                if (onCustomPressListener != null){
                    mState = STATE_NORMAL;
                    mProgressPercent = 0;
                    onCustomPressListener.onLongPressEnd();
                }
            }else{
                mHandler.removeCallbacksAndMessages(null);
                if (onCustomPressListener != null){
                    onCustomPressListener.onClick();
                }
            }
        }
        return true;
    }

    public void resetState(){
        mState = STATE_NORMAL;
        this.mProgressPercent = 0;
        postInvalidate();
    }

    public void setProgressPercent(float percent){
        mState = STATE_RECODING;
        this.mProgressPercent = percent;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#99ffffff"));
        canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2, ConvertUtil.convertDpToPixel(100)/2,mPaint);
        if (mState == STATE_NORMAL){
            mPaint.setColor(Color.parseColor("#e5ffffff"));
            canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2, ConvertUtil.convertDpToPixel(60)/2,mPaint);
        }else if (mState == STATE_RECODING){
            mPaint.setColor(Color.parseColor("#e5ffffff"));
            canvas.drawCircle(getMeasuredWidth()/2,getMeasuredHeight()/2, ConvertUtil.convertDpToPixel(35)/2,mPaint);
            mPaint.setStrokeWidth(STROKEN_WIDTH);
            mPaint.setColor(Color.parseColor("#ffde08"));
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawArc(arcRect, -90, 360*mProgressPercent, false, mPaint);
        }
    }

    public interface OnCustomPressListener{
        void onClick();
        void onLongPressStart();
        void onLongPressEnd();
    }
}
