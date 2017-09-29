package com.gxut.bitliker.usoftchinauu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gxut.bitliker.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.model.SelectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bitliker on 2017/6/30.
 */

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {
    private Context ct;
    private List<SelectModel> models;
    private boolean showCb;

    public SelectAdapter(Context ct, List<SelectModel> models) {
        this.ct = ct;
        this.models = models;
    }

    public SelectAdapter(Context ct, boolean showCb, List<SelectModel> models) {
        this.ct = ct;
        this.showCb = showCb;
        this.models = models;
    }

    public List<SelectModel> getModels() {
        return models;
    }

    public void setModels(List<SelectModel> models) {
        this.models = models;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ct).inflate(R.layout.item_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SelectModel model = models.get(position);
        if (model != null) {
            holder.nameTv.setText(model.getName());
            holder.subTv.setVisibility(Utils.isEmpty(model.getSub()) ? View.GONE : View.VISIBLE);
            holder.tagTv.setVisibility(Utils.isEmpty(model.getTag()) ? View.GONE : View.VISIBLE);
            holder.subTv.setText(model.getSub());
            holder.tagTv.setText(model.getTag());
        }
        holder.selectCB.setVisibility(showCb ? View.VISIBLE : View.GONE);
        holder.selectCB.setFocusable(false);
        holder.selectCB.setClickable(false);
        holder.selectCB.setChecked(model.isSelect());
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.itemClick(models.get(position), position);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return Utils.getSize(models);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox selectCB;
        TextView nameTv, subTv, tagTv;

        public ViewHolder(View itemView) {
            super(itemView);
            selectCB = (CheckBox) itemView.findViewById(R.id.selectCB);
            subTv = (TextView) itemView.findViewById(R.id.subTv);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            tagTv = (TextView) itemView.findViewById(R.id.tagTv);
        }
    }

    public SelectModel getSelect() {
        if (Utils.isEmpty(models)) return null;
        for (SelectModel m : models) {
            if (m.isSelect())
                return m;
        }
        return models.get(0);
    }

    public List<SelectModel> getSelects() {
        if (Utils.isEmpty(models)) return null;
        List<SelectModel> selectModels = new ArrayList<>();
        for (SelectModel m : models) {
            if (m.isSelect()) selectModels.add(m);
        }
        return selectModels;
    }

    public interface OnItemClickListener {
        void itemClick(SelectModel model, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
