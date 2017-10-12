package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.gxut.code.baseutil.ui.base.BaseActivity;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.config.AppConfig;

public class IndexActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_index);
        initData();
    }


    private void initData() {
        if (AppConfig.api().getLoginUser() == null)
            startActivity(new Intent(ct, LoginActivity.class));
        else startActivity(new Intent(ct, MainActivity.class));
        finish();
    }
}
