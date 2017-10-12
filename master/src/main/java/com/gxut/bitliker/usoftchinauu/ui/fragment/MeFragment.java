package com.gxut.bitliker.usoftchinauu.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.gxut.code.baseutil.ui.base.BaseFragment;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.config.AppConfig;
import com.gxut.bitliker.usoftchinauu.config.BroadcastManager;
import com.gxut.bitliker.usoftchinauu.config.SettingSp;
import com.gxut.bitliker.usoftchinauu.db.dao.UserDao;
import com.gxut.bitliker.usoftchinauu.model.User;
import com.gxut.bitliker.usoftchinauu.service.AutoTaskService;
import com.gxut.bitliker.usoftchinauu.ui.activity.LoginActivity;
import com.gxut.bitliker.usoftchinauu.ui.widget.SwitchView;
import com.gxut.bitliker.usoftchinauu.util.AlarmUtil;
import com.gxut.bitliker.usoftchinauu.util.LoginHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.gxut.bitliker.usoftchinauu.util.LoginHelper.SELECT_COMPANY;


public class MeFragment extends BaseFragment {

    @BindView(R.id.companyTv)
    TextView companyTv;
    @BindView(R.id.masterTv)
    TextView masterTv;
    @BindView(R.id.userTv)
    TextView userTv;
    @BindView(R.id.autoSW)
    SwitchView autoSW;

    Unbinder unbinder;
    private LoginHelper loginHelper;

    private BroadcastReceiver userUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                User user = intent.getParcelableExtra(BroadcastManager.MODEL_KEY);
                if (user != null) {
                    companyTv.setText(user.getCompany());
                    masterTv.setText(user.getMasterName());
                    if (SettingSp.api().getBoolean(SettingSp.AUTO_TASK, false)) {
                        AutoTaskService.api().start();
                    }
                }
            }
        }
    };

    @Override
    protected int inflater() {
        return R.layout.fragment_me;
    }

    @Override
    protected void createView(Bundle savedInstanceState, boolean createView) {
        unbinder = ButterKnife.bind(this, getRootView());
        BroadcastManager.registerReceiver(userUpdateReceiver, new IntentFilter(BroadcastManager.USER_CHANGE));
        User user = AppConfig.api().getLoginUser();
        loginHelper = new LoginHelper(ct);
        if (user != null) {
            userTv.setText(user.getEmName());
            companyTv.setText(user.getCompany());
            masterTv.setText(user.getMasterName());
        }
        initEnevt();
    }

    private void initEnevt() {
        autoSW.setChecked(SettingSp.api().getBoolean(SettingSp.AUTO_TASK, false));
        autoSW.setOnCheckedChangeListener(new SwitchView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean isChecked) {
                SettingSp.api().put(SettingSp.AUTO_TASK, isChecked);
                if (isChecked) {
                    AlarmUtil.startAlarm(AlarmUtil.AUTOTASK_REQUESTCODE, AlarmUtil.AUTOTASK_ACTION, System.currentTimeMillis() + 1000);
                } else {
                    AlarmUtil.cancelAlarm(AlarmUtil.AUTOTASK_REQUESTCODE, AlarmUtil.AUTOTASK_ACTION);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        BroadcastManager.unregisterReceiver(userUpdateReceiver);
    }

    @OnClick({R.id.company_rl, R.id.master_rl, R.id.clear_rl, R.id.unLoginBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.company_rl:
                selectCompany();
                break;
            case R.id.master_rl:
                loginHelper.getAllMasters(AppConfig.api().getLoginUser(), true);
                break;
            case R.id.clear_rl:
                showClearDialog();
                break;
            case R.id.unLoginBtn:
                showSureUnlogin();
                break;
        }
    }

    private void selectCompany() {
        Observable.create(new ObservableOnSubscribe<List<User>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<User>> e) throws Exception {
                e.onNext(UserDao.api().queryCompany());
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(@NonNull List<User> users) throws Exception {
                        loginHelper.showCompanySelectDialog(users, SELECT_COMPANY);
                    }
                });
    }

    private void showSureUnlogin() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.dialog_title)
                .setMessage("是否确定退出登录").setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endofLogin();
            }
        }).setNegativeButton(R.string.cancel, null).show();
    }

    private void showClearDialog() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.dialog_title)
                .setMessage("是否确定清空缓存").setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO 清空缓存
            }
        }).setNegativeButton(R.string.cancel, null).show();
    }

    private void endofLogin() {
        SettingSp.api().remove(SettingSp.MASTER);
        UserDao.api().deleteLoginUser(AppConfig.api().getLoginUser());
        AppConfig.api().setLoginUser(null);
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }
}
