package com.gxut.bitliker.usoftchinauu.presenter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.model.LatLng;
import com.gxut.code.network.request.Parameter;
import com.gxut.code.network.request.Tags;
import com.gxut.code.network.response.Failure;
import com.gxut.code.network.response.OnHttpCallback;
import com.gxut.code.network.response.Success;
import com.gxut.bitliker.usoftchinauu.config.AppConfig;
import com.gxut.bitliker.usoftchinauu.config.LocationHelper;
import com.gxut.bitliker.usoftchinauu.model.Locale;
import com.gxut.bitliker.usoftchinauu.model.User;
import com.gxut.bitliker.usoftchinauu.model.Work;
import com.gxut.bitliker.usoftchinauu.network.UrlHelper;
import com.gxut.bitliker.usoftchinauu.presenter.imp.IWork;
import com.gxut.bitliker.usoftchinauu.presenter.imp.IWorkView;
import com.gxut.bitliker.usoftchinauu.ui.activity.InputWorkActivity;
import com.gxut.bitliker.usoftchinauu.util.AlarmUtil;
import com.gxut.bitliker.usoftchinauu.util.BDMapUtil;
import com.gxut.code.baseutil.util.TimeUtil;
import com.gxut.code.baseutil.util.Utils;
import com.gxut.code.network.HttpClient;
import com.gxut.code.network.util.JSONUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;




/**
 * Created by Bitliker on 2017/6/26.
 */

public class WorkPresenter implements OnHttpCallback {
    private final int LOAD_WORK_DATA = 0x11;//获取班次
    private final int LOAD_WORK_LOG = 0x12;//获取打卡列表
    private final int LOAD_SAVE_LOG = 0x13;//签到
    private final int LOAD_WORK_SET = 0x14;//获取考勤设置
    private final int LOAD_WORK_CODE = 0x15;


    private final int LOAD_TIME = 0x01;
    private final int WORK_SET = 0x02;
    private final int WORK_DATA = 0x03;
    private final int SUBMIT_TIME = 0x04;
    private final int LOCALE = 0x05;
    private final int DISTANCE = 0x06;
    private final int AUTO = 0x07;

    private List<Locale> comLocales;
    private IWork iwork;

    public WorkPresenter(IWork iwork) {
        this.iwork = iwork;
    }


    public void loadData(long time) {
        loadWorkSet(time);
    }

    /**
     * 仅判断是否是自动打卡，地址为本地地址，提交时间为当前
     *
     * @param auto
     */
    public void submit(boolean auto) {
        submit(auto, 0, -1, null);
    }

    public void submit(boolean auto, long submitTime, float distance, Locale locale) {
        if (locale == null) {
            locale = LocationHelper.api().getLocation();
            if (locale == null) {
                LocationHelper.api().requestLocation();
                iwork.doResult(false, "没有获取到当前位置");
                return;
            }
        }
        float minDistance = -1;
        if (distance < 0 && !Utils.isEmpty(comLocales)) {
            LatLng myLocale = locale.getLatLng();
            for (Locale l : comLocales) {
                float dis = BDMapUtil.workDistance(myLocale, l.getLatLng());
                if (dis < locale.getSigninRange()) {
                    //符合打卡
                    distance = dis;
                    break;
                } else if (minDistance == -1 || minDistance > dis) {
                    minDistance = dis;
                }
            }
        }
        if (distance >= 0) {
            loadCode(auto, submitTime, locale, distance);
        } else {
            iwork.doResult(false, "不符合打卡距离");
            if (minDistance > 0) {
                LocationHelper.api().requestLocation();
                long nextTime = (long) ((minDistance / 100) * 60 * 1000);
                AlarmUtil.startAlarm(AlarmUtil.AUTOTASK_REQUESTCODE, AlarmUtil.AUTOTASK_ACTION, System.currentTimeMillis() + Math.max(1000, nextTime));
            }
        }
    }

    /**
     * 通过当前的地址信息判断与打卡位置的聚类
     *
     * @param myLocale
     * @return -1：comLocales为空 不符合打卡
     */
    private float accordWorkPlace(LatLng myLocale) {
        if (Utils.isEmpty(comLocales)) {
            return -1;
        }
        for (Locale locale : comLocales) {
            float dis = BDMapUtil.workDistance(myLocale, locale.getLatLng());
            if (dis < locale.getSigninRange()) {
                //符合打卡
                return dis;
            }
        }
        return -1;
    }

    /**
     * 获取打卡设置
     *
     * @param time 选择时间
     */
    private void loadWorkSet(long time) {
        if (time == 0) {
            time = System.currentTimeMillis();
        }
        showProgress();
        Parameter parameter = new Parameter.Builder()
                .mode(Parameter.GET)
                .url(UrlHelper.api().workSetUrl())
                .addParams("code", 1)
                .addTag(LOAD_TIME, time)
                .tag(LOAD_WORK_SET)
                .bulid();
        HttpClient.api().request(parameter, this);
    }

