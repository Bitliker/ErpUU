package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.fastjson.JSONArray;
import com.gxut.bitliker.baseutil.ui.base.BaseActivity;
import com.gxut.bitliker.baseutil.util.Utils;
import com.module.recyclerlibrary.ui.refresh.smart.SmartRefreshLayout;
import com.gxut.bitliker.httpclient.HttpClient;
import com.gxut.bitliker.httpclient.request.Parameter;
import com.gxut.bitliker.httpclient.response.Failure;
import com.gxut.bitliker.httpclient.response.OnHttpCallback;
import com.gxut.bitliker.httpclient.response.Success;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.adapter.BaseSelectAdapter;
import com.gxut.bitliker.usoftchinauu.config.AppConfig;
import com.gxut.bitliker.usoftchinauu.model.BaseSelectModel;
import com.gxut.bitliker.usoftchinauu.model.Daily;
import com.gxut.bitliker.usoftchinauu.network.UrlHelper;
import com.gxut.bitliker.usoftchinauu.ui.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListDailyActivity extends BaseActivity implements OnHttpCallback, BaseSelectAdapter.OnItemClickListener<Daily> {
    private final int TAG_PAGEINDEX = 1;
    private final int LOAD_DATA = 11;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.listRV)
    RecyclerView listRV;
    @BindView(R.id.refreshView)
    SmartRefreshLayout refreshView;
    private int pageIndex;
    private BaseSelectAdapter<Daily> mAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.add == item.getItemId()) {
            startActivity(new Intent(ct, InputWorkActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        ButterKnife.bind(this);
        initView();
        initEvent();
    }

    private void initEvent() {
        searchView.setChangedListener(new SearchView.OnTextChangedListener() {
            @Override
            public void onTextChanged(String s) {

            }
        });
        refreshView.setOnRefreshListener(new SmartRefreshLayout.onRefreshListener() {
            @Override
            public void onRefresh() {
                ListDailyActivity.this.onRefresh();
            }

            @Override
            public void onLoadMore() {
                ListDailyActivity.this.onLoadMore();
            }
        });
    }

    private void initView() {
        listRV.setLayoutManager(new LinearLayoutManager(this));
        listRV.setItemAnimator(new DefaultItemAnimator());
        pageIndex = 1;
        loadData();
    }

    private void onRefresh() {
        pageIndex = 1;
        loadData();
    }

    private void onLoadMore() {
        pageIndex++;
        loadData();
    }


    private void loadData() {
        if (!refreshView.isRefreshing()) {
            showProgress();
        }
        HttpClient.api().request(new Parameter.Builder()
                .url(UrlHelper.api().getWorkDailyUrl())
                .addParams("emcode", AppConfig.api().getLoginUser().getEmCode())
                .addParams("pageIndex", pageIndex)
                .mode(Parameter.GET)
                .addTag(TAG_PAGEINDEX, pageIndex)
                .record(LOAD_DATA)
                .bulid(), this);
    }


    @Override
    public void onSuccess(Success success) {
        hideProgress();
        switch (success.getRecord()) {
            case LOAD_DATA:
                handlerWorkDaily(success.getJSONArray("listdata"));
                break;
        }
    }

    @Override
    public void onFailure(Failure failure) {
        hideProgress();
        showMessage(failure.getMessage());
    }


    private void handlerWorkDaily(JSONArray listdata) {
        if (Utils.isEmpty(listdata)) {
            if (pageIndex > 1) {
                pageIndex--;
            }
            stopLoading();
        } else {
            List<BaseSelectModel<Daily>> dailys = new ArrayList<>();
            for (int i = 0; i < listdata.size(); i++) {
                Daily data = new Daily(listdata.getJSONObject(i));
                if (!data.isEmpty()) {
                    BaseSelectModel<Daily> baseModel = new BaseSelectModel<>(
                            data.getDate()
                            , data.getComment()
                            , data.getStatus());
                    baseModel.setData(data);
                    dailys.add(baseModel);
                }
            }
            setData2Adapter(dailys);
        }
    }

    private void setData2Adapter(List<BaseSelectModel<Daily>> dailys) {
        if (mAdapter == null) {
            mAdapter = new BaseSelectAdapter.Builder<Daily>(ct)
                    .setMulti(false)
                    .setShowCb(false)
                    .setAutoHeight(true)
                    .setModels(dailys)
                    .setOnItemClickListener(this)
                    .builder();
            listRV.setAdapter(mAdapter);
        } else {
            if (pageIndex == 1) {
                mAdapter.setModels(dailys);
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter.addModels(dailys);
            }
        }
        stopLoading();
    }

    private void stopLoading() {
        refreshView.stopRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 20 && resultCode == 20) {
            pageIndex = 1;
            loadData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void itemClick(BaseSelectModel<Daily> model, int position) {
        if (model == null) return;
        Daily daily = model.getData();
        if ("在录入".equals(daily.getStatus())) {
            startActivity(new Intent(ct, InputDailyActivity.class)
                    .putExtra("id", daily.getId())
                    .putExtra("summary", daily.getComment())
                    .putExtra("plan", daily.getPlan())
                    .putExtra("experience", daily.getExperience()));
        } else {
            startActivityForResult(new Intent(ct, DetailsDailyActivity.class)
                    .putExtra("model", daily), 20);
        }
    }
}
