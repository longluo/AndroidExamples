package com.longluo.demo.view.dragchoose.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.longluo.demo.R;
import com.longluo.demo.view.dragchoose.adapter.DragAdapter;
import com.longluo.demo.view.dragchoose.bean.LeftBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 可拖拽item出列表的自定义View （其中有GridView）
 */
public class DragView extends FrameLayout {
    private static final int TAG_KEY = R.id.my_c_id;
    private NoScrollGridView mGridView;
    private final List<View> mChilds = new ArrayList<>();
    protected int mNumColumns = 3;
    protected int mColHeight = 0;
    protected int mColWidth = 0;
    protected int mChildCount = 0;
    protected int mMaxHeight = 0;
    private boolean isViewInitDone = false;
    private ListenScrollView mScrollView;
    private int mCurrentY = 0;
    /**
     * 自动滚屏的动画
     */
    private ValueAnimator animator;
    private DragAdapter adapter;
    private int headDragPosition = 0;
    private int footDragPosition = 0;
    //当前拖拽item的index, 无拖拽默认是-1
    private int currentDragPosition = -1;
    //当前拖拽item的value
    private String currentDragValue = "";

    /**
     * todo: not use
     * 是否有位置发生改变,否则不用重绘 （这个是用于gridview内部拖拽改变位置的，eiken本次对应用不上）
     */
    private boolean hasPositionChange = false;
    /**
     * gridview能否滚动,是否内容太多
     */
    private boolean canScroll = true;
    /**
     * 动画时间
     */
    private static final long ANIM_DURING = 250;
    private Object swapeData;

    public DragView(@NonNull Context context) {
        this(context, null);
    }

    public DragView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DragView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Context context = getContext();
        mGridView = new NoScrollGridView(context);

