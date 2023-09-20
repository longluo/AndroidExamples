package com.longluo.demo.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * 自定义ExpandableListView，为了解决嵌套ScrollView的滚动冲突
 **/
class CustomNestedExpandableListView extends ExpandableListView {

    public CustomNestedExpandableListView(Context context) {
        super(context);
    }


    public CustomNestedExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNestedExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写这个方法就行
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = View.MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams(); // 存在一个问题 ，如果是全部收起的话 ，就会导致页面空白
        params.height = getMeasuredHeight();

    }

}
