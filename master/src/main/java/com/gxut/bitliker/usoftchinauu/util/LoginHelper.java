package com.gxut.bitliker.usoftchinauu.util;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gxut.bitliker.usoftchinauu.config.AppConfig;
import com.gxut.bitliker.usoftchinauu.config.SettingSp;
import com.gxut.bitliker.usoftchinauu.db.dao.UserDao;
import com.gxut.bitliker.usoftchinauu.model.BaseSelectModel;
import com.gxut.bitliker.usoftchinauu.model.User;
import com.gxut.bitliker.usoftchinauu.network.UrlHelper;
import com.gxut.bitliker.usoftchinauu.ui.activity.MainActivity;
import com.gxut.bitliker.usoftchinauu.util.hmac.HmacUtils;
import com.gxut.code.baseutil.ui.base.BaseActivity;
import com.gxut.code.baseutil.util.LogUtil;
import com.gxut.code.baseutil.util.Utils;
import com.gxut.code.network.HttpClient;
import com.gxut.code.network.request.Parameter;
import com.gxut.code.network.response.Failure;
import com.gxut.code.network.response.OnHttpCallback;
import com.gxut.code.network.response.Success;
import com.gxut.code.network.util.JSONUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
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
 * Created by Bitliker on 2017/6/21.
 */

public class LoginHelper implements OnHttpCallback {
    private final int MASTERS_URL = 0x14;
    private final int LOGIN_URL = 0x13;
    private final int ACCOUNT_URL = 0x12;
    private BaseActivity ct;
    private final int PASSWORD = 0x1;
    private final int ACCOUNT = 0x2;
    private final int USER = 0x3;
    private final int SHOWSELECT = 0x4;
    public static final String SELECT_COMPANY = "选择公司";
    public static final String SELECT_MASTER = "选择账套";

    public LoginHelper(BaseActivity ct) {
        this.ct = ct;
    }

