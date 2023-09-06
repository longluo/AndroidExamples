package com.longluo.demo.spinner.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.longluo.demo.R;

import java.util.ArrayList;
import java.util.List;

public class SpinnerTextView extends androidx.appcompat.widget.AppCompatTextView implements SpinnerPopWindow.OnRecyclerItemClickListener, View.OnClickListener {

    private List<String> dataList = new ArrayList<>();
    private SpinnerPopWindow<String> popWindow;
    private OnItemSelectListener onItemSelectListener;
    private OnViewClickListener onViewClickListener;

    public SpinnerTextView(Context context) {
        super(context);
    }

    public SpinnerTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpinnerTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        dataList = new ArrayList<>();
        popWindow = new SpinnerPopWindow<>(context, dataList, this);
        setOnClickListener(this);
    }

    public void setDataList(List<String> list) {
        dataList = list;
        setNewData(list);
    }

    @Override
    public void onItemClick(int position, TextView textView, List<?> list) {
        Object object = list.get(position);
        String[] result = {""};
        if (null != object) {
            result[0] = object.toString();
        }
        setText(result[0]);
        if (null != onItemSelectListener) {
            onItemSelectListener.OnItemSelect(position, result[0]);
        }
        popWindow.dismiss();
    }


    @Override
    public void onClick(View v) {
        popWindow.showWindow(findViewById(R.id.custom_text));
        if (null != onViewClickListener) {
            onViewClickListener.viewClick(v);
        }
    }

    public void reInit() {
        dataList = new ArrayList<>();
        setText("");
    }

    public OnItemSelectListener getOnItemSelectListener() {
        return onItemSelectListener;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public OnViewClickListener getOnViewClickListener() {
        return onViewClickListener;
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public void setNewData(List<String> dataList) {
        if (null == dataList) {
            dataList = new ArrayList<>();
        }
        this.dataList = dataList;
        popWindow.setNewData(this.dataList);
    }

    public interface OnViewClickListener {
        public void viewClick(View v);
    }

    public interface OnItemSelectListener {
        public void OnItemSelect(int position, String text);
    }
}


