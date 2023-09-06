package com.longluo.demo.view.dragchoose.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.longluo.demo.R;
import com.longluo.demo.view.dragchoose.bean.LeftBean;

import java.util.ArrayList;
import java.util.Collections;


public class CustomAdapter extends DragAdapter {

    ArrayList<LeftBean> list;

    public CustomAdapter(ArrayList<LeftBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rea_rec, null);
            viewHolder.tv_mid = convertView.findViewById(R.id.tv_content);
            viewHolder.lin_root = convertView.findViewById(R.id.lin_root);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_mid.setText(list.get(position).getItemName());
        //若是被拖拽到右边的数据源，设置灰色，否则默认白色
        int colorBg = list.get(position).getIsDragInRight() ? R.color.colorGrey : R.color.colorRed;
        viewHolder.lin_root.setBackgroundColor(ContextCompat.getColor(viewHolder.lin_root.getContext(), colorBg));

        return convertView;
    }

    @Override
    public void onDataModelMove(int from, int to) {
        Collections.swap(list, from, to);
    }

    @Override
    public LeftBean getSwapData(int position) {
        return list.get(position);
    }

    @Override
    public void removeData(int position) {
        list.remove(position);
    }

    @Override
    public void addNewData(Object data) {
        if (data instanceof LeftBean) {
            list.add(((LeftBean) data));
        }
    }

    @Override
    public void addDataByIndex(int index, LeftBean bean) {
        list.add(index, bean);
    }

    static class ViewHolder {
        public LinearLayout lin_root;
        public TextView tv_mid;
    }

}
