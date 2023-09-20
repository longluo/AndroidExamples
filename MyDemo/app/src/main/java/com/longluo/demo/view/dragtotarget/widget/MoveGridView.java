package com.longluo.demo.view.dragtotarget.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * 长按能选中item, 拖拽
 **/
public class MoveGridView extends GridView {
    private WindowManager mWindowManager;
    /**
     * item镜像的布局参数
     */
    private WindowManager.LayoutParams mWindowLayoutParams;
    /**
     * 震动器
     */
    private Vibrator mVibrator;
    // 震动的时间，默认为100ms
    private long vibratorMs = 100;
    // 设置长按时间为1秒
    private long responseMS = 1000;
    private static boolean isMove = false;
    // 按下去的x,y
    private int mDownX = 0;
    private int mDownY = 0;
    // 移动的时候的x,y
    private int mMoveX = 0;
    private int mMoveY = 0;
    // 抬起的x,y
    private int mUpX = 0;
    private int mUpY = 0;
    private int mPoint2ItemTop;
    private int mPoint2ItemLeft;
    private int mOffset2Top;
    private int mOffset2Left;
    /**
     * 状态栏的高度
     */
    private int mStatusHeight;

    public MoveGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mVibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context); // 获取状态栏的高度
    }

    // 要移动的item的位置,默认为INVALID_POSITION=-1
    private int mMovePosition = INVALID_POSITION;
    /**
     * 刚开始拖拽的item对应的View
     */
    private View mStartMoveItemView = null;
    private ImageView mMoveImageView = null;
    private Bitmap mMoveBitmap;
    private Handler mHandler = new Handler();
    // 判断是否能开始移动元素
    private Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            isMove = true;
            mVibrator.vibrate(vibratorMs);
            // 根据我们按下的点显示item镜像
            createDragImage(mMoveBitmap, mDownX, mDownY);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();
                System.out.println("ACTION_DOWN");
                // 根据按下的X,Y坐标获取所点击item的position
                mMovePosition = pointToPosition(mDownX, mDownY);
                // 如果选中的为非法的位置。则不处理消息
                if (mMovePosition == AdapterView.INVALID_POSITION) {
                    break;
                }
                mHandler.postDelayed(mLongClickRunnable, responseMS);
                mStartMoveItemView = getChildAt(mMovePosition
                        - getFirstVisiblePosition());
                mPoint2ItemTop = mDownY - mStartMoveItemView.getTop();
                mPoint2ItemLeft = mDownX - mStartMoveItemView.getLeft();
                mOffset2Top = (int) (ev.getRawY() - mDownY);
                mOffset2Left = (int) (ev.getRawX() - mDownX);
                // 开启mMoveItemView绘图缓存
                mStartMoveItemView.setDrawingCacheEnabled(true);
                // 获取mMoveItemView在缓存中的Bitmap对象
                mMoveBitmap = Bitmap.createBitmap(mStartMoveItemView
                        .getDrawingCache());
                // 这一步很关键，释放绘图缓存，避免出现重复的镜像
                mStartMoveItemView.destroyDrawingCache();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = (int) ev.getX();
                mMoveY = (int) ev.getY();
                // 如果我们在按下的item上面移动，只要不超过item的边界我们就不移除mRunnable
                // 依然能监听到longClick
                if (!isTouchInItem(mStartMoveItemView, mMoveX, mMoveY)) {
                    mHandler.removeCallbacks(mLongClickRunnable);
                }
                // //禁止Gridview侧边进行滑动,移动的时候不许发生侧滑事件
                if (isMove) {
                    onDragItem(mMoveX, mMoveY);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                mUpX = (int) ev.getX();
                mUpY = (int) ev.getY();
                mHandler.removeCallbacks(mLongClickRunnable);
                if (isMove) {
                    deleteIfNeed();
                }
                removeDragImage();
                isMove = false;
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断是否要删除，满足条件删除
     */
    private void deleteIfNeed() {
        int y = mUpY - mPoint2ItemTop + mOffset2Top
                - mStatusHeight;
        if (y < 50) {
            if (mUninstallListener != null)
                mUninstallListener.onUninstallListener(mMovePosition);
        }
    }

    /**
     * 是否点击在GridView的item上面
     *
     * @param dragView
     * @param x
     * @param y
     * @return
     */
    private boolean isTouchInItem(View dragView, int x, int y) {
        if (dragView == null) {
            return false;
        }
        int leftOffset = dragView.getLeft();
        int topOffset = dragView.getTop();
        if (x < leftOffset || x > leftOffset + dragView.getWidth()) {
            return false;
        }
        if (y < topOffset || y > topOffset + dragView.getHeight()) {
            return false;
        }
        return true;
    }

    /**
     * 创建拖动的镜像
     *
     * @param bitmap
     * @param downX  按下的点相对父控件的X坐标
     * @param downY  按下的点相对父控件的X坐标
     */
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; // 图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top
                - mStatusHeight;
        mWindowLayoutParams.alpha = 0.55f; // 透明度
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mMoveImageView = new ImageView(getContext());
        mMoveImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mMoveImageView, mWindowLayoutParams);
    }

    /**
     * 从界面上面移动拖动镜像
     */
    private void removeDragImage() {
        if (mMoveImageView != null) {
            mWindowManager.removeView(mMoveImageView);
            mMoveImageView = null;
        }
    }

    /**
     * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
     *
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY) {
        mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top
                - mStatusHeight;
        mWindowManager.updateViewLayout(mMoveImageView, mWindowLayoutParams); // 更新镜像的位置
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass
                        .getField("status_bar_height").get(localObject)
                        .toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 设置响应拖拽的毫秒数，默认是1000毫秒
     *
     * @param responseMS
     */
    public void setResponseMS(long responseMS) {
        this.responseMS = responseMS;
    }

    /**
     * 设置震动时间的毫秒数，默认是1000毫秒
     *
     * @param vibratorMs
     */
    public void setVibrator(long vibratorMs) {
        this.vibratorMs = vibratorMs;
    }

    public void setOnUninstallListener(UninstallListener l) {
        mUninstallListener = l;
    }

    ;
    private UninstallListener mUninstallListener;
}