    /**
     * 获取班次数据
     *
     * @param time    选择时间点
     * @param workSet 班次设置
     */
    private void loadWorkData(long time, WorkSet workSet) {
        showProgress();
        Parameter parameter = new Parameter.Builder()
                .mode(Parameter.GET)
                .url(UrlHelper.api().workUrl())
                .addParams("date", TimeUtil.long2Str(time, "yyyyMMdd"))
                .addParams("emcode", AppConfig.api().getLoginUser().getEmCode())
                .tag(LOAD_WORK_DATA)
                .addTag(LOAD_TIME, time)
                .addTag(WORK_SET, workSet)
                .bulid();
        HttpClient.api().request(parameter, this);
    }

    /**
     * 获取打卡数据
     *
     * @param time  选择时间点
     * @param works 班次
     */
    private void loadWorkLog(long time, List<Work> works) {
        showProgress();
        if (time <= 0) {
            time = System.currentTimeMillis();
        }
        Parameter parameter = new Parameter.Builder()
                .mode(Parameter.GET)
                .url(UrlHelper.api().workLogUrl())
                .addParams("condition",
                        "cl_emcode='" + AppConfig.api().getLoginUser().getEmCode() +
                                "' and to_char(cl_time,'yyyy-MM-dd')='" + TimeUtil.long2Str(time, TimeUtil.YMD) + "'")
                .addParams("page", 1)
                .addParams("pageSize", 1000)
                .addParams("caller", "CardLog")
                .addParams("emcode", AppConfig.api().getLoginUser().getEmCode())
                .tag(LOAD_WORK_LOG)
                .addTag(WORK_DATA, works)
                .bulid();
        HttpClient.api().request(parameter, this);
    }


    private void loadCode(boolean auto, long submitTime, Locale locale, float distance) {
        Parameter parameter = new Parameter.Builder()
                .mode(Parameter.GET)
                .url(UrlHelper.api().commonCodeUrl())
                .addParams("type", 2)
                .addParams("caller", "CardLog")
                .tag(LOAD_WORK_CODE)
                .addTag(SUBMIT_TIME, submitTime)
                .addTag(LOCALE, locale)
                .addTag(DISTANCE, distance)
                .addTag(AUTO, auto)
                .bulid();
        HttpClient.api().request(parameter, this);
    }


    private void signining(String code, Tags tags) {
        boolean auto = false;
        float distance = 0f;
        long submitTime = 0;
        Locale locale = null;
        if (tags != null) {
            auto = (boolean) tags.get(AUTO);
            submitTime = (long) tags.get(SUBMIT_TIME);
            locale = (Locale) tags.get(LOCALE);
            distance = (float) tags.get(DISTANCE);
            if (distance < 0) return;
            if (locale == null) {
                locale = LocationHelper.api().getLocation();
            }
            if (locale == null) {
                return;
            }
        }
        Map<String, Object> formStore = new HashMap<>();
        User user = AppConfig.api().getLoginUser();
        formStore.put("cl_emname", user.getEmName());
        formStore.put("cl_emcode", user.getEmCode());
        formStore.put("cl_phone", user.getAccount());
        formStore.put("cl_code", code);
        if (submitTime > 0) {
            formStore.put("cl_time", TimeUtil.long2Str(submitTime, TimeUtil.YMD_HMS));
        }
        formStore.put("cl_distance", distance);
        if (auto) {
            formStore.put("cl_location", InputWorkActivity.AUTO_SIGNIN);
        } else {
            formStore.put("cl_location", locale.getName());
        }
        formStore.put("cl_address", locale.getAddress());
        Parameter parameter = new Parameter.Builder()
                .mode(Parameter.POST)
                .url(UrlHelper.api().saveCardlogUrl())
                .addParams("caller", "CardLog")
                .addParams("sessionUser", user.getEmCode())
                .addParams("master", user.getMaster())
                .addParams("sessionId", user.getSessionId())
                .addParams("formStore", JSONUtil.mapToJson(formStore))
                .tag(LOAD_SAVE_LOG).bulid();
        HttpClient.api().request(parameter, this);
    }


    @Override
    public void onSuccess(Success success) {
        int tag = (int) success.getTag();
        switch (tag) {
            case LOAD_WORK_SET:
                handlerWorkSet(success);
                break;
            case LOAD_WORK_DATA:
                handlerWorkData(success);
                break;
            case LOAD_WORK_LOG:
                handlerWorkLog(success);
                break;
            case LOAD_WORK_CODE:
                signining(JSONUtil.getText(success.getJsonObject(), "code"), success.getTags());
                break;
            case LOAD_SAVE_LOG:
                iwork.doResult(true, "打卡成功");
                break;
        }
    }

