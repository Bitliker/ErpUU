package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.adapter.ListFormDetailAdapter;
import com.gxut.bitliker.usoftchinauu.config.AppConfig;
import com.gxut.bitliker.usoftchinauu.model.FormDetailList;
import com.gxut.bitliker.usoftchinauu.network.UrlHelper;
import com.gxut.bitliker.usoftchinauu.ui.widget.OnItemTouchListener;
import com.gxut.bitliker.usoftchinauu.ui.widget.SearchView;
import com.gxut.code.baseutil.ui.base.BaseActivity;
import com.gxut.code.baseutil.util.Utils;
import com.gxut.code.network.HttpClient;
import com.gxut.code.network.request.Parameter;
import com.gxut.code.network.response.Failure;
import com.gxut.code.network.response.OnHttpCallback;
import com.gxut.code.network.response.Success;
import com.gxut.code.network.util.JSONUtil;
import com.gxut.ui.refreshlayout.refresh.smart.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bitliker on 2017/8/28.
 */

public class ListFormDetailActivity extends BaseActivity implements OnHttpCallback {
    private final int LOAD_DATA = 11;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.listRV)
    RecyclerView contentRV;
    @BindView(R.id.refreshView)
    SmartRefreshLayout refreshView;
    private ListFormDetailAdapter mAdapter;
    private String caller;
    private int page;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.add == item.getItemId()) {
            startActivity(new Intent(ct, FormDetailActivity.class)
                    .putExtra("caller", caller)
                    .putExtra("title", getSupportActionBar().getTitle()));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        page = 1;
        Intent intent = getIntent();
        if (intent != null) {
            caller = intent.getStringExtra("caller");
            String title = intent.getStringExtra("title");
            if (!Utils.isEmpty(title))
                getSupportActionBar().setTitle(title);
        }
        if (caller == null) {
            finish();
            return;
        }
        contentRV.setLayoutManager(new LinearLayoutManager(ct));
        contentRV.setItemAnimator(new DefaultItemAnimator());
        refreshView.setOnRefreshListener(new SmartRefreshLayout.onRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                loadData();
            }

            @Override
            public void onLoadMore() {
                page++;
                loadData();
            }
        });
        loadData();
    }


    private void loadData() {
        String emCode = AppConfig.api().getLoginUser().getEmCode();
        String condition = null;
        if ("Ask4Leave".equals(caller)) {
            condition = "va_emcode='" + emCode + "'";
        } else if ("SpeAttendance".equals(caller)) {
            condition = "sa_appmancode='" + emCode + "'";
        } else if ("Workovertime".equals(caller) || "ExtraWork$".equals(caller)) {
            condition = "wod_empcode='" + emCode + "'";
        } else if ("FeePlease!CCSQ".equals(caller) || "FeePlease!CCSQ!new".equals(caller)) {
            condition = "FP_PEOPLE2='" + emCode + "'";
        } else if ("FeePlease!FYBX".equals(caller)) {
            condition = "fp_pleasemancode='" + emCode + "'";
        }
        if (condition == null) return;
        if (!refreshView.isRefreshing()) {
            showProgress();
        }
        HttpClient.api().request(new Parameter.Builder()
                .url(UrlHelper.api().commonListUrl())
                .addParams("condition", condition)
                .addParams("caller", caller.equals("Workovertime") ? "WorkovertimeDetail" : caller)
                .addParams("page", page)
                .addParams("pageSize", 10)
                .mode(Parameter.GET)
                .record(LOAD_DATA)
                .bulid(), this);
    }


    private void handlerData(String idKey, JSONArray columns, JSONArray listdata) {
        if (Utils.isEmpty(columns) || Utils.isEmpty(listdata)) {
            showMessage("没有数据");
            return;
        }
        List<FormDetailList> formDetailLists = new ArrayList<>();
        for (int i = 0; i < listdata.size(); i++) {
            JSONObject data = listdata.getJSONObject(i);
            FormDetailList formDetailList = new FormDetailList();
            formDetailList.setId(JSONUtil.getInt(data, idKey));
            for (int j = 0; j < columns.size(); j++) {
                JSONObject column = columns.getJSONObject(j);
                String valuesKey = JSONUtil.getText(column, "dataIndex");
                String caption = JSONUtil.getText(column, "caption");
                String values = JSONUtil.getText(data, valuesKey);
                if (Utils.isEmpty(valuesKey) || Utils.isEmpty(caption) || Utils.isEmpty(values))
                    continue;
                if ("单据状态".equals(caption)||"状态".equals(caption)) {
                    formDetailList.setStatus(values);
                }
                formDetailList.addItemData(caption, values, valuesKey);
            }
            if (formDetailList.getItemDatas().size() <= 0) continue;
            formDetailList.setCaller(caller);
            formDetailLists.add(formDetailList);
        }
        setData2Adapter(formDetailLists);
    }


    public void setData2Adapter(List<FormDetailList> formDetailLists) {
        if (mAdapter == null) {
            mAdapter = new ListFormDetailAdapter(ct, formDetailLists);
            contentRV.setAdapter(mAdapter);
            contentRV.addOnItemTouchListener(new OnItemTouchListener(contentRV) {
                @Override
                public void onItemClick(RecyclerView.ViewHolder vh) {
                    if (mAdapter == null) return;
                    List<FormDetailList> models = mAdapter.getModels();
                    int position = vh.getAdapterPosition();
                    if (position >= 0 && Utils.getSize(models) > position) {
                        FormDetailList selectModel = models.get(position);
                        startActivity(new Intent(ct, FormDetailActivity.class)
                                .putExtra("caller", selectModel.getCaller())
                                .putExtra("id", selectModel.getId())
                                .putExtra("status", selectModel.getStatus())
                                .putExtra("title", getSupportActionBar().getTitle()));
                    }
                }
            });
        } else {
            if (page == 1) {
                mAdapter.setModels(formDetailLists);
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter.addModels(formDetailLists);
            }
        }
    }

    @Override
    public void onSuccess(Success success) {
        handlerData(JSONUtil.getText(success.getJsonObject(), "keyField"), success.getJSONArray("columns"), success.getJSONArray("listdata"));
        hideProgress();
        refreshView.stopRefresh();
    }

    @Override
    public void onFailure(Failure failure) {
        hideProgress();
        showMessage(failure.getMessage());
    }
}
