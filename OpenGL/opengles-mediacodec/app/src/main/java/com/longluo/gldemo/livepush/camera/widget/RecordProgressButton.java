package com.longluo.gldemo.livepush.camera.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class RecordProgressButton extends View {
    private final float INNER_RADIUS_MIN = 15;
    private final float INNER_RADIUS_MAX = 25;

    private final float OUTER_RADIUS_MIN = 35;
    private final float OUTER_RADIUS_MAX = 55;

    private float mInnerRadius;
    private float mOuterRadius;
    private long mMaxProgress = 60000;
    private long mCurrentProgress = 0;

    private boolean mIsRecording = false;

    private Paint mPaint;

    public RecordProgressButton(Context context) {
        this(context, null);
    }

    public RecordProgressButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordProgressButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInnerRadius = dip2px(INNER_RADIUS_MAX);
        mOuterRadius = dip2px(OUTER_RADIUS_MIN);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    private int dip2px(float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.parseColor("#dfdfdf"));
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredWidth() / 2, mOuterRadius, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredWidth() / 2, mInnerRadius, mPaint);

        if (mIsRecording && mCurrentProgress > 0) {
            // 画进度
            float top = getMeasuredHeight() / 2 - mOuterRadius;
            float left = getMeasuredWidth() / 2 - mOuterRadius;
            float right = left + mOuterRadius * 2;
            float bottom = top + mOuterRadius * 2;
            RectF rectF = new RectF(left, top, right, bottom);

            int startAngle = -90;
            int sweepAngle = (int) (mCurrentProgress * 360f / mMaxProgress);

            mPaint.setStrokeWidth(dip2px(4));
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.parseColor("#62c554"));
            canvas.drawArc(rectF, startAngle, sweepAngle, false, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fingerDownAnim();
                break;
            case MotionEvent.ACTION_UP:
                onEnd();
                break;
        }

        return true;
    }

    /**
     * 按下的动画，
     */
    private void fingerDownAnim() {
        if (mRecordListener != null) {
            mRecordListener.onStart();
        }

        // 外原放大，内圆缩小
        ValueAnimator innerAnimator = ObjectAnimator.ofFloat(mInnerRadius, dip2px(INNER_RADIUS_MIN));
        ValueAnimator outerAnimator = ObjectAnimator.ofFloat(mOuterRadius, dip2px(OUTER_RADIUS_MAX));
        innerAnimator.setDuration(200);
        outerAnimator.setDuration(200);

        innerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mInnerRadius = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        outerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOuterRadius = (float) animation.getAnimatedValue();
            }
        });

        innerAnimator.start();
        outerAnimator.start();

        outerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsRecording = true;
                mInnerRadius = dip2px(INNER_RADIUS_MIN);
                mOuterRadius = dip2px(OUTER_RADIUS_MAX);
            }
        });
    }

    /**
     * 抬起的动画，
     */
    private void fingerUpAnim() {
        // 外原缩小，内圆放大
        ValueAnimator innerAnimator = ObjectAnimator.ofFloat(mInnerRadius, dip2px(INNER_RADIUS_MAX));
        ValueAnimator outerAnimator = ObjectAnimator.ofFloat(mOuterRadius, dip2px(OUTER_RADIUS_MIN));
        innerAnimator.setDuration(200);
        outerAnimator.setDuration(200);

        innerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mInnerRadius = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });

        outerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOuterRadius = (float) animation.getAnimatedValue();
            }
        });

        innerAnimator.start();
        outerAnimator.start();

        outerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mInnerRadius = dip2px(INNER_RADIUS_MAX);
                mOuterRadius = dip2px(OUTER_RADIUS_MIN);
            }
        });
    }

    public interface RecordListener {
        void onStart();

        void onEnd();
    }

    private RecordListener mRecordListener;

    public void setOnRecordListener(RecordListener mRecordListener) {
        this.mRecordListener = mRecordListener;
    }

    public void setMaxProgress(int mMaxProgress) {
        this.mMaxProgress = mMaxProgress;
    }

    public void setCurrentProgress(long currentProgress) {
        this.mCurrentProgress = currentProgress;
        postInvalidate();
        if (mCurrentProgress >= mMaxProgress) {
            onEnd();
        }
    }

    private void onEnd() {
        mCurrentProgress = 0;
        mIsRecording = false;
        fingerUpAnim();
        if (mRecordListener != null) {
            mRecordListener.onEnd();
        }
        postInvalidate();
    }
}
