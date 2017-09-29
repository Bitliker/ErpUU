package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gxut.bitliker.baseutil.ui.base.BaseActivity;
import com.gxut.bitliker.baseutil.util.JSONUtil;
import com.gxut.bitliker.baseutil.util.Utils;
import com.gxut.bitliker.httpclient.HttpClient;
import com.gxut.bitliker.httpclient.request.Parameter;
import com.gxut.bitliker.httpclient.response.Failure;
import com.gxut.bitliker.httpclient.response.OnHttpCallback;
import com.gxut.bitliker.httpclient.response.Success;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.adapter.BaseSelectAdapter;
import com.gxut.bitliker.usoftchinauu.model.BaseSelectModel;
import com.gxut.bitliker.usoftchinauu.network.UrlHelper;

import java.util.ArrayList;
import java.util.List;

public class OAListActivity extends BaseActivity implements BaseSelectAdapter.OnItemClickListener {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oalist);
        initView();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(ct));
        loadData();
    }

    private void loadData() {
        showProgress();
        HttpClient.api().request(new Parameter.Builder()
                .url(UrlHelper.api().getoaconifgUrl())
                .mode(Parameter.GET)
                .bulid(), new OnHttpCallback() {
            @Override
            public void onSuccess(Success success) {
                handlerData(success.getJSONArray("listdata"));
                hideProgress();
            }

            @Override
            public void onFailure(Failure failure) {
                hideProgress();

            }
        });
    }

    private BaseSelectAdapter mAdapter;

    private void handlerData(JSONArray array) {
        List<BaseSelectModel> baseSelectModels = new ArrayList<>();
        baseSelectModels.add(new BaseSelectModel<>("请假申请", "Ask4Leave"));
        baseSelectModels.add(new BaseSelectModel<>("加班申请", "Workovertime"));
        baseSelectModels.add(new BaseSelectModel<>("出差申请", "FeePlease!CCSQ"));
        if (!Utils.isEmpty(array)) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject o = array.getJSONObject(i);
                String name = JSONUtil.getText(o, "MO_NAME");
                String caller = JSONUtil.getText(o, "MO_CALLER");
                boolean added = false;
                for (BaseSelectModel e : baseSelectModels) {
                    if (name.equals(e.getName()) && !Utils.isEmpty(caller)) {
                        e.setSub(caller);
                        added = true;
                        break;
                    }
                }
                if (!added && !Utils.isEmpty(name) && !Utils.isEmpty(caller)) {
                    baseSelectModels.add(new BaseSelectModel<>(name, caller));
                }
            }
        }
        if (!Utils.isEmpty(baseSelectModels)) {
            if (mAdapter == null) {
                mAdapter = new BaseSelectAdapter.Builder<>(ct)
                        .setAutoHeight(false)
                        .setShowCb(false)
                        .setMulti(false)
                        .setModels(baseSelectModels)
                        .setOnItemClickListener(this)
                        .builder();
                recyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setModels(baseSelectModels);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void itemClick(BaseSelectModel model, int position) {
        startActivity(new Intent(ct, FormDetailActivity.class)
                .putExtra("caller", model.getSub())
                .putExtra("title", model.getName())
        );
    }
}
