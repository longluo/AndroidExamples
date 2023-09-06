package com.longluo.demo.view.dragchoose.bean;

import java.io.Serializable;

/**
 * 左布局item
 **/
public class LeftBean implements Serializable {

    //item名
    private String itemName;
    //是否拖拽到右布局
    private boolean isDragInRight;
    //是否是用户自定义增加
    private boolean isUserAdd;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean getIsDragInRight() {
        return isDragInRight;
    }

    public void setDragInRight(boolean dragInRight) {
        isDragInRight = dragInRight;
    }

    public boolean getIsUserAdd() {
        return isUserAdd;
    }

    public void setUserAdd(boolean userAdd) {
        isUserAdd = userAdd;
    }
}
