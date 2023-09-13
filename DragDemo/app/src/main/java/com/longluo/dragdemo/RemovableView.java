package com.longluo.dragdemo;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;

/*
 * 可拖动且自动贴边的自定义View
 * */
public class RemovableView extends FrameLayout {

    private final Context mContext;

    public RemovableView(Context context) {
        this(context, null);
    }

    public RemovableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RemovableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.removable_view, this);
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    // ----------------------------- 拖拽 -----------------------------

    private boolean mIsDrug = true;
    private boolean mCustomIsAttach = true;//是否需要自动吸
    private boolean mCustomIsDrag = true;//是否可拖曳

    private float mLastRawX;
    private float mLastRawY;
    private int mRootMeasuredWidth = 0;
    private int mRootMeasuredHeight = 0;
    private int mRootTopY = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //判断是否需要滑动
        if (mCustomIsDrag) {
            //当前手指的坐标
            float mRawX = ev.getRawX();
            float mRawY = ev.getRawY();

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN://手指按下
                    mIsDrug = false;
                    //记录按下的位置
                    mLastRawX = mRawX;
                    mLastRawY = mRawY;
                    ViewGroup mViewGroup = (ViewGroup) getParent();
                    if (mViewGroup != null) {
                        int[] location = new int[2];
                        mViewGroup.getLocationInWindow(location);
                        //获取父布局的高度
                        mRootMeasuredHeight = mViewGroup.getMeasuredHeight();
                        mRootMeasuredWidth = mViewGroup.getMeasuredWidth();
                        //获取父布局顶点的坐标
                        mRootTopY = location[1];
                    }
                    break;

                case MotionEvent.ACTION_MOVE://手指滑动
                    if (mRawX >= 0 && mRawX <= mRootMeasuredWidth && mRawY >= mRootTopY && mRawY <= (mRootMeasuredHeight + mRootTopY)) {
                        //手指X轴滑动距离
                        float differenceValueX = mRawX - mLastRawX;
                        //手指Y轴滑动距离
                        float differenceValueY = mRawY - mLastRawY;
                        //判断是否为拖动操作
                        if (!mIsDrug) {
                            mIsDrug = !(Math.sqrt(differenceValueX * differenceValueX + differenceValueY * differenceValueY) < 2);
                        }
                        //获取手指按下的距离与控件本身X轴的距离
                        float ownX = getX();
                        //获取手指按下的距离与控件本身Y轴的距离
                        float ownY = getY();
                        //理论中X轴拖动的距离
                        float endX = ownX + differenceValueX;
                        //理论中Y轴拖动的距离
                        float endY = ownY + differenceValueY;
                        //X轴可以拖动的最大距离
                        float maxX = mRootMeasuredWidth - getWidth();
                        //Y轴可以拖动的最大距离
                        float maxY = mRootMeasuredHeight - getHeight();
                        //X轴边界限制
                        endX = endX < 0 ? 0 : Math.min(endX, maxX);
                        //Y轴边界限制
                        endY = endY < 0 ? 0 : Math.min(endY, maxY);
                        //开始移动
                        setX(endX);
                        setY(endY);
                        //记录位置
                        mLastRawX = mRawX;
                        mLastRawY = mRawY;
                    }
                    break;

                case MotionEvent.ACTION_UP://手指离开
                    //根据自定义属性判断是否需要贴边
                    if (mCustomIsAttach) {
                        //判断是否为点击事件
                        if (mIsDrug) {
                            float center = (mRootMeasuredWidth >> 1);
                            //自动贴边
                            if (mLastRawX <= center) {
                                mLastRawX = 0;
                                //向左贴边
                                this.animate()
                                        .setInterpolator(new BounceInterpolator())
                                        .setDuration(500)
                                        .x(mLastRawX)
                                        .start();
                            } else {
                                mLastRawX = mRootMeasuredWidth - getWidth();
                                //向右贴边
                                this.animate()
                                        .setInterpolator(new BounceInterpolator())
                                        .setDuration(500)
                                        .x(mLastRawX)
                                        .start();
                            }
                        }
                    }

                    // 如果要保存最后坐标点
                    /*if (mIsDrug) {
                        mDefaultPreference.putFloat(IPreferencesConsts.REMOVABLE_VIEW_COORDINATE_X, mLastRawX);
                        mDefaultPreference.putFloat(IPreferencesConsts.REMOVABLE_VIEW_COORDINATE_Y, mLastRawY - ev.getY());
                        mDefaultPreference.commit();
                    }*/
                    break;
            }
        }

        //是否拦截事件
        return mIsDrug ? mIsDrug : super.onTouchEvent(ev);
    }

}