        mGridView.setVerticalScrollBarEnabled(false);
        mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        mGridView.setSelector(new ColorDrawable());

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (mChilds.isEmpty()) {
                for (int i = 0; i < mGridView.getChildCount(); i++) {
                    View view = mGridView.getChildAt(i);
                    view.setTag(TAG_KEY, new int[]{0, 0});
                    view.clearAnimation();
                    mChilds.add(view);
                }
            }
            if (!mChilds.isEmpty()) {
                mColHeight = mChilds.get(0).getHeight();
            }
            mColWidth = mGridView.getColumnWidth();
            if (mChildCount % mNumColumns == 0) {
                mMaxHeight = mColHeight * mChildCount / mNumColumns;
            } else {
                mMaxHeight = mColHeight * (mChildCount / mNumColumns + 1);
            }
            canScroll = mMaxHeight - getHeight() > 0;
            // 告知事件处理,完成View加载,许多属性也已经初始化了
            isViewInitDone = true;
        });
        mScrollView = new ListenScrollView(context);
        mGridView.setNumColumns(mNumColumns);
        //为mScrollView动态添加子view(mGridView)
        mScrollView.addView(mGridView, -1, -1);
        //动态添加view(mScrollView)
        addView(mScrollView, -1, -1);
    }

    private DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            mChildCount = adapter.getCount();
            // 下列属性状态清除,才会在被调用notifyDataSetChange时,在gridview测量布局完成后重新获取
            mChilds.clear();
            mColHeight = mColWidth = mMaxHeight = 0;
            isViewInitDone = false;
        }

        @Override
        public void onInvalidated() {
            mChildCount = adapter.getCount();
        }
    };

    /**
     * 控制自动滚屏的动画监听器.
     */
    private ValueAnimator.AnimatorUpdateListener animUpdateListener = new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int targetY = Math.round((Float) animation.getAnimatedValue());
            if (targetY < 0) {
                targetY = 0;
            } else if (targetY > mMaxHeight - getHeight()) {
                targetY = mMaxHeight - getHeight();
            }
            // mGridView.scrollTo(0, targetY);
            mScrollView.smoothScrollTo(0, targetY);
            // mCurrentY = targetY;
        }

    };

    public void setAdapter(DragAdapter adapter) {
        if (this.adapter != null && observer != null) {
            this.adapter.unregisterDataSetObserver(observer);
        }
        this.adapter = adapter;
        mGridView.setAdapter(adapter);
        adapter.registerDataSetObserver(observer);
        mChildCount = adapter.getCount();
    }

    /**
     * @param from
     * @param to
     * @描述:动画效果移动View （top部分列表内拖拽的动画效果）
     */
    public void translateView(int from, int to) {
        View view = mChilds.get(from);
        int fromXValue = ((int[]) view.getTag(TAG_KEY))[0];
        int fromYValue = ((int[]) view.getTag(TAG_KEY))[1];
        int toXValue = to % mNumColumns - from % mNumColumns + fromXValue;
        int toYValue = to / mNumColumns - from / mNumColumns + fromYValue;
        Animation animation = new TranslateAnimation(1, fromXValue, 1, toXValue, 1, fromYValue, 1, toYValue);
        animation.setDuration(ANIM_DURING);
        animation.setFillAfter(true);
        view.setTag(TAG_KEY, new int[]{toXValue, toYValue});
        view.startAnimation(animation);
    }

    /**
     * @param from
     * @param to
     * @描述:拖动View使位置发生改变时 （not use）
     */
    public void onDragPositionChange(int from, int to) {
        if (from > to) {
            for (int i = to; i < from; i++) {
                translateView(i, i + 1);
            }
        } else {
            for (int i = to; i > from; i--) {
                translateView(i, i - 1);
            }
        }
        if (!hasPositionChange) {
            hasPositionChange = true;
        }
        if ((from >= mChilds.size() || from < 0) || (to >= mChilds.size() || to < 0))
            return;
        adapter.onDataModelMove(from, to);
        View view = mChilds.remove(from);
        mChilds.add(to, view);
        currentDragPosition = to;
    }

    /**
     * @param scrollStates
     * @描述:触摸区域改变,做相应处理,开始滚动或停止滚动
     */
    public void onTouchAreaChange(int scrollStates) {
        if (!canScroll) {
            return;
        }
        if (animator != null) {
            animator.removeUpdateListener(animUpdateListener);
        }
        if (scrollStates == 1) {// 从普通区域进入触发向上滚动的区域
            int instance = mMaxHeight - getHeight() - mCurrentY;
            animator = ValueAnimator.ofFloat(mCurrentY, mMaxHeight - getHeight());
            animator.setDuration((long) (instance / 0.5f));
            animator.setTarget(mGridView);
            animator.addUpdateListener(animUpdateListener);
            animator.start();
        } else if (scrollStates == -1) {// 进入触发向下滚动的区域
            animator = ValueAnimator.ofFloat(mCurrentY, 0);
            animator.setDuration((long) (mCurrentY / 0.5f));
            animator.setTarget(mGridView);
            animator.addUpdateListener(animUpdateListener);
            animator.start();
        }
    }

    /**
     * @param ev 事件
     * @return 0中间区域, 1底部,-1顶部
     * @描述: 检查当前触摸事件位于哪个区域, 顶部1/5可能触发下滚,底部1/5可能触发上滚
     */
    public int decodeTouchArea(MotionEvent ev) {
        if (ev.getY() > (getHeight() + getY()) * 4 / (double) 5) {
            return 1;
        } else if (ev.getY() < (getHeight() + getY()) / (double) 5) {
            return -1;
        } else {
            return 0;
        }
    }

    public boolean isViewInitDone() {
        return isViewInitDone;
    }

    /**
     * 设置前几个item不可以改变位置
     */
    public void setNoPositionChangeItemCount(int count) {
        headDragPosition = count;
    }

    /**
     * 设置后几个item不可以改变位置
     */
    public void setFootNoPositionChangeItemCount(int count) {
        footDragPosition = count;
    }

    public int getHeadDragPosition() {
        return headDragPosition;
    }

    public int getFootDragPosition() {
        return footDragPosition;
    }

    public int getGridChildCount() {
        return mChilds.size();
    }

    public View getGridChildAt(int position) {
        if (position < 0 || position >= mChilds.size())
            return null;
        return mChilds.get(position);
    }

    public int getGridChildPos(View view) {
        return mGridView.indexOfChild(view);
    }

    public void setCurrentDragPosition(int currentDragPosition) {
        this.currentDragPosition = currentDragPosition;
    }

    public int getCurrentDragPosition() {
        return currentDragPosition;
    }

    public String getCurrentDragValue() {
        return currentDragValue;
    }

    public void setCurrentDragValue(String currentDragValue) {
        this.currentDragValue = currentDragValue;
    }

    public int getmColHeight() {
        return mColHeight;
    }

    public int getmColWidth() {
        return mColWidth;
    }

    public DragAdapter getAdapter() {
        return adapter;
    }

    public boolean isHasPositionChange() {
        return hasPositionChange;
    }

    public void setHasPositionChange(boolean hasPositionChange) {
        this.hasPositionChange = hasPositionChange;
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mGridView.setOnItemClickListener(onItemClickListener);
    }

    public void onItemLongClick(AdapterView.OnItemLongClickListener itemLongClickListener) {
        itemLongClickListener.onItemLongClick(mGridView, childAt(currentDragPosition), currentDragPosition, 0);
    }

    public View childAt(int index) {
        return mGridView.getChildAt(index);
    }

    public void dispatchEvent(MotionEvent ev) {
        if (canScroll)
            mScrollView.dispatchTouchEvent(ev);
        else
            mGridView.dispatchTouchEvent(ev);
    }

    public int eventToPosition(MotionEvent ev) {
        if (ev != null) {
            int m = (int) ev.getX() / mColWidth;
            int n = (int) (ev.getY() - getY() + mCurrentY) / mColHeight;
            int position = n * mNumColumns + m;
            if (position >= mChildCount) {
                return mChildCount - 1;
            } else {
                return position;
            }
        }
        return 0;
    }

    /**
     * 添加被交换的item view
     *
     * @param data
     */
    public void addSwapView(Object data) {
        adapter.addNewData(data);
    }

    /**
     * 增加数据源到指定位置
     * @param index index
     * @param data data
     */
    public void addDataByIndex(int index, LeftBean data) {
        adapter.addDataByIndex(index, data);
    }

    /**
     * 根据下标获取拖拽item的data（数据源）
     * @param index 下标
     * @return value
     */
    public LeftBean getTargetDataByIndex(int index) {
        return adapter.getSwapData(index);
    }

    public void removeSwapView() {
        if (adapter != null) {
            adapter.removeData(currentDragPosition);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * delete 指定index的 数据
     * @param position
     */
    public void removeTargetData(int position) {
        if (adapter != null) {
            adapter.removeData(position);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 清空当前正在拖拽的信息
     */
    public void clearCurrentDragInfo() {
        currentDragValue = "";
        currentDragPosition = -1;
    }

    class ListenScrollView extends ScrollView {
        public ListenScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
            mCurrentY = getScrollY();
        }
    }

    static class NoScrollGridView extends GridView {

        public NoScrollGridView(Context context) {
            super(context);
        }

        /**
         * @return
         * @描述:兼容老版本的getColumWidth
         * @作者 [pWX273343] 2015年7月1日
         */
        public int getColumnWidth() {
            return getWidth() / getNumColumns();
        }

        public NoScrollGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, mExpandSpec);
        }
    }
}