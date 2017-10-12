package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gxut.code.baseutil.ui.base.BaseActivity;
import com.gxut.code.baseutil.util.TimeUtil;
import com.gxut.code.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.adapter.WorkAdapter;
import com.gxut.bitliker.usoftchinauu.config.LocationHelper;
import com.gxut.bitliker.usoftchinauu.model.Locale;
import com.gxut.bitliker.usoftchinauu.model.Work;
import com.gxut.bitliker.usoftchinauu.presenter.WorkPresenter;
import com.gxut.bitliker.usoftchinauu.presenter.imp.IWorkView;
import com.gxut.bitliker.usoftchinauu.util.BDMapUtil;
import com.modular.poplibrary.pickerview.PickerBuilder;
import com.modular.poplibrary.pickerview.TimePickerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkActivity extends BaseActivity implements IWorkView, TimePickerView.OnTimeSelectListener {

    @BindView(R.id.calendarTV)
    TextView calendarTV;
    @BindView(R.id.distanceTv)
    TextView distanceTv;
    @BindView(R.id.addressTv)
    TextView addressTv;
    @BindView(R.id.workRecycler)
    RecyclerView workRecycler;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    private Date selectDate;
    private WorkAdapter mAdapter = null;
    private WorkPresenter mPresenter;
    private Locale neerLocale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        ButterKnife.bind(this);
        initData();
        initEvent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            startActivity(new Intent(ct, InputWorkActivity.class)
                    .putExtra("location", neerLocale));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationHelper.api().requestLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.calendarTV, R.id.addressRefresh, R.id.signinBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.calendarTV:
                showTimeSelect();
                break;
            case R.id.addressRefresh:
                LocationHelper.api().requestLocation();
                break;
            case R.id.signinBtn:
                if (canSubmit())
                    mPresenter.submit(false);
                break;
        }
    }

    private boolean canSubmit() {
        String selectDay = TimeUtil.date2Str(selectDate, TimeUtil.YMD);
        String today = TimeUtil.long2Str(TimeUtil.YMD);
        if (today.equals(selectDay)) {
            return true;
        } else {
            showMessage("当前日期无法签到");
            return false;
        }
    }


    private void initData() {
        mPresenter = new WorkPresenter(this);
        selectDate = new Date();
        calendarTV.setText(TimeUtil.date2Str(selectDate, TimeUtil.YMD));
        dateChange();
    }

    private void initEvent() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LocationHelper.api().requestLocation();
                dateChange();
            }
        });
    }

    private void showTimeSelect() {
        Calendar starDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        starDate.set(2017, 1, 1);
        endDate.set(2018, 1, 1);

        new PickerBuilder(ct)
                .setRang(starDate, endDate)
                .setType(PickerBuilder.Type.YMD)
                .show(new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        selectDate = date;
                        dateChange();
                    }
                });
    }

    private void dateChange() {
        calendarTV.setText(TimeUtil.long2Str(selectDate.getTime(), TimeUtil.YMD));
        mPresenter.loadData(selectDate.getTime());
    }


    @Override
    public void showProgress() {
        if (!swipeRefresh.isRefreshing())
            super.showProgress();
    }

    @Override
    public void hideProgress() {
        swipeRefresh.setRefreshing(false);
        super.hideProgress();
    }

    @Override
    public void neerLocale(Locale neerLocale) {
        this.neerLocale = neerLocale;
        Locale myLocale = LocationHelper.api().getLocation();
        String address;
        if (myLocale == null) {
            address = neerLocale.getAddress();
        } else {
            address = myLocale.getAddress();
        }

        float distance = BDMapUtil.workDistance(neerLocale.getLatLng());
        addressTv.setText("当前位置   " + (Utils.isEmpty(address) ? "" : address));
        distanceTv.setText("距离公司  " + distance + "米");
    }

    @Override
    public void showModel(List<Work> works) {
        hideProgress();
        if (mAdapter == null) {
            mAdapter = new WorkAdapter(ct, works);
            workRecycler.setLayoutManager(new LinearLayoutManager(ct));
            workRecycler.setAdapter(mAdapter);
        } else mAdapter.setWorks(works);
    }

    @Override
    public void doResult(boolean ok, String message) {
        showMessage(message);
        if (ok) {
            selectDate = new Date();
            mPresenter.loadData(selectDate.getTime());
        }
    }

    @Override
    public void onTimeSelect(Date date, View v) {
        selectDate = date;
        dateChange();
    }
}
