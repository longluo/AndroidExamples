package com.longluo.demo.view.dragchoose;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.longluo.demo.R;
import com.longluo.demo.view.dragchoose.adapter.CustomAdapter;
import com.longluo.demo.view.dragchoose.adapter.RightChooseAdapter;
import com.longluo.demo.view.dragchoose.bean.LeftBean;
import com.longluo.demo.view.dragchoose.bean.RightBean;
import com.longluo.demo.view.dragchoose.widget.DragChessView;

import java.util.ArrayList;

/**
 * 拖拽选择的item到指定位置
 */
public class DragChooseActivity extends AppCompatActivity {

    DragChessView dragChessView;
    //左布局数据源
    ArrayList<LeftBean> leftList = new ArrayList<>();
    //右布局数据源
    ArrayList<RightBean> rightList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_choose);
        initView();
    }

    void initView() {
        dragChessView = findViewById(R.id.drag_main);
        dragChessView.setDragModel(DragChessView.DRAG_BY_LONG_CLICK);

        for (int i = 0; i < 30; i++) {
            LeftBean bean = new LeftBean();
            bean.setItemName("" + i);
            bean.setDragInRight(false);
            bean.setUserAdd(false);
            leftList.add(bean);
        }

        dragChessView.setLeftAdapter(new CustomAdapter(leftList));
        dragChessView.setRightAdapter(new RightChooseAdapter(rightList));
    }
}