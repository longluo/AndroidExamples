package com.longluo.demo.view.dragchoose.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longluo.demo.view.dragchoose.bean.LeftBean;


/**
 * 接口类
 */
public abstract class DragAdapter extends BaseAdapter {

    /**
     * @param from
     * @param to
     * @描述:当从from排序被拖到to排序时的处理方式,请对相应的数据做处理。
     */
    public abstract void onDataModelMove(int from, int to);

    /**
     * 复制View使用的方法,默认直接使用getView方法获取
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View copyView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    /**
     * 是否启用copyView方法
     *
     * @return true 使用copyView复制 false 使用getView直接获取镜像
     */
    public boolean isUseCopyView() {
        return false;
    }

    public LeftBean getSwapData(int position) {
        return null;
    }

    public abstract void removeData(int position);

    public void addNewData(Object data) {

    }

    /**
     * 增加数据源到指定位置
     *
     * @param index index
     * @param bean  leftBean
     */
    public void addDataByIndex(int index, LeftBean bean) {

    }
}