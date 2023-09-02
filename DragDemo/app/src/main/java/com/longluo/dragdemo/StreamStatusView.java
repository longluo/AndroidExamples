package com.longluo.dragdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class StreamStatusView extends LinearLayout {

    private static final String TAG = StreamStatusView.class.getSimpleName();

    private Context mContext;

    private int width;

    private int height;

    private int screenWidth;

    private int screenHeight;

    private float mLastX;

    private float mLastY;

    private boolean isDrag = false;
    private View mRootView;

    private TextView mTvLatency;

    private TextView mTvFps;

    private TextView mTvQuality;

    private ImageView mIvClose;

    private int mLatency;

    private int mFps;

    private long mLastTime;

    private long mCurrentTime;

    private OnClickListener mListener;

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
                Log.d(TAG, "mIvClose & Close");

                if (mRootView != null) {
                    mRootView.setVisibility(View.GONE);
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

    public boolean isDrag() {
        return isDrag;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();

        screenWidth = ScreenUtil.getScreenWidth(mContext);
        screenHeight = ScreenUtil.getScreenHeight(mContext);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getAction();

        if (this.isEnabled()) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isDrag = false;
                    mLastX = event.getX();
                    mLastY = event.getY();
                    mLastTime = System.currentTimeMillis();
                    break;

                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "ACTION_MOVE");

                    final float xDistance = event.getX() - mLastX;
                    final float yDistance = event.getY() - mLastY;

                    int l, r, t, b;

                    //当水平或者垂直滑动距离大于10,才算拖动事件
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

                        this.layout(l, t, r, b);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    mCurrentTime = System.currentTimeMillis();

                    if (mCurrentTime - mLastTime < 800L && Math.abs(event.getX() - mLastX) < 10
                            && Math.abs(event.getY() - mLastY) < 10) {

                        Log.d(TAG, "ACTION_UP current Time = " + mCurrentTime);
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

        return false;
    }

    public void setStreamLatency(int latency) {
        Log.d(TAG, "setStreamLatency latency = " + latency);

        mLatency = latency;

        mTvLatency.setText(String.valueOf(mLatency));
    }

    public void setStreamFps(int fps) {
        Log.d(TAG, "setStreamLatency fps =" + fps);

        mFps = fps;

        mTvFps.setText(String.valueOf(fps));
    }

    public void setClickListener(OnClickListener listener) {
        mListener = listener;
    }

}
