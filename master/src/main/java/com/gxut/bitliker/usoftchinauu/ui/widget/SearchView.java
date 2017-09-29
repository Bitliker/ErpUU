package com.gxut.bitliker.usoftchinauu.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.gxut.bitliker.usoftchinauu.R;


/**
 * Created by Bitliker on 2017/5/15.
 */

public class SearchView extends RelativeLayout implements TextWatcher {
    private ClearEditText sreachEt;
    private OnTextChangedListener onTextChangedListener;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.common_sreach, this);
        sreachEt = (ClearEditText) findViewById(R.id.sreachEt);
        sreachEt.addTextChangedListener(this);

    }

    public void setHineText(String hine) {
        sreachEt.setHint(hine);
    }

    public void setText(String hine) {
        sreachEt.setText(hine);
    }

    public String getText() {
        return TextUtils.isEmpty(sreachEt.getText()) ? "" : sreachEt.getText().toString();
    }


    public void setChangedListener(OnTextChangedListener onTextChangedListener) {
        this.onTextChangedListener = onTextChangedListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (onTextChangedListener != null) {
            onTextChangedListener.onTextChanged(TextUtils.isEmpty(s) ? "" : s.toString());
        }
    }

    public interface OnTextChangedListener {
        void onTextChanged(String s);
    }
}
