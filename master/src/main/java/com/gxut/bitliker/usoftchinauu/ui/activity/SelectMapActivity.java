package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.adapter.BaseSelectAdapter;
import com.gxut.bitliker.usoftchinauu.config.LocationHelper;
import com.gxut.bitliker.usoftchinauu.model.BaseSelectModel;
import com.gxut.bitliker.usoftchinauu.ui.widget.SearchView;
import com.gxut.bitliker.usoftchinauu.util.BDMapUtil;
import com.gxut.code.baseutil.ui.base.BaseActivity;
import com.gxut.code.baseutil.util.LogUtil;
import com.gxut.code.baseutil.util.Utils;
import com.gxut.ui.refreshlayout.refresh.smart.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SelectMapActivity extends BaseActivity implements BaseSelectAdapter.OnItemClickListener<PoiInfo>
        , SearchView.OnTextChangedListener {
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.bdMapView)
    MapView bdMapView;
    @BindView(R.id.listRV)
    RecyclerView listRV;
    @BindView(R.id.refreshView)
    SmartRefreshLayout refreshView;

    private BaseSelectAdapter<PoiInfo> mAdapter;
    private DataType type;
    private LatLng selectLaLng;//选择的地址
    private Comparator<BaseSelectModel<PoiInfo>> sortComparator;
    private int pagerNum;
    private PoiSearch mPoiSearch = null;
    private BaseSelectModel<PoiInfo> selectModel = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sure, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.sure == item.getItemId()) {
            if (mAdapter != null) {

                if (selectModel != null && selectModel.getData() != null) {
                    Intent intent = new Intent();
                    intent.putExtra(Utils.DEF_RESULT_MODEL, selectModel.getData());
                    setResult(Utils.DEF_RESULT_CODE, intent);
                    finish();
                } else {
                    showMessage("没有选择，不能确定");
                }
            } else {
                showMessage("没有选择，不能确定");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_map);
        ButterKnife.bind(this);
        initView();
        initIntent();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadByNeer();
            }
        }, 1000);
    }


    private void initView() {
        refreshView.setOnRefreshListener(new SmartRefreshLayout.onRefreshListener() {
            @Override
            public void onRefresh() {
                pagerNum = 0;
                loadData();
            }

            @Override
            public void onLoadMore() {
                pagerNum++;
                SelectMapActivity.this.loadData();
            }
        });
        searchView.setChangedListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ct);
        listRV.setLayoutManager(layoutManager);
    }

    private void loadData() {
        if (Utils.isEmpty(searchView.getText())) {
            //没有输入，选择获取周围信息
            loadByNeer();
        } else {
            //有输入，获取搜索信息
            loadSreachKey(searchView.getText());
        }
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            type = (DataType) intent.getSerializableExtra("type");
            selectLaLng = intent.getParcelableExtra(Utils.DEF_RESULT_MODEL);
            if (type == null) {
                type = DataType.NEER_AND_SEACH;
            }
            if (!Utils.isEmpty(title)) {
                getSupportActionBar().setTitle(title);
            }
        }
    }

    private void loadByNeer() {
        LatLng latLng;
        if (type == DataType.ONLY_SELECT_NEER && selectLaLng != null) {
            latLng = new LatLng(selectLaLng.longitude, selectLaLng.latitude);
        } else {
            latLng = LocationHelper.api().getLocation().getLatLng();
        }
        BDMapUtil.showMapPount(bdMapView, latLng);
        loadByNeer(latLng);
    }

    private void loadByNeer(LatLng latLng) {
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        if (mPoiSearch == null)
            mPoiSearch = PoiSearch.newInstance();
        option.keyword("公司");
        option.sortType(PoiSortType.distance_from_near_to_far);
        option.location(latLng);
        option.radius(200);
        option.pageNum(pagerNum < 0 ? 0 : pagerNum);
        option.pageCapacity(1000);
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        mPoiSearch.searchNearby(option);
    }

    private void loadSreachKey(String key) {
        if (mPoiSearch == null)
            mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        PoiCitySearchOption option = new PoiCitySearchOption();
        option.city(LocationHelper.api().getLocation().getCity());
        option.keyword(key);
        option.isReturnAddr(true);
        option.pageNum(0);
        option.pageCapacity(1000);
        mPoiSearch.searchInCity(option);
    }


    private void sort(List<BaseSelectModel<PoiInfo>> models) {
        if (Utils.isEmpty(models)) return;
        if (sortComparator == null) {
            sortComparator = new Comparator<BaseSelectModel<PoiInfo>>() {
                @Override
                public int compare(BaseSelectModel o1, BaseSelectModel o2) {
                    if (o1.getSort() > o2.getSort()) {
                        return 1;
                    } else if (o1.getSort() == o2.getSort()) {
                        return 0;
                    } else if (o1.getSort() < o2.getSort()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            };
        }
        Collections.sort(models, sortComparator);
    }


    /*当搜索百度地图获取到数据回调*/
    private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            LogUtil.i("onGetPoiResult");
            refreshView.stopRefresh();
            List<BaseSelectModel<PoiInfo>> models = new ArrayList<>();
            List<PoiInfo> pois = poiResult.getAllPoi();
            if (!Utils.isEmpty(pois)) {
                BaseSelectModel<PoiInfo> model = null;
                for (PoiInfo info : pois) {
                    float distance = BDMapUtil.workDistance(info.location, selectLaLng);
                    model = new BaseSelectModel<PoiInfo>(info.name, info.address, "距离" + distance + "米");
                    model.setData(info);
                    model.setSort(distance);
                    models.add(model);
                }
            }
            if (pagerNum > 1) {
                models.addAll(0, mAdapter.getModels());
            }
            sort(models);
            showModels(models);
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            LogUtil.i("onGetPoiDetailResult");

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            LogUtil.i("onGetPoiIndoorResult");

        }
    };


    /*当数据源变化时候显示变化*/
    private void showModels(List<BaseSelectModel<PoiInfo>> models) {
        hideProgress();
        if (mAdapter == null) {
            mAdapter = new BaseSelectAdapter.Builder<PoiInfo>(ct)
                    .setShowCb(true)
                    .setModels(models)
                    .setMulti(false)
                    .setOnItemClickListener(this)
                    .setAutoHeight(true)
                    .builder();
            listRV.setLayoutManager(new LinearLayoutManager(ct));
            listRV.setAdapter(mAdapter);
        } else {
            mAdapter.setModels(models);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void itemClick(BaseSelectModel<PoiInfo> model, int position) {
        this.selectModel = model;
        BDMapUtil.showMapPount(bdMapView, model.getData().location, true);
    }

    @Override
    public void onTextChanged(String s) {
        pagerNum = 1;
        loadData();
    }


    public enum DataType {
        ONLY_NEER,//只搜索附近
        ONLY_SELECT_NEER,//只搜索传递过来的地址的附近
        NEER_AND_SEACH;//先显示附近，在根据搜索进行搜索
    }
}
