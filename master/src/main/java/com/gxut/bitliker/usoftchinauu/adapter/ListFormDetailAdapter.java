package com.gxut.bitliker.usoftchinauu.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gxut.code.baseutil.util.LogUtil;
import com.gxut.code.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.model.FormDetailList;

import java.util.List;

/**
 * Created by Bitliker on 2017/8/28.
 */

public class ListFormDetailAdapter extends RecyclerView.Adapter<ListFormDetailAdapter.ViewHolder> {
    private Activity ct;
    private List<FormDetailList> models;

    public ListFormDetailAdapter(Activity ct, List<FormDetailList> models) {
        this.ct = ct;
        this.models = models;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(ct, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
    }

    public void setModels(List<FormDetailList> models) {
        this.models = models;
    }

    public List<FormDetailList> getModels() {
        return models;
    }

    public void addModels(List<FormDetailList> models) {
        if (Utils.isEmpty(this.models)) {
            setModels(models);
            notifyDataSetChanged();
        } else {
            int startItem = this.models.size();
            this.models.addAll(models);
            notifyItemRangeInserted(startItem, this.models.size());
            notifyItemRangeChanged(startItem, this.models.size());
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        List<FormDetailList.ItemData> itemDatas = models.get(position).getItemDatas();
        if (Utils.isEmpty(itemDatas)) return;
        if (holder.recyclerView.getLayoutManager() == null) {
            holder.recyclerView.setLayoutManager(getLayoutManager());
        }
        if (holder.recyclerView.getAdapter() == null || !(holder.recyclerView.getAdapter() instanceof ItemAdapter)) {
            holder.recyclerView.setAdapter(new ItemAdapter(itemDatas));
        } else {
            ItemAdapter mAdapter = (ItemAdapter) holder.recyclerView.getAdapter();
            mAdapter.setItemDatas(itemDatas);
        }
    }

    @Override
    public int getItemCount() {
        return Utils.getSize(models);
    }


    class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private List<FormDetailList.ItemData> itemDatas;

        private void specialUpdate() {
            Handler handler = new Handler(Looper.getMainLooper());
            final Runnable r = new Runnable() {
                public void run() {
                    ListFormDetailAdapter.this.notifyDataSetChanged();
                }
            };
            handler.post(r);
        }

        public void setItemDatas(List<FormDetailList.ItemData> itemDatas) {
            this.itemDatas = itemDatas;
            specialUpdate();
        }

        public ItemAdapter(List<FormDetailList.ItemData> itemDatas) {
            this.itemDatas = itemDatas;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, final int position) {
            FormDetailList.ItemData data = itemDatas.get(position);
            holder.tagTV.setText(data.getKey());
            holder.contentTV.setText(data.getValues());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.i("position=" + position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return Utils.getSize(itemDatas);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public ViewHolder(ViewGroup parent) {
            this(LayoutInflater.from(ct).inflate(R.layout.item_recycler, parent, false));
        }

        public ViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tagTV, contentTV;

        public ItemViewHolder(ViewGroup parent) {
            this(LayoutInflater.from(ct).inflate(R.layout.item_inputform, parent, false));
        }

        public ItemViewHolder(View itemView) {
            super(itemView);
            tagTV = (TextView) itemView.findViewById(R.id.tagTV);
            contentTV = (TextView) itemView.findViewById(R.id.contentTV);
        }
    }

}
