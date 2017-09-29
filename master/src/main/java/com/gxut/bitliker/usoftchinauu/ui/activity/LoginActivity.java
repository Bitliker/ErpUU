package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.gxut.bitliker.baseutil.ui.base.BaseActivity;
import com.gxut.bitliker.baseutil.util.LogUtil;
import com.gxut.bitliker.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.config.SettingSp;
import com.gxut.bitliker.usoftchinauu.ui.widget.ClearEditText;
import com.gxut.bitliker.usoftchinauu.util.LoginHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class LoginActivity extends BaseActivity {

    @BindView(R.id.account_et)
    ClearEditText account_et;
    @BindView(R.id.password_et)
    ClearEditText password_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        String account = SettingSp.api().getString(SettingSp.ACCOUNT);
        String password = SettingSp.api().getString(SettingSp.PASSWORD);
        if (!Utils.isEmpty(account)) {
            account_et.setText(account);
        }
        if (!Utils.isEmpty(password)) {
            password_et.setText(password);
        }
    }

    @Override
    protected boolean showHomeBar() {
        return false;
    }

    @OnClick(R.id.login_btn)
    public void login() {
        LogUtil.i("login");
        if (!canLogin()) return;
        try {
            LoginHelper helper = new LoginHelper(ct);
            helper.account(Utils.getText(account_et), Utils.getText(password_et));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean canLogin() {
        if (TextUtils.isEmpty(account_et.getText())) {
            showMessage("请输入账号");
            account_et.setShakeAnimation();
            return false;
        } else if (!Utils.isMobile(account_et.getText().toString())) {
            showMessage("账号格式不正确");
            account_et.setShakeAnimation();
            return false;
        } else {
            SettingSp.api().put(SettingSp.ACCOUNT, account_et.getText().toString());
        }
        if (Utils.isEmpty(password_et.getText())) {
            showMessage("请输入密码");
            password_et.setShakeAnimation();
            return false;
        } else {
            SettingSp.api().put(SettingSp.PASSWORD, password_et.getText().toString());
        }
        return true;
    }
}
