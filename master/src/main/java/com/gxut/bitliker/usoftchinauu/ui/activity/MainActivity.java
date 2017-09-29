package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gxut.bitliker.baseutil.ui.base.BaseActivity;
import com.gxut.bitliker.baseutil.ui.base.BaseFragment;
import com.gxut.bitliker.baseutil.util.PermissionUtil;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.config.LocationHelper;
import com.gxut.bitliker.usoftchinauu.config.SettingSp;
import com.gxut.bitliker.usoftchinauu.ui.fragment.MeFragment;
import com.gxut.bitliker.usoftchinauu.ui.fragment.NewsFragment;
import com.gxut.bitliker.usoftchinauu.ui.fragment.OAFragment;
import com.gxut.bitliker.usoftchinauu.util.AlarmUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.news_rb)
    RadioButton newsRb;
    @BindView(R.id.oa_rb)
    RadioButton oaRb;
    @BindView(R.id.me_rb)
    RadioButton meRb;
    @BindView(R.id.indicator_rg)
    RadioGroup indicatorRg;

    private RadioButton lastSelectRb;
    private BaseFragment lastFragment;
    private MeFragment meFragment;
    private NewsFragment newsFragment;
    private OAFragment oaFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean showHomeBar() {
        return false;
    }

    private void initView() {
        if (SettingSp.api().getBoolean(SettingSp.AUTO_TASK, false)) {
            AlarmUtil.startAlarm(AlarmUtil.AUTOTASK_REQUESTCODE, AlarmUtil.AUTOTASK_ACTION, System.currentTimeMillis() + 1000);
        }
        indicatorRg.setOnCheckedChangeListener(this);
        oaFragment = new OAFragment();
        selectFragment(oaRb, oaFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPermission();
    }

    private void initPermission() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE};
        for (String p : permissions) {
            if (PermissionUtil.lacksPermissions(ct, p)) {
                PermissionUtil.requestPermission(ct, p);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtil.DEFAULT_REQUEST) {
            if (requestCode == PermissionUtil.DEFAULT_REQUEST) {
                if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    LocationHelper.api().requestLocation();
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.news_rb:
                if (lastSelectRb != newsRb) {
                    newsRb.setTextColor(getResources().getColor(R.color.pass));
                    lastSelectRb.setTextColor(getResources().getColor(R.color.noPass));
                    if (newsFragment == null)
                        newsFragment = new NewsFragment();
                    selectFragment(newsRb, newsFragment);
                }
                break;
            case R.id.oa_rb:
                if (lastSelectRb != oaRb) {
                    oaRb.setTextColor(getResources().getColor(R.color.pass));
                    lastSelectRb.setTextColor(getResources().getColor(R.color.noPass));
                    if (oaFragment == null)
                        oaFragment = new OAFragment();
                    selectFragment(oaRb, oaFragment);
                }
                break;
            case R.id.me_rb:
                if (lastSelectRb != meRb) {
                    meRb.setTextColor(getResources().getColor(R.color.pass));
                    lastSelectRb.setTextColor(getResources().getColor(R.color.noPass));
                    if (meFragment == null)
                        meFragment = new MeFragment();
                    selectFragment(meRb, meFragment);
                }
                break;
        }
    }

    private void selectFragment(RadioButton selectRb, BaseFragment addFragment) {
        if (addFragment == null) return;
        if (addFragment == this.lastFragment) return;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (this.lastFragment != null)
            fragmentTransaction.hide(this.lastFragment);
        if (addFragment.isAdded())
            fragmentTransaction.show(addFragment);
        else fragmentTransaction.add(R.id.content_fl, addFragment);
        this.lastSelectRb = selectRb;
        this.lastFragment = addFragment;
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(selectRb.getText());
    }
}
