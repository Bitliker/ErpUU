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
import com.gxut.bitliker.usoftchinauu.model.BaseSelectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 样式：
 * 1.存在title、sub。。。。
 * 2.只有title
 * 选择：
 * 1.单选
 * 1.1 onItemClickListener.itemClick(BaseSelectModel<T> model, int position)
 * <p>
 * 2.多选
 * 2.1
 * Created by Bitliker on 2017/7/4.
 */
public class BaseSelectAdapter<T> extends RecyclerView.Adapter<BaseSelectAdapter.ViewHolder> {
    private Context ct;
    private int lastPostion = -1;
    private boolean autoHeight = true;//是否自适应高度
    private boolean showCb = false;//是否显示选择框
    private boolean isMulti = false;//是否多选
    private OnItemClickListener<T> onItemClickListener;//监听器
    private List<BaseSelectModel<T>> models;
    private List<BaseSelectModel<T>> selectModels;


    public static class Builder<T> {
        private BaseSelectAdapter<T> mAdapter;

        public Builder(Context ct) {
            mAdapter = new BaseSelectAdapter<T>(ct);
        }

        public Builder setAutoHeight(boolean autoHeight) {
            mAdapter.autoHeight = autoHeight;
            return this;
        }

        public Builder setShowCb(boolean showCb) {
            mAdapter.showCb = showCb;
            return this;
        }

        public Builder setMulti(boolean isMulti) {
            mAdapter.isMulti = isMulti;
            return this;
        }

        public Builder setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
            mAdapter.onItemClickListener = onItemClickListener;
            return this;
        }

        public Builder setModels(List<BaseSelectModel<T>> models) {
            mAdapter.setModels(models);
            return this;
        }

        public BaseSelectAdapter<T> builder() {
            return mAdapter;
        }
    }

    private BaseSelectAdapter(Context ct) {
        this.ct = ct;
    }

    public List<BaseSelectModel<T>> getModels() {
        return models;
    }

    public List<BaseSelectModel<T>> getSelectModels() {
        return selectModels;
    }


    public void setModels(List<BaseSelectModel<T>> models) {
        this.models = models;
        if (!Utils.isEmpty(selectModels)) {
            selectModels = new ArrayList<>();
            lastPostion = -1;
        }
    }

    public void addModels(List<BaseSelectModel<T>> models) {
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
    public BaseSelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int id = autoHeight ? 0 : R.layout.item_select;
        return new ViewHolder(id, parent);
    }

    @Override
    public int getItemCount() {
        return Utils.getSize(models);
    }

    @Override
    public void onBindViewHolder(BaseSelectAdapter.ViewHolder holder, final int position) {
        BaseSelectModel model = models.get(position);
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
                    if (Utils.getSize(models) <= position) return;
                    if (showCb) {//显示选择框
                        if (isMulti) {//多选
                            showCbMulti(position);
                        } else {
                            showCbRadio(position);
                        }
                    } else {//不显示选择框，默认为单选
                        onItemClickListener.itemClick(models.get(position), position);
                    }
                }
            });
        }

    }

    /*当多选时候点击item*/
    private void showCbMulti(int position) {
        models.get(position).setSelect(!models.get(position).isSelect());
        notifyItemChanged(position);
        onItemClickListener.itemClick(models.get(position), position);
    }

    /*当单选有显示选择框的时候点击item*/
    private void showCbRadio(int position) {
        if (lastPostion == -1 || lastPostion == position) {
            models.get(position).setSelect(!models.get(position).isSelect());
            notifyItemChanged(position);
            if (lastPostion == position) {
                lastPostion = -1;
            } else {
                lastPostion = position;
            }
        } else {
            if (Utils.getSize(models) > lastPostion) {
                models.get(position).setSelect(!models.get(position).isSelect());
                models.get(lastPostion).setSelect(!models.get(lastPostion).isSelect());
                notifyItemChanged(position);
                notifyItemChanged(lastPostion);
                lastPostion = position;
            }
        }
        onItemClickListener.itemClick(models.get(position), position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox selectCB;
        TextView nameTv, subTv, tagTv;

        public ViewHolder(int layoutId, ViewGroup parent) {
            this(LayoutInflater.from(ct).inflate(layoutId <= 0 ? R.layout.item_select_auto : layoutId, parent, false));
        }

        public ViewHolder(View itemView) {
            super(itemView);
            selectCB = (CheckBox) itemView.findViewById(R.id.selectCB);
            subTv = (TextView) itemView.findViewById(R.id.subTv);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            tagTv = (TextView) itemView.findViewById(R.id.tagTv);
        }
    }

    public interface OnItemClickListener<T> {
        void itemClick(BaseSelectModel<T> model, int position);
    }


}
