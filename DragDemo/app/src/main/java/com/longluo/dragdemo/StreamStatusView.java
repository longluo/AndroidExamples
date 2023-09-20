package com.longluo.dragdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import timber.log.Timber;

public class StreamStatusView extends LinearLayout {

    private static final String TAG = StreamStatusView.class.getSimpleName();

    private Context mContext;

    private int mViewWidth;

    private int mViewHeight;

    private int screenWidth;

    private int screenHeight;

    private float mLastX;

    private float mLastY;

    private boolean isDrag = false;

    private boolean isClickInStreamStatus = false;

    private View mRootView;

    private TextView mTvLatency;

    private TextView mTvFps;

    private TextView mTvQuality;

    private ImageView mIvClose;

    private int mLatency;

    private int mFps;

    private long mLastTime;

    private long mCurrentTime;

    private OnListener mListener;

    public StreamStatusView(Context context) {
        this(context, null);
    }

    public StreamStatusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StreamStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        initView();
    }

    protected void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.float_stream_status, this);

        mRootView = rootView.findViewById(R.id.stream_status_root);

        mTvLatency = rootView.findViewById(R.id.tv_float_status_latency);

        mTvFps = rootView.findViewById(R.id.tv_float_status_fps);

        mTvQuality = rootView.findViewById(R.id.tv_float_status_quality);

        mIvClose = rootView.findViewById(R.id.iv_float_status_close);

        mIvClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "StreamStatusView mIvClose & Close");

                if (mListener != null) {
                    mListener.hide();
                }
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mViewWidth = getWidth();
        mViewHeight = getHeight();

        Log.d(TAG, "onDraw: w = " + mViewWidth + ", h = " + mViewHeight);
    }

    public boolean isDrag() {
        return isDrag;
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        Timber.i("StatusView onHoverEvent: %s", event.getAction());

        int action = event.getAction();

        if (action == MotionEvent.ACTION_HOVER_ENTER) {

        } else if (action == MotionEvent.ACTION_HOVER_EXIT) {

        }

        return super.onHoverEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        Log.d(TAG, "onMeasure: w = " + mViewWidth + ", h = " + mViewHeight);

        screenWidth = ScreenUtil.getScreenWidth(mContext);
        screenHeight = ScreenUtil.getScreenHeight(mContext);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        Log.d(TAG, "onLayout: StreamStatus changed = " + changed + ", l=" + l + ",t=" + t + ", r = " + r + ", b = " + b);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Timber.i(TAG, "StatusView dispatchTouchEvent: (%s, %s), action = %s", event.getX(), event.getY(), event.getAction());

//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//            return true;
//        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        float currentX = event.getX();
        float currentY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Timber.i("fix StatusView ACTION_DOWN");
                isDrag = false;
                mLastX = event.getX();
                mLastY = event.getY();
                mLastTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
                Timber.i("fix StatusView ACTION_MOVE");

                final float xDistance = currentX - mLastX;
                final float yDistance = currentY - mLastY;

                mCurrentTime = System.currentTimeMillis();

                int l, r, t, b;

                // 当水平或者垂直滑动距离大于10,才算拖动事件
                if (Math.abs(xDistance) > 10 || Math.abs(yDistance) > 10) {
                    Timber.i("StatusView Drag");

                    isDrag = true;
                    l = (int) (getLeft() + xDistance);
                    r = l + mViewWidth;
                    t = (int) (getTop() + yDistance);
                    b = t + mViewHeight;

                    //不划出边界判断,此处应按照项目实际情况,因为本项目需求移动的位置是手机全屏,
                    // 所以才能这么写,如果是固定区域,要得到父控件的宽高位置后再做处理
                    if (l < 0) {
                        l = 0;
                        r = l + mViewWidth;
                    } else if (r > screenWidth) {
                        r = screenWidth;
                        l = r - mViewWidth;
                    }
                    if (t < 0) {
                        t = 0;
                        b = t + mViewHeight;
                    } else if (b > screenHeight) {
                        b = screenHeight;
                        t = b - mViewHeight;
                    }

                    this.layout(l, t, r, b);
                }
                break;

            case MotionEvent.ACTION_UP:
                Timber.i("fix StatusView ACTION_UP");

                mCurrentTime = System.currentTimeMillis();

                long deltaTime = mCurrentTime - mLastTime;

                if (deltaTime < 800L && Math.abs(event.getX() - mLastX) < 10
                        && Math.abs(event.getY() - mLastY) < 10) {
                    Timber.i("StatusView ACTION_UP Click %s", deltaTime);
                }
                setPressed(false);
                break;

            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                break;

            default:
                break;
        }

        Timber.i("StatusView onTouchEvent");

        return true;
    }

    public int[] getmViewSize() {
        return new int[]{mViewWidth, mViewHeight};
    }

    public void setmViewWidth(int mViewWidth) {
        this.mViewWidth = mViewWidth;
    }

    public void setStreamLatency(int latency) {
        Timber.d("setStreamLatency latency = " + latency);

        mLatency = latency;

        mTvLatency.setText(String.valueOf(mLatency));
    }

    public void setStreamFps(int fps) {
        Timber.d("setStreamLatency fps =" + fps);

        mFps = fps;

        mTvFps.setText(String.valueOf(fps));
    }

    public void setListener(OnListener listener) {
        mListener = listener;
    }

    public interface OnListener {
        void hide();
    }
}
