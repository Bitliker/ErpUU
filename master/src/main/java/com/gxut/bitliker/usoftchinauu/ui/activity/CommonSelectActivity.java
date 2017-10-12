package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.gxut.code.baseutil.ui.base.BaseActivity;
import com.gxut.code.baseutil.util.LogUtil;
import com.gxut.ui.refreshlayout.refresh.smart.SmartRefreshLayout;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.ui.widget.SearchView;

import butterknife.BindView;
import butterknife.ButterKnife;



public class CommonSelectActivity extends BaseActivity implements SearchView.OnTextChangedListener {

    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.refreshView)
    RecyclerView refreshView;
    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_select);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        searchView.setChangedListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SmartRefreshLayout.onRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtil.i("onRefresh");
            }

            @Override
            public void onLoadMore() {
                LogUtil.i("onLoadMore");
                swipeRefreshLayout.stopRefresh();
            }
        });
        refreshView.setLayoutManager(new LinearLayoutManager(ct));
    }

    @Override
    public void onTextChanged(String s) {

    }

}
