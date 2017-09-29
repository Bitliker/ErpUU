package com.gxut.bitliker.usoftchinauu.adapter;

import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.modular.poplibrary.pickerview.TimePickerView;
import com.gxut.bitliker.baseutil.ui.base.BaseActivity;
import com.gxut.bitliker.baseutil.util.TimeUtil;
import com.gxut.bitliker.baseutil.util.Utils;
import com.modular.poplibrary.pickerview.PickerBuilder;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.model.BaseSelectModel;
import com.gxut.bitliker.usoftchinauu.model.FormDetail;
import com.gxut.bitliker.usoftchinauu.model.User;
import com.gxut.bitliker.usoftchinauu.util.SelectPopupBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Bitliker on 2017/8/26.
 */

public class FormDetailAdapter extends RecyclerView.Adapter<FormDetailAdapter.ViewHolder> {

    private boolean showEdit;
    private BaseActivity ct;
    private List<FormDetail> models;

    public FormDetailAdapter(boolean showEdit, BaseActivity ct, List<FormDetail> models) {
        this.showEdit = showEdit;
        this.ct = ct;
        this.models = models;
    }

    public void setModels(List<FormDetail> models) {
        this.models = models;
    }

    public List<FormDetail> getModels() {
        return models;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }


    @Override
    public int getItemCount() {
        return Utils.getSize(this.models);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView valuesTV;
        TextView captionTV;
        EditText valuesET;

        public ViewHolder(ViewGroup parent) {
            this(LayoutInflater.from(ct).inflate(showEdit ? R.layout.item_input_formdetail : R.layout.item_formdetail, parent, false));
        }

        public ViewHolder(View itemView) {
            super(itemView);
            captionTV = (TextView) itemView.findViewById(R.id.captionTV);
            if (showEdit) {
                valuesET = (EditText) itemView.findViewById(R.id.valuesET);
            } else {
                valuesTV = (TextView) itemView.findViewById(R.id.valuesTV);
            }
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final FormDetail detail = models.get(position);
        holder.captionTV.setText(detail.getCaption());
        if (detail.isTAG()) {
            setvaluesText(holder, View.GONE);
            holder.captionTV.setTextColor(ct.getResources().getColor(R.color.colorPrimaryDark));
            return;
        }
        setvaluesText(holder, View.VISIBLE);
        holder.captionTV.setTextColor(ct.getResources().getColor(R.color.textColorHine));
        setvaluesText(holder, detail.getValues());
        if (showEdit) {
            if (detail.isSelect()) {
                holder.valuesET.setHint("请选择");
                holder.valuesET.setFocusable(true);
                holder.valuesET.setFocusableInTouchMode(false);
                holder.valuesET.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (detail.isSelectDate()) {
                            showTimeSelect(position);
                        } else if (!Utils.isEmpty(detail.getCombostores())) {
                            showSelect("选择" + detail.getCaption(), detail.getCombostores(), position);
                        }
                    }
                });
            } else {
                if (detail.isNumber()) {
                    holder.valuesET.setHint("请输入数字");
                    holder.valuesET.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    holder.valuesET.setHint("请输入");
                    holder.valuesET.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                holder.valuesET.addTextChangedListener(new TextChangListener(holder, position));
            }
        }
    }


    private void setvaluesText(ViewHolder holder, int visibility) {
        if (showEdit) {
            holder.valuesET.setVisibility(visibility);
        } else {
            holder.valuesTV.setVisibility(visibility);
        }
    }

    private void setvaluesText(ViewHolder holder, String message) {
        if (message == null) message = "";
        if (showEdit) {
            holder.valuesET.setText(message);
        } else {
            holder.valuesTV.setText(message);
        }
    }

    private void showSelect(String title, List<FormDetail.Combostore> combostores, final int position) {
        if (Utils.isEmpty(combostores)) return;
        List<BaseSelectModel<Parcelable>> datas = new ArrayList<>();
        for (FormDetail.Combostore e : combostores) {
            datas.add(new BaseSelectModel<Parcelable>(e.getValues(), e.getDisplay()));
        }
        new SelectPopupBuilder(ct)
                .setTitle(title)
                .setOnSelectListener(new SelectPopupBuilder.OnRadioSelectListener<User>() {
                    @Override
                    public void callBack(BaseSelectModel<User> data) {
                        if (position >= 0 && Utils.getSize(models) > position) {
                            models.get(position).setValues(data.getName(), data.getSub());
                            notifyItemChanged(position);
                        }
                    }
                })
                .setDatas(datas)
                .builder();
    }

    private void showTimeSelect(final int position) {
        Calendar starDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        starDate.set(2017, 1, 1);
        endDate.set(2018, 1, 1);
        new PickerBuilder(ct)
                .setRang(starDate, endDate)
                .setType(PickerBuilder.Type.YMD_HMS)
                .show(new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        if (position >= 0 && Utils.getSize(models) > position) {
                            models.get(position).setValues(TimeUtil.date2Str(date, TimeUtil.YMD_HMS));
                            notifyItemChanged(position);
                        }
                    }
                });
    }

    private class TextChangListener implements TextWatcher {
        ViewHolder hodler;
        private int position;

        public TextChangListener(ViewHolder hodler, int position) {
            this.hodler = hodler;
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (this.position >= 0) {
                if (this.hodler.valuesET != null) {
                    String valueEt = this.hodler.valuesET.getText().toString();
                    models.get(this.position).setValues(valueEt == null ? "" : valueEt);
                }
            }
        }
    }
}
