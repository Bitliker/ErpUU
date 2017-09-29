package com.gxut.bitliker.usoftchinauu.ui.widget;

import android.app.AlertDialog;
import android.content.Context;

import com.gxut.bitliker.usoftchinauu.R;


/**
 * Created by Bitliker on 2017/7/5.
 */

public class CustomProgressDialog extends AlertDialog {

    protected CustomProgressDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_custom_progress);
    }


}