    public void account() {
        String account = SettingSp.api().getString(SettingSp.ACCOUNT);
        String password = SettingSp.api().getString(SettingSp.PASSWORD);
        try {
            account(account, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void account(String account, String password) throws Exception {
        if (Utils.isEmpty(account) || Utils.isEmpty(account)) return;
        AppConfig.api().setLoginUser(new User());
        ct.showProgress();
        Map<String, Object> params = new HashMap<>();
        params.put("user", account);
        params.put("password", password);
        StringBuilder urlBuilder = new StringBuilder(mosaicUrl(UrlHelper.api().accountUrl(), params));
        urlBuilder.append("&_timestamp=").append(System.currentTimeMillis());
        String hmac = urlBuilder.toString();
        urlBuilder.append("&_signature=").append(HmacUtils.encode(hmac));
        Parameter parameter = new Parameter.Builder()
                .mode(Parameter.GET)
                .url(urlBuilder.toString())
                .tag(ACCOUNT_URL)
                .addTag(PASSWORD, password)
                .addTag(ACCOUNT, account)
                .bulid();
        HttpClient.api().request(parameter, this);
    }

    public void loginUser(User user) {
        if (user == null || user.isEmpty()) return;
        ct.showProgress();
        AppConfig.api().setLoginUser(user);
        Parameter parameter = new Parameter.Builder()
                .url(UrlHelper.api().loginUrl())
                .tag(LOGIN_URL)
                .addTag(USER, user)
                .addParams("username", user.getAccount())
                .addParams("password", user.getPassword())
                .addParams("master", user.getMaster())
                .mode(Parameter.POST).bulid();
        HttpClient.api().request(parameter, this);
    }

    public void getAllMasters(User user, boolean showSelect) {
        if (user == null || user.isEmpty()) {
            return;
        }
        AppConfig.api().setLoginUser(user);
        ct.showProgress();
        Parameter parameter = new Parameter.Builder()
                .url(UrlHelper.api().mastersUrl())
                .tag(MASTERS_URL)
                .addTag(SHOWSELECT, showSelect)
                .addTag(USER, user)
                .mode(Parameter.GET).bulid();
        HttpClient.api().request(parameter, this);
    }


    private void HandleManage(final Success result) {
        Observable.create(new ObservableOnSubscribe<List<User>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<User>> e) throws Exception {
                JSONArray array = JSON.parseArray(result.getMessage());
                if (Utils.isEmpty(array)) return;
                JSONObject object = null;
                User user = null;
                String name = "";
                List<User> users = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    object = array.getJSONObject(i);
                    if ("B2B".equals(JSONUtil.getText(object, "platform"))) {
                        name = JSONUtil.getText(object, "userName");
                    } else if ("ERP".equals(JSONUtil.getText(object, "platform"))) {
                        user = new User();
                        user.setBaseUrl(JSONUtil.getText(object, "website"));
                        user.setMaster(JSONUtil.getText(object, "master"));
                        user.setEmCode(JSONUtil.getText(object, "account"));
                        user.setCompany(JSONUtil.getText(object, "name"));
                        user.setId(JSONUtil.getInt(object, "enuu"));
                        if (user.getId() == 0) {
                            user.setId(JSONUtil.getInt(object, "masterId"));
                        }
                        user.setEmName(name);
                        user.setAccount((String) result.getTag(ACCOUNT));
                        user.setPassword((String) result.getTag(PASSWORD));
                        users.add(user);
                    }
                }
                if (!Utils.isEmpty(users)) {
                    UserDao.api().saveUser(users);
                    e.onNext(users);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(@NonNull List<User> users) throws Exception {
                        showCompanySelectDialog(users, SELECT_COMPANY);
                    }
                });

    }

    private void handlerMaster2Show(final Success success) {
        Observable.create(new ObservableOnSubscribe<List<User>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<User>> e) throws Exception {
                if (success == null || !success.isJSON()) {
                    e.onNext(new ArrayList<User>());
                    return;
                }
                JSONArray array = success.getJSONArray("masters");
                if (Utils.isEmpty(array)) {
                    e.onNext(new ArrayList<User>());
                    return;
                }
                User user = (User) success.getTag(USER);
                List<User> users = new ArrayList<>();
                User u = null;
                JSONObject o;
                for (int i = 0; i < array.size(); i++) {
                    o = array.getJSONObject(i);
                    String masterName = JSONUtil.getText(o, "ma_function");
                    String master = JSONUtil.getText(o, "ma_name");
                    String baseUrl = JSONUtil.getText(o, "b2CUrl");
                    u = new User(user);
                    u.setMasterName(masterName);
                    u.setMaster(master);
                    u.setBaseUrl(baseUrl);
                    if (!u.isEmpty())
                        users.add(u);
                }
                e.onNext(users);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(@NonNull List<User> masters) throws Exception {
                        showCompanySelectDialog(masters, SELECT_MASTER);
                    }
                });
    }

    private void HandleMasters(final Success result) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                if (result == null || !result.isJSON()) {
                    e.onNext(false);
                    return;
                }
                JSONArray array = result.getJSONArray("masters");
                if (Utils.isEmpty(array)) {
                    e.onNext(false);
                    return;
                }
                User user = (User) result.getTag(USER);
                JSONObject o;
                for (int i = 0; i < array.size(); i++) {
                    o = array.getJSONObject(i);
                    String masterName = JSONUtil.getText(o, "ma_function");
                    String master = JSONUtil.getText(o, "ma_name");
                    if (!Utils.isEmpty(master) && master.equals(user.getMaster())) {
                        user.setMasterName(masterName);
                        AppConfig.api().setLoginUser(user);
                    }
                }
                e.onNext(UserDao.api().updataUser(user));
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean saveOk) throws Exception {
                        turn2Main();
                    }
                });
    }

    private void HandleLogin(final Success success) {
        final JSONObject object = success.getJsonObject();
        if (JSONUtil.getBoolean(object, "success")) {
            Observable.create(new ObservableOnSubscribe<User>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<User> e) throws Exception {
                    if (object == null) return;
                    User user = null;
                    if (JSONUtil.getBoolean(object, "success")) {
                        user = (User) success.getTag(USER);
                        String sessionId = JSONUtil.getText(object, "sessionId");
                        String emName = JSONUtil.getText(object, "emname");
                        String emCode = JSONUtil.getText(object, "erpaccount");
                        user.setEmName(emName);
                        user.setEmCode(emCode);
                        user.setSessionId(sessionId);
                        UserDao.api().updataUser(user);
                        SettingSp.api().put(SettingSp.MASTER, user.getMaster());
                    }
                    e.onNext(user == null ? new User() : user);
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<User>() {
                        @Override
                        public void accept(@NonNull User user) throws Exception {
                            AppConfig.api().setLoginUser(user);
                            getAllMasters(user, false);
                        }
                    });
        } else {
            String reason = JSONUtil.getText(object, "reason");
            ct.showMessage(reason);
        }
    }

    private void turn2Main() {
        ct.startActivity(new Intent(ct, MainActivity.class));
        if (!(ct instanceof MainActivity))
            ct.finish();
    }


    public void showCompanySelectDialog(final List<User> users, String title) {
        if (Utils.isEmpty(users)) return;
        List<BaseSelectModel<User>> datas = new ArrayList<BaseSelectModel<User>>();
        for (User u : users) {
            BaseSelectModel<User> model = new BaseSelectModel<User>(SELECT_COMPANY.equals(title) ? u.getCompany() : u.getMasterName());
            model.setData(u);
            datas.add(model);
        }
        new SelectPopupBuilder(ct)
                .setTitle(title)
                .setOnSelectListener(new SelectPopupBuilder.OnRadioSelectListener<User>() {
                    @Override
                    public void callBack(BaseSelectModel<User> data) {
                        if (data != null && !Utils.isEmpty(data.getName())) {
                            User user = data.getData();
                            loginUser(user);
                        }
                    }
                })
                .setDatas(datas)
                .builder();
    }

    public final String mosaicUrl(String url, Map<String, Object> param) throws Exception {
        if (Utils.isEmpty(url))
            return "";
        StringBuilder urlBuilder = new StringBuilder(url);
        if (param == null || param.isEmpty()) {
            return urlBuilder.toString();
        }
        urlBuilder.append("?");
        for (Map.Entry<String, Object> e : param.entrySet()) {
            if (e.getValue() == null || Utils.isEmpty(e.getKey()))
                continue;
            urlBuilder.append(String.format("%s=%s", e.getKey(), URLEncoder.encode(e.getValue().toString(), "utf-8")));
            urlBuilder.append("&");
        }
        if (urlBuilder.length() > 1)
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        return urlBuilder.toString();
    }

    @Override
    public void onSuccess(Success success) {
        int tag = (int) success.getTag();
        int httpCode = success.getCode();
        LogUtil.i("httpCode=" + httpCode);
        ct.hideProgress();
        if (!success.isJSON()) return;
        if (ACCOUNT_URL == tag)
            HandleManage(success);
        else if (LOGIN_URL == tag)
            HandleLogin(success);
        else if (MASTERS_URL == tag) {
            if ((boolean) success.getTag(SHOWSELECT))
                handlerMaster2Show(success);
            else
                HandleMasters(success);
        }
    }

    @Override
    public void onFailure(Failure failure) {

    }
}
