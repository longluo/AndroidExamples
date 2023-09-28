package com.longluo.gldemo.livepush.camera.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.longluo.gldemo.R;


/**
 * 相机对焦view
 */
public class CameraFocusView extends View {
    private int mStrokeWidth;
    private int prepareColor;
    private int finishColor;
    private int mPaintColor;
    private boolean isFocusing;
    private Paint mPaint;
    private int mDuration;
    private Handler mDurationHandler;

    public CameraFocusView(Context context) {
        super(context);
        init(context, null);
    }

    public CameraFocusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CameraFocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.camera_focus_view);
        this.mStrokeWidth = (int) typedArray.getDimension(R.styleable.camera_focus_view_stroke_width, 5);
        this.prepareColor = typedArray.getColor(R.styleable.camera_focus_view_prepare_color, Color.RED);
        this.finishColor = typedArray.getColor(R.styleable.camera_focus_view_finish_color, Color.YELLOW);

        this.mPaint = new Paint();
        this.mPaintColor = prepareColor;
        this.mDuration = 1000;
        this.mDurationHandler = new Handler(Looper.getMainLooper());
        this.setVisibility(GONE);
    }

    public void beginFocus(int centerX, int centerY) {
        mPaintColor = prepareColor;
        isFocusing = true;
        int x = centerX - getMeasuredWidth()/2;
        int y = centerY - getMeasuredHeight()/2;
        setX(x);
        setY(y);
        setVisibility(VISIBLE);
        invalidate();
    }

    public void endFocus(boolean isSuccess) {
        isFocusing = false;
        if (isSuccess) {
            mPaintColor = finishColor;
            mDurationHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isFocusing) {
                        setVisibility(GONE);
                    }
                }
            }, mDuration);
            invalidate();
        } else {
            setVisibility(GONE);
        }
    }
    public void cancelFocus() {
        isFocusing = false;
        setVisibility(GONE);
    }
    public void setDuration(int duration) {
        mDuration = duration;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mPaintColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawLine(0, 0, width/3, 0, mPaint);
        canvas.drawLine(width*2/3, 0, width, 0, mPaint);
        canvas.drawLine(0, height, width/3, height, mPaint);
        canvas.drawLine(width*2/3, height, width, height, mPaint);

        canvas.drawLine(0, 0, 0, height/3, mPaint);
        canvas.drawLine(0, height*2/3, 0, height, mPaint);
        canvas.drawLine(width, 0, width, height/3, mPaint);
        canvas.drawLine(width, height*2/3, width, height, mPaint);
    }
}
