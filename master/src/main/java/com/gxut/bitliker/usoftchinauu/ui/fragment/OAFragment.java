package com.gxut.bitliker.usoftchinauu.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gxut.bitliker.baseutil.ui.base.BaseFragment;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.ui.activity.InputDailyActivity;
import com.gxut.bitliker.usoftchinauu.ui.activity.OAListActivity;
import com.gxut.bitliker.usoftchinauu.ui.activity.WorkActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Bitliker on 2017/6/21.
 */

public class OAFragment extends BaseFragment {
    Unbinder unbinder;


    @Override
    protected int inflater() {
        return R.layout.fragment_oa;
    }

    @Override
    protected void createView(Bundle savedInstanceState, boolean createView) {
        unbinder = ButterKnife.bind(this, getRootView());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.workTv, R.id.taskTv,R.id.dailyTv, R.id.inputFormTv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.workTv:
                startActivity(new Intent(ct, WorkActivity.class));
                break;
            case R.id.inputFormTv:
                startActivity(new Intent(ct, OAListActivity.class));
                break;
            case R.id.dailyTv:
                startActivity(new Intent(ct, InputDailyActivity.class));
                break;

        }
    }

}