    @Override
    public void onFailure(Failure failure) {

    }

    private void handlerWorkSet(Success result) {
        long time = (long) result.getTag(LOAD_TIME);
        JSONObject o = result.getJsonObject();
        int nonclass = JSONUtil.getInt(o, "nonclass");
        int earlyoff = JSONUtil.getInt(o, "earlyoff");
        int overlatetime = JSONUtil.getInt(o, "overlatetime");
        int latetime = JSONUtil.getInt(o, "latetime");
        loadWorkData(time, new WorkSet(latetime, overlatetime, earlyoff, nonclass));
    }

    private void handlerWorkData(final Success result) {
        Observable.create(new ObservableOnSubscribe<List<Work>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Work>> e) throws Exception {
                JSONObject object = result.getJsonObject();
                if (object == null) return;
                WorkSet workSet = (WorkSet) result.getTag(WORK_SET);
                if (workSet == null) workSet = new WorkSet();
                List<Work> works = new ArrayList<>();
                int earlytime = JSONUtil.getInt(object, "wd_earlytime");
                String[] keySet = {"Class1", "Class2", "Class3"};
                Work work = null;
                for (String key : keySet) {
                    work = getWorkByKey(object, key);
                    if (work == null) continue;
                    //上班时间到矿工时间
                    work.setWorkEnd(TimeUtil.long2Str(TimeUtil.str2Long(work.getWorkTime(), TimeUtil.HM) + workSet.nonclass * 1000 * 60, TimeUtil.HM));
                    //矿工时间到下班时间
                    work.setOffStart(TimeUtil.long2Str(TimeUtil.str2Long(work.getOffTime(), TimeUtil.HM) - workSet.nonclass * 1000 * 60, TimeUtil.HM));
                    works.add(work);
                }
                handlerWorkLocation(result.getJSONArray("comAddressdata"));
                if (!Utils.isEmpty(works)) {
                    arrangeWork(works, earlytime);
                    e.onNext(works);
                }

            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Work>>() {
                    @Override
                    public void accept(@NonNull List<Work> works) throws Exception {
                        loadWorkLog((long) result.getTag(LOAD_TIME), works);
                    }
                });
    }

    private static void arrangeWork(List<Work> works, int earlytime) {
        String start = null;
        String end = null;
        for (int i = 0; i < works.size(); i++) {
            if (i == 0) {
                start = TimeUtil.long2Str(TimeUtil.str2Long(works.get(i).getWorkTime(), TimeUtil.HM) - earlytime * 60 * 1000 * 60, TimeUtil.HM);
            } else {
                //判断两个的中间
                long lastEndTime =TimeUtil. str2Long(works.get(i - 1).getOffTime(), TimeUtil.HM);
                long workTime = TimeUtil.str2Long(works.get(i).getWorkTime(), TimeUtil.HM);
                start = TimeUtil.long2Str((lastEndTime + workTime) / 2, TimeUtil.HM);
            }
            if (works.size() > (i + 1)) {
                long timeNew = TimeUtil.str2Long(works.get(i).getOffTime(), TimeUtil.HM);
                long timeNext =TimeUtil. str2Long(works.get(i + 1).getWorkTime(), TimeUtil.HM);
                end = TimeUtil.long2Str((timeNew + timeNext) / 2, TimeUtil.HM);
            } else {//最后一个
                end = "24:00";
            }
            if (Utils.isEmpty(works.get(i).getWorkStart())) works.get(i).setWorkStart(start);
            if (Utils.isEmpty(works.get(i).getOffEnd())) works.get(i).setOffEnd(end);
        }
    }

    private Work getWorkByKey(JSONObject object, String key) {
        int id = Utils.getInt(key);
        JSONObject o = JSONUtil.getJSONObject(object, key);
        String wd_onbeg = JSONUtil.getText(o, "wd_onbeg");
        String wd_onduty = JSONUtil.getText(o, "wd_onduty");
        String wd_offduty = JSONUtil.getText(o, "wd_offduty");
        String wd_offend = JSONUtil.getText(o, "wd_offend");
        if (Utils.isEmpty(wd_onduty) || Utils.isEmpty(wd_offduty)) return null;
        Work work = new Work();
        work.init(wd_onbeg, wd_onduty, wd_offduty, wd_offend);
        work.setId(id);
        return work;
    }


