package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.gxut.code.baseutil.ui.base.BaseActivity;
import com.gxut.code.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.adapter.FormDetailAdapter;
import com.gxut.bitliker.usoftchinauu.model.FormDetail;
import com.gxut.bitliker.usoftchinauu.presenter.FormDetailPresenter;
import com.gxut.bitliker.usoftchinauu.presenter.imp.IFormDetail;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class FormDetailActivity extends BaseActivity implements IFormDetail {


    @BindView(R.id.deleteBtn)
    Button deleteBtn;
    @BindView(R.id.saveBtn)
    Button saveBtn;
    @BindView(R.id.operationLL)
    LinearLayout operationLL;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private boolean inputing;
    private FormDetailPresenter mPresenter;
    private FormDetailAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_detail);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(ct));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Intent intent = getIntent();
        if (intent != null) {
            String status = intent.getStringExtra("status");
            if (Utils.isEmpty(status)) {
                inputing = true;
                setViewVisibility(View.GONE, deleteBtn);
            } else if ("已提交".equals(status)) {
                setViewVisibility(View.GONE, saveBtn);
            } else {
                setViewVisibility(View.GONE, operationLL);
            }
            mPresenter = new FormDetailPresenter(this, this, inputing, intent);
        } else {
            showMessage("intent == null");
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (inputing) {
            getMenuInflater().inflate(R.menu.menu_list, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.list == item.getItemId()) {
            startActivity(new Intent(ct, ListFormDetailActivity.class)
                    .putExtra("caller", mPresenter.getCaller())
                    .putExtra("title", getSupportActionBar().getTitle()));
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.deleteBtn, R.id.saveBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.deleteBtn:
                showUnsubmit(true);
                break;
            case R.id.saveBtn:
                if (mAdapter != null) {
                    List<FormDetail> formDetails = mAdapter.getModels();
                    if (!Utils.isEmpty(formDetails)) {
                        mPresenter.submit(formDetails);
                    }
                }
                break;
        }
    }

    private void setViewVisibility(int visibility, View... views) {
        if (views == null || views.length <= 0) return;
        for (View v : views)
            v.setVisibility(visibility);

    }

    private void showUnsubmit(final boolean neerDetele) {
        new AlertDialog.Builder(ct).setTitle("提示").setMessage(neerDetele ? "是否确定删除" : "是否确定反提交").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.unsubmit(neerDetele);
            }
        }).setNegativeButton("取消", null).show();
    }

    @Override
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void showModes(boolean inputing, List<FormDetail> models) {
        mAdapter = new FormDetailAdapter(inputing, ct, models);
        recyclerView.setAdapter(mAdapter);
        hideProgress();
    }

}
