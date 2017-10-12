package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.modular.poplibrary.pickerview.TimePickerView;
import com.gxut.code.baseutil.ui.base.BaseActivity;
import com.gxut.code.baseutil.util.TimeUtil;
import com.gxut.code.baseutil.util.Utils;
import com.modular.poplibrary.pickerview.PickerBuilder;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.config.LocationHelper;
import com.gxut.bitliker.usoftchinauu.model.BaseSelectModel;
import com.gxut.bitliker.usoftchinauu.model.Locale;
import com.gxut.bitliker.usoftchinauu.model.User;
import com.gxut.bitliker.usoftchinauu.model.Work;
import com.gxut.bitliker.usoftchinauu.presenter.WorkPresenter;
import com.gxut.bitliker.usoftchinauu.presenter.imp.IWork;
import com.gxut.bitliker.usoftchinauu.ui.widget.SwitchView;
import com.gxut.bitliker.usoftchinauu.util.BDMapUtil;
import com.gxut.bitliker.usoftchinauu.util.SelectPopupBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class InputWorkActivity extends BaseActivity implements IWork {

    private final int LOCATION_REQUEST = 0x16;
    @BindView(R.id.locationTV)
    TextView locationTV;
    @BindView(R.id.addressTV)
    TextView addressTV;
    @BindView(R.id.typeTV)
    TextView typeTV;
    @BindView(R.id.distanceTV)
    TextView distanceTV;
    @BindView(R.id.timeTV)
    TextView timeTV;
    @BindView(R.id.timeAbleSv)
    SwitchView timeAbleSv;
    @BindView(R.id.timeRl)
    RelativeLayout timeRl;

    private final String OTHER_SIGNIN = "其他方式";
    public static final String AUTO_SIGNIN = "android 自动打卡记录";
    private Locale selectLocation;//选择最近的打卡位置
    private Locale newLocation;
    private boolean finished = false;

    @OnClick({R.id.locationTV, R.id.typeTV, R.id.timeTV})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.locationTV:
                selectLacation();
                break;
            case R.id.typeTV:
                selectType();
                break;
            case R.id.timeTV:
                showTimeSelect();
                break;
        }
    }

    private void selectLacation() {
        startActivityForResult(new Intent(ct, SelectMapActivity.class)
                        .putExtra(Utils.DEF_RESULT_MODEL, selectLocation.getLatLng())
                        .putExtra("type", SelectMapActivity.DataType.ONLY_NEER)
                , LOCATION_REQUEST);
    }

    private void selectType() {
        List<BaseSelectModel<Parcelable>> datas = new ArrayList<>();
        datas.add(new BaseSelectModel(OTHER_SIGNIN));
        datas.add(new BaseSelectModel(AUTO_SIGNIN));
        new SelectPopupBuilder(ct)
                .setTitle("选择方式")
                .setOnSelectListener(new SelectPopupBuilder.OnRadioSelectListener<User>() {
                    @Override
                    public void callBack(BaseSelectModel<User> data) {
                        typeTV.setText(data.getName());
                    }
                })
                .setDatas(datas)
                .builder();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_push, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuPush) {
            submit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Utils.DEF_RESULT_CODE && data != null) {
            switch (requestCode) {
                case LOCATION_REQUEST:
                    PoiInfo info = data.getParcelableExtra(Utils.DEF_RESULT_MODEL);
                    if (info != null) {
                        locationTV.setText(info.name);
                        addressTV.setText(info.address);
                        if (newLocation == null) {
                            newLocation = new Locale();
                        }
                        newLocation.setName(info.name);
                        newLocation.setAddress(info.address);
                        newLocation.setLatLng(info.location);
                        shortestDistance(newLocation.getLatLng());
                    }
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        finished = true;
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_work);
        ButterKnife.bind(this);
        initView();
        initEnevt();
    }

    private void initEnevt() {
        timeAbleSv.setOnCheckedChangeListener(new SwitchView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean isChecked) {
                timeRl.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                while (true) {
                    Thread.sleep(1000);
                    if (finished) {
                        e.onComplete();
                    } else {
                        e.onNext(TimeUtil.long2Str(System.currentTimeMillis(), TimeUtil.YMD_HMS));
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        timeTV.setText(s == null ? "" : s);
                    }
                });
    }

    private void initView() {
        timeAbleSv.setChecked(false);
        if (getIntent() != null) {
            selectLocation = getIntent().getParcelableExtra("location");
        }
        Locale location = LocationHelper.api().getLocation();
        if (location != null) {
            locationTV.setText(location.getName());
            addressTV.setText(location.getAddress());
            typeTV.setText(AUTO_SIGNIN);
            timeTV.setText(TimeUtil.long2Str(System.currentTimeMillis(), TimeUtil.YMD_HMS));
            shortestDistance();
        }
    }

    private void showTimeSelect() {
        Calendar endDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2017, 6, 1);
        endDate.set(2018, 1, 1);
        new PickerBuilder(ct)
                .setRang(startDate, endDate)
                .setType(PickerBuilder.Type.YMD_HMS)
                .show(new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        finished = true;
                        timeTV.setText(TimeUtil.date2Str(date, TimeUtil.YMD_HMS));
                    }
                });
    }

    private void shortestDistance() {
        if (selectLocation == null) return;
        float distance = BDMapUtil.workDistance(selectLocation.getLatLng());
        distanceTV.setText(String.valueOf(distance));
    }

    private void shortestDistance(LatLng seletcLaLng) {
        if (selectLocation == null) return;
        float distance = BDMapUtil.workDistance(seletcLaLng, selectLocation.getLatLng());
        distanceTV.setText(String.valueOf(distance));
    }

    private void submit() {
        boolean auto = typeTV.getText().equals(AUTO_SIGNIN);
        long submitTime = 0;
        if (timeAbleSv.isChecked() && !TextUtils.isEmpty(timeTV.getText())) {
            String time = timeTV.getText().toString();
            submitTime = TimeUtil.str2Long(time, TimeUtil.YMD_HMS);
        }
        try {
            float distance = Float.valueOf(Utils.getText(distanceTV));
            new WorkPresenter(this).submit(auto, submitTime, distance, newLocation);
        } catch (Exception e) {
        }
    }


    @Override
    public void showModel(List<Work> works) {

    }

    @Override
    public void doResult(boolean ok, String message) {
        showMessage(message);
        if (ok)
            finish();
    }
}
