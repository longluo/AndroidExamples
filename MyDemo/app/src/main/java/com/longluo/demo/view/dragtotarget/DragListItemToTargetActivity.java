package com.longluo.demo.view.dragtotarget;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SimpleAdapter;

import com.longluo.demo.R;
import com.longluo.demo.view.dragtotarget.widget.MoveGridView;
import com.longluo.demo.view.dragtotarget.widget.UninstallListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * https://www.jb51.net/article/130441.htm 的demo
 */
public class DragListItemToTargetActivity extends AppCompatActivity {

    private MoveGridView mMoveGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_list_item_to_target);
        mMoveGridView = (MoveGridView) findViewById(R.id.gridview);
        final ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemText", "NO." + String.valueOf(i));// 按序号做ItemText
            lstImageItem.add(map);
        }
        final SimpleAdapter saImageItems = new SimpleAdapter(this,
                lstImageItem,// 数据来源
                R.layout.item_drag_to_target,
                // 动态数组与ImageItem对应的子项
                new String[] { "ItemText" },
                // ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[] { R.id.ItemText });
        // 添加并且显示
        mMoveGridView.setAdapter(saImageItems);
        //监听到卸载删除数据
        mMoveGridView.setOnUninstallListener(new UninstallListener() {
            @Override
            public void onUninstallListener(int position) {
                lstImageItem.remove(position);
                saImageItems.notifyDataSetChanged();
            }
        });
    }
}