package com.longluo.demo.spinner.widget;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longluo.demo.R;

import java.util.ArrayList;
import java.util.List;

public class SpinnerPopWindow<T> extends PopupWindow {
    private LayoutInflater inflater;
    private RecyclerView mListView;
    private List<T> list;
    private PopUpAdapter popAdapter;

    //声明自定义的监听接口
    private OnRecyclerItemClickListener onItemClickListener;

    //提供set方法供Activity或Fragment调用
    public void setRecyclerItemClickListener(OnRecyclerItemClickListener listener) {
        onItemClickListener = listener;
    }

    public SpinnerPopWindow(Context context, List<T> list, OnRecyclerItemClickListener listener) {
        super(LayoutInflater.from(context).inflate(R.layout.spinner_window_layout, null),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.onItemClickListener = listener;
        init();
    }

    private void init() {
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        mListView = getContentView().findViewById(R.id.popup_listview);
        if (null == list) {
            list = new ArrayList<>();
        }
        mListView.setAdapter(popAdapter = new PopUpAdapter(list));
    }

    public void setNewData(List<T> list) {
        this.list = list;
        popAdapter.setNewData(list);
    }

    public void showWindow(View view) {
        if (!isShowing()) {
            //当手机安卓系统为7.0时，showAsDropDown()不起效果，我们可以用showAtLocation来解决这个问题
            if (Build.VERSION.SDK_INT < 24) {
                showAsDropDown(view);
            } else {
                // 获取控件的位置，安卓系统>7.0
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1]);
            }
        }
    }

    private class PopUpAdapter extends RecyclerView.Adapter<PopUpAdapter.ViewHolder> {

        private List<T> list;

        public PopUpAdapter(List<T> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View convertView = inflater.inflate(R.layout.spinner_pop_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(convertView);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textTv.setText(list.get(position).toString());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void setNewData(List<T> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textTv;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textTv = itemView.findViewById(R.id.text_content);
                itemView.findViewById(R.id.pop_item_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != onItemClickListener) {
                            onItemClickListener.onItemClick(getAdapterPosition(), textTv, list);
                        }
                    }
                });
            }
        }

    }

    public interface OnRecyclerItemClickListener {
        //RecyclerView的点击事件，将信息回调给view
        void onItemClick(int position, TextView textView, List<?> list);
    }
}