    private void handlerWorkLocation(final JSONArray array) {
        Observable.create(new ObservableOnSubscribe<Locale>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Locale> e) throws Exception {
                JSONObject o = null;
                Locale location = null;
                comLocales = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    o = array.getJSONObject(i);
                    location = new Locale();
                    int id = JSONUtil.getInt(o, "cS_ID", "CS_ID");
                    int workRange = JSONUtil.getInt(o, "cS_INNERDISTANCE", "CS_INNERDISTANCE");//办公距离
                    double latitude = JSONUtil.getDouble(o, "cS_LATITUDE", "CS_LATITUDE");//精度
                    double longitude = JSONUtil.getDouble(o, "cS_LONGITUDE", "CS_LONGITUDE");//纬度
                    LatLng latLng = new LatLng(latitude, longitude);
                    int signinRange = JSONUtil.getInt(o, "cS_VALIDRANGE", "CS_VALIDRANGE");//打卡范围
                    String position = JSONUtil.getText(o, "sHORTNAME", "CS_SHORTNAME");//位置
                    String address = JSONUtil.getText(o, "cS_WORKADDR", "CS_WORKADDR");//地址
                    location.setName(position);
                    location.setAddress(address);
                    location.setLatLng(latLng);
                    location.setCode(id);
                    location.setSigninRange(signinRange);
                    location.setWorkRange(workRange);
                    comLocales.add(location);
                }
                float distance = -1;
                Locale neerLocale = null;
                if (!Utils.isEmpty(comLocales)) {
                    for (Locale l : comLocales) {
                        float dis = BDMapUtil.workDistance(l.getLatLng());
                        if (distance < 0 || dis < distance) {
                            neerLocale = l;
                        }
                    }
                    e.onNext(neerLocale);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Locale>() {
                    @Override
                    public void accept(@NonNull Locale locale) throws Exception {
                        neerLocale(locale);
                    }
                });
    }

    private void handlerWorkLog(final Success result) {
        Observable.create(new ObservableOnSubscribe<List<Work>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Work>> e) throws Exception {
                JSONArray listdata = result.getJSONArray("listdata");
                List<Work> works = (List<Work>) result.getTag(WORK_DATA);
                if (Utils.isEmpty(works)) return;
                if (!Utils.isEmpty(listdata)) {
                    String[] logs = new String[listdata.size()];
                    for (int i = 0; i < listdata.size(); i++)
                        logs[i] = TimeUtil.long2Str(TimeUtil.str2Long(JSONUtil.getText(listdata.getJSONObject(i), "cl_time"), TimeUtil.YMD_HMS), TimeUtil.HM);
                    Arrays.sort(logs);
                    for (int i = 0; i < logs.length; i++) {
                        String timeLog = logs[i];
                        for (int j = 0; j < works.size(); j++) {
                            Work work = works.get(j);
                            if (work.isEmpty()) continue;
                            if (enoughWork(work, timeLog)) {//属于上班打卡
                                works.get(j).setWorkSignin(timeLog);
                                break;
                            } else if (enoughOff(work, timeLog)) {//属于下班打卡
                                works.get(j).setOffSignin(timeLog);
                            }
                        }
                    }
                }
                e.onNext(works);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Work>>() {
                    @Override
                    public void accept(@NonNull List<Work> works) throws Exception {
                        iwork.showModel(works);
                    }
                });
    }


    private static boolean enoughWork(Work e, String time) {
        if (!Utils.isEmpty(e.getWorkSignin())) return false;
        if (time.compareTo(e.getWorkStart()) >= 0 && time.compareTo(e.getWorkEnd()) <= 0 && time.compareTo(e.getOffTime()) < 0)
            return true;//小于上班时间
        return false;
    }

    private static boolean enoughOff(Work e, String time) {
        if (time.compareTo(e.getOffStart()) >= 0
                && time.compareTo(e.getOffEnd()) <= 0
                && time.compareTo(e.getWorkTime()) > 0
                && (Utils.isEmpty(e.getOffSignin()) || time.compareTo(e.getOffSignin()) > 0))
            return true;
        return false;
    }

    public void showProgress() {
        if (iwork != null && iwork instanceof IWorkView) {
            ((IWorkView) iwork).showProgress();
        }
    }

    public void hideProgress() {
        if (iwork != null && iwork instanceof IWorkView) {
            ((IWorkView) iwork).hideProgress();
        }
    }

    public void neerLocale(Locale neerLocale) {
        if (neerLocale != null && iwork != null && iwork instanceof IWorkView) {
            ((IWorkView) iwork).neerLocale(neerLocale);
        }
    }


    private class WorkSet {
        private int latetime = 0;//允许迟到时间 m
        private int overlatetime = 30;//严重迟到时间 m（时间大于这个时候为严重迟到，在允许时间到严重迟到时间之前是迟到）
        private int earlyoff = 0;//允许早退时间 m
        private int nonclass = 60;//旷工时间 m

        public WorkSet(int latetime, int overlatetime, int earlyoff, int nonclass) {
            this.latetime = latetime;
            this.overlatetime = overlatetime;
            this.earlyoff = earlyoff;
            this.nonclass = nonclass;
        }

        public WorkSet() {
        }
    }


}
