package com.gxut.bitliker.usoftchinauu.util;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gxut.code.baseutil.util.DisplayUtil;
import com.gxut.code.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.adapter.BaseSelectAdapter;
import com.gxut.bitliker.usoftchinauu.model.BaseSelectModel;

import java.util.List;

/**
 * Created by Bitliker on 2017/8/28.
 */

public class SelectPopupBuilder<T> implements BaseSelectAdapter.OnItemClickListener<T> {
    private PopupWindow.OnDismissListener onDismissListener;
    private Activity ct;
    private String title;
    private List<BaseSelectModel<T>> datas;
    private OnSelectListener onSelectListener;
    private PopupWindow mWindow = null;

    @Override
    public void itemClick(BaseSelectModel<T> model, int position) {
        if (onSelectListener != null) {
            if (onSelectListener instanceof OnRadioSelectListener) {
                ((OnRadioSelectListener) onSelectListener).callBack(model);
                if (mWindow != null) {
                    mWindow.dismiss();
                }
            }
        }
    }

    private interface OnSelectListener {
    }

    public interface OnMultiSelectListener<T> extends OnSelectListener {
        void callBack(List<BaseSelectModel<T>> datas);
    }

    public interface OnRadioSelectListener<T> extends OnSelectListener {
        void callBack(BaseSelectModel<T> data);
    }

    public SelectPopupBuilder(Activity ct) {
        this.ct = ct;
    }

    public SelectPopupBuilder<T> setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public SelectPopupBuilder<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public SelectPopupBuilder<T> setDatas(List<BaseSelectModel<T>> datas) {
        this.datas = datas;
        return this;
    }

    public SelectPopupBuilder<T> setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
        return this;
    }


    public void builder() {
        mWindow = new PopupWindow(ct);
        final View view = LayoutInflater.from(ct).inflate(R.layout.pop_select_list, null);
        mWindow.setContentView(view);
        RecyclerView contentLV = (RecyclerView) view.findViewById(R.id.contentLV);
        TextView titleTV = (TextView) view.findViewById(R.id.titleTV);
        final TextView selectAllTV = (TextView) view.findViewById(R.id.selectAllTV);
        if (!Utils.isEmpty(title)) {
            titleTV.setText(title);
        }
        final boolean isMulti = onSelectListener != null && onSelectListener instanceof OnMultiSelectListener;
        mWindow.setTouchable(true);
        mWindow.setBackgroundDrawable(ct.getResources().getDrawable(R.drawable.pop_round_bg));
        int height = Math.min(DisplayUtil.dip2px(330), (int) (DisplayUtil.getScreenHeight(ct) * 0.6));
        int width = Math.min(DisplayUtil.dip2px(300), (int) (DisplayUtil.getScreenWidth(ct) * (0.75)));
        mWindow.setHeight(height);
        mWindow.setWidth(width);
        mWindow.setOutsideTouchable(false);
        mWindow.setFocusable(true);
        final BaseSelectAdapter<T> mAdapter = new BaseSelectAdapter.Builder<T>(ct)
                .setAutoHeight(false)
                .setShowCb(isMulti)
                .setMulti(isMulti)
                .setOnItemClickListener(this)
                .setModels(datas)
                .builder();
        if (isMulti) {
            view.findViewById(R.id.operationLL).setVisibility(View.VISIBLE);
            selectAllTV.setVisibility(View.VISIBLE);
            view.findViewById(R.id.line).setVisibility(View.VISIBLE);
            view.findViewById(R.id.cancelTV).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWindow.dismiss();
                }
            });
            view.findViewById(R.id.sureTV).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<BaseSelectModel<T>> selectModels = mAdapter.getSelectModels();
                    ((OnMultiSelectListener) onSelectListener).callBack(selectModels);
                    mWindow.dismiss();
                }
            });
            selectAllTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean select;
                    if ("全选".equals(selectAllTV.getText().toString())) {
                        selectAllTV.setText("全不选");
                        select = true;
                    } else {
                        selectAllTV.setText("全选");
                        select = false;
                    }
                    if (!Utils.isEmpty(datas)) {
                        for (BaseSelectModel<T> t : datas) {
                            t.setSelect(select);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        } else {
            view.findViewById(R.id.operationLL).setVisibility(View.GONE);
            selectAllTV.setVisibility(View.GONE);
            view.findViewById(R.id.line).setVisibility(View.GONE);
        }
        contentLV.setLayoutManager(new LinearLayoutManager(ct));
        contentLV.setAdapter(mAdapter);
        DisplayUtil.setBackgroundAlpha(ct, 0.5f);
        mWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                DisplayUtil.setBackgroundAlpha(ct, 1f);
                if (onDismissListener != null) {
                    onDismissListener.onDismiss();
                }
            }
        });
        mWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }


}
