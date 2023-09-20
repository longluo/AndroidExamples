package com.longluo.dragdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import timber.log.Timber;


public class FloatBallView extends LinearLayout {

    private static final String TAG = FloatBallView.class.getSimpleName();

    private Context mContext;

    private int width;

    private int height;

    private int screenWidth;

    private int screenHeight;

    private float mLastX;

    private float mLastY;

    private boolean isDrag = false;

    private View mRootView;

    private ImageView mIvRingLatency;

    private TextView mTvLatency;

    private int mLatency;

    private long mLastTime;

    private long mCurrentTime;

    private static final int TIME_SLOP = 800;

    private OnClickListener mListener;

    public FloatBallView(Context context) {
        this(context, null);
    }

    public FloatBallView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatBallView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.float_ball, this);

        mRootView = rootView.findViewById(R.id.root_float_ball);

        mIvRingLatency = rootView.findViewById(R.id.iv_ring_status);

        mTvLatency = rootView.findViewById(R.id.tv_latency);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        Log.d(TAG, "onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        Log.d(TAG, "onDetachedFromWindow");
    }

    public boolean isDrag() {
        return isDrag;
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        Log.d(TAG, "onHoverEvent: " + event.getAction());

        int action = event.getAction();

        if (action == MotionEvent.ACTION_HOVER_ENTER) {

        } else if (action == MotionEvent.ACTION_HOVER_EXIT) {

        }

        return super.onHoverEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();

        screenWidth = ScreenUtil.getScreenWidth(mContext);
        screenHeight = ScreenUtil.getScreenHeight(mContext);

        Timber.i("onMeasure: FloatBall w = %s, h = %s, mScreenWidth: w = %s, h = %s",
                width, height, screenWidth, screenHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        Log.d(TAG, "onLayout: FloatBall changed = " + changed + ", l=" + l + ",t=" + t + ", r = " + r + ", b = " + b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        float currentX = event.getX();
        float currentY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                mLastX = event.getX();
                mLastY = event.getY();
                mLastTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "ACTION_MOVE");

                final float xDistance = currentX - mLastX;
                final float yDistance = currentY - mLastY;

                mCurrentTime = System.currentTimeMillis();

                int l, r, t, b;

                // 当水平或者垂直滑动距离大于10,才算拖动事件
                if (Math.abs(xDistance) > 10 || Math.abs(yDistance) > 10) {
                    Log.d(TAG, "Drag");

                    isDrag = true;
                    l = (int) (getLeft() + xDistance);
                    r = l + width;
                    t = (int) (getTop() + yDistance);
                    b = t + height;

                    //不划出边界判断,此处应按照项目实际情况,因为本项目需求移动的位置是手机全屏,
                    // 所以才能这么写,如果是固定区域,要得到父控件的宽高位置后再做处理
                    if (l < 0) {
                        l = 0;
                        r = l + width;
                    } else if (r > screenWidth) {
                        r = screenWidth;
                        l = r - width;
                    }
                    if (t < 0) {
                        t = 0;
                        b = t + height;
                    } else if (b > screenHeight) {
                        b = screenHeight;
                        t = b - height;
                    }

                    Log.d(TAG, "ACTION_MOVE & layout l=" + l + ",t=" + t + ",r=" + r + ",b=" + b);
                    this.layout(l, t, r, b);
                }
                break;

            case MotionEvent.ACTION_UP:
                mCurrentTime = System.currentTimeMillis();

                long deltaTime = mCurrentTime - mLastTime;

                if (deltaTime < 800L && Math.abs(event.getX() - mLastX) < 10
                        && Math.abs(event.getY() - mLastY) < 10) {
                    Log.d(TAG, "ACTION_UP Click = " + deltaTime);
                    performClick();
                }
                setPressed(false);
                break;

            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                break;

            default:
                break;
        }

        return true;
    }

    public void setLatency(int latency) {
        Log.d(TAG, "setLatency: mLatency = " + mLatency + ", latency = " + latency);

        mLatency = latency;

        mTvLatency.setText(String.valueOf(mLatency));

//        if (mLatency < Constants.STREAM_LATENCY_GREEN) {
//            mTvLatency.setTextColor(getResources().getColor(R.color.green, null));
//        } else if (mLatency < Constants.STREAM_LATENCY_BLUE) {
//            mTvLatency.setTextColor(getResources().getColor(R.color.blue, null));
//        } else if (mLatency < Constants.STREAM_LATENCY_YELLOW) {
//            mTvLatency.setTextColor(getResources().getColor(R.color.yellow, null));
//        } else {
//            mTvLatency.setTextColor(getResources().getColor(R.color.red, null));
//        }
    }

    public void setClickListener(OnClickListener listener) {
        mListener = listener;
    }
}
