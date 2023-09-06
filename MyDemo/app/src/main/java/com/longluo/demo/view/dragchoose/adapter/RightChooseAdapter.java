package com.longluo.demo.view.dragchoose.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longluo.demo.R;
import com.longluo.demo.view.dragchoose.bean.RightBean;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class RightChooseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<RightBean> mList;

    public RightChooseAdapter(ArrayList<RightBean> dataList) {
        this.mList = dataList;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right_choose, parent, false);
        return new RightChooseHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RightChooseHolder) {
            ((RightChooseHolder) holder).bind(position, mList);
        }
    }

    public List<RightBean> getDataList() {
        return mList;
    }

    public void setDataList(ArrayList<RightBean> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    /**
     * 数据源中增加1个数据
     *
     * @param bean bean
     */
    public void addData(RightBean bean) {
        this.mList.add(bean);
        notifyDataSetChanged();
    }

    /**
     * 数据源中删除1个数据
     *
     * @param bean bean
     */
    public void removeData(RightBean bean) {
        this.mList.remove(bean);
        notifyDataSetChanged();
    }

    /**
     * RightChoose ViewHolder
     */
    static class RightChooseHolder extends RecyclerView.ViewHolder {

        TextView tv_content;
        ImageView iv_close;

        public RightChooseHolder(View itemView) {
            super(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
            iv_close = itemView.findViewById(R.id.iv_close);
        }

        public void bind(int position, ArrayList<RightBean> list) {
            tv_content.setText(list.get(position).getItemName());
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo: 发送删除右布局item event （item删右，增加左）
                    EventBus.getDefault().post(new DeleteRightItemEvent(list.get(position).getItemName()));
                }
            });
        }

    }

    /**
     * 删除右布局item的event
     */
    public static class DeleteRightItemEvent {

        private final String itemName;

        public String getItemName() {
            return itemName;
        }

        DeleteRightItemEvent(@NonNull String id) {
            this.itemName = id;
        }
    }

    //!------------ not use --------

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

}
