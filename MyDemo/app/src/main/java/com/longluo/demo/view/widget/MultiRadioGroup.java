package com.longluo.demo.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.longluo.demo.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 这是一个继承LinearLayout的自定义多选RadioGroup效果
 */
public class MultiRadioGroup extends LinearLayout {
    //多选的item xml id dataSource
    private int[] checkedIdArray = {-1, -1};
    //多选的item下标dataSource
    private int[] checkedIndexArray = {-1, -1};
    // tracks children radio buttons checked state
    private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
    // when true, mOnCheckedChangeListener discards events
    private boolean mProtectFromCheckedChange = false;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;
    //最大可多选数量
    int maxCheckCount = 2;
    //当前已多选数量
    int currentCheckCount = 0;

    /**
     * {@inheritDoc}
     */
    public MultiRadioGroup(Context context) {
        super(context);
        setOrientation(VERTICAL);
        init();
    }

    /**
     * {@inheritDoc}
     */
    public MultiRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mChildOnCheckedChangeListener = new CheckedStateTracker();
        //不用这个，已选中的就不能恢复了
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }

    /**
     * get all radio buttons which are in the view
     *
     * @param child
     */
    private List<CheckBox> getAllCheckbox(View child) {
        List<CheckBox> btns = new ArrayList<CheckBox>();
        if (child instanceof CheckBox) {
            btns.add((CheckBox) child);
        } else if (child instanceof ViewGroup) {
            int counts = ((ViewGroup) child).getChildCount();
            for (int i = 0; i < counts; i++) {
                btns.addAll(getAllCheckbox(((ViewGroup) child).getChildAt(i)));
            }
        }
        return btns;
    }

    //选中本次
    private void setCheckedId(int id) {
        if (checkedIdArray[0] == -1) {
            checkedIdArray[0] = id;
        } else if (checkedIdArray[1] == -1) {
            checkedIdArray[1] = id;
        }
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, id);
        }
    }

    /**
     * 设置曾经选中过的item
     */
    private void setCheckedStateForView() {
        for (int i = 0; i < checkedIdArray.length; i++) {
            View checkedView = findViewById(checkedIdArray[i]);
            if (checkedView instanceof CheckBox) {
                //如果已经有2个选中，那么就清空。不足2个就选中状态显示
                if (currentCheckCount >= maxCheckCount) {
                    ((CheckBox) checkedView).setChecked(false);
                } else {
                    ((CheckBox) checkedView).setChecked(true);
                }
            }
        }
        if (currentCheckCount >= maxCheckCount) {
            //页面选中状态都设置完毕后，再清0
            currentCheckCount = 0;
            checkedIdArray[0] = -1;
            checkedIdArray[1] = -1;
            checkedIndexArray[0] = -1;
            checkedIndexArray[1] = -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(MultiRadioGroup.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(MultiRadioGroup.class.getName());
    }

    /**
     * <p>This set of layout parameters defaults the width and the height of
     * the children to {@link #WRAP_CONTENT} when they are not specified in the
     * XML file. Otherwise, this class ussed the value read from the XML file.</p>
     *
     * <p>
     * for a list of all child view attributes that this class supports.</p>
     */
    public static class LayoutParams extends LinearLayout.LayoutParams {
        /**
         * {@inheritDoc}
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int w, int h) {
            super(w, h);
        }


        @Override
        protected void setBaseAttributes(TypedArray a,
                                         int widthAttr, int heightAttr) {
            if (a.hasValue(widthAttr)) {
                width = a.getLayoutDimension(widthAttr, "layout_width");
            } else {
                width = WRAP_CONTENT;
            }

            if (a.hasValue(heightAttr)) {
                height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                height = WRAP_CONTENT;
            }
        }
    }

    /**
     * <p>Interface definition for a callback to be invoked when the checked
     * radio button changed in this group.</p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is -1.</p>
         *
         * @param group     the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        public void onCheckedChanged(MultiRadioGroup group, int checkedId);
    }

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return;
            }
            mProtectFromCheckedChange = true;
            if (isHasChecked()) {
                setCheckedStateForView();
            }
            mProtectFromCheckedChange = false;
            int id = buttonView.getId();

            setCheckedId(id);
            currentCheckCount++;
            setCheckedIndexById(id);
            Log.v("logStr:", "checkedIndexArray[0]: " + checkedIndexArray[0] + "," + "checkedIndexArray[1]: " + checkedIndexArray[1]);
        }
    }

    /**
     * <p>A pass-through listener acts upon the events and dispatches them
     * to another listener. This allows the table layout to set its own internal
     * hierarchy change listener without preventing the user to setup his.</p>
     */
    private class PassThroughHierarchyChangeListener implements
            OnHierarchyChangeListener {
        private OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        @SuppressLint("NewApi")
        public void onChildViewAdded(View parent, View child) {
            if (parent == MultiRadioGroup.this) {
                List<CheckBox> btns = getAllCheckbox(child);
                if (btns.size() > 0) {
                    for (CheckBox btn : btns) {
                        int id = btn.getId();
                        // generates an id if it's missing
                        if (id == View.NO_ID) {
                            id = View.generateViewId();
                            btn.setId(id);
                        }
                        btn.setOnCheckedChangeListener(
                                mChildOnCheckedChangeListener);
                    }
                }
            }
            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void onChildViewRemoved(View parent, View child) {
            if (parent == MultiRadioGroup.this) {
                List<CheckBox> btns = getAllCheckbox(child);
                if (btns.size() > 0) {
                    for (CheckBox btn : btns) {
                        btn.setOnCheckedChangeListener(null);
                    }
                }
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

    /**
     * 是否有选中过多选自定义checkbox
     *
     * @return
     */
    boolean isHasChecked() {
        if (checkedIdArray[0] == -1 && checkedIdArray[1] == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 根据view的id来存多选的具体下标index，cb_1: 1, cb_2: 2, cb_3: 3
     *
     * @param id
     */
    void setCheckedIndexById(int id) {
        if (id == R.id.cb_1) {
            setCheckedIndexArray(1);
        } else if (id == R.id.cb_2) {
            setCheckedIndexArray(2);
        } else if (id == R.id.cb_3) {
            setCheckedIndexArray(3);
        } else if (id == R.id.cb_4) {
            setCheckedIndexArray(4);
        }
    }

    /**
     * 存入选中的index
     *
     * @param index 下标
     */
    void setCheckedIndexArray(int index) {
        if (checkedIndexArray[0] == -1) {
            checkedIndexArray[0] = index;
        } else if (checkedIndexArray[1] == -1) {
            checkedIndexArray[1] = index;
        }
    }
}