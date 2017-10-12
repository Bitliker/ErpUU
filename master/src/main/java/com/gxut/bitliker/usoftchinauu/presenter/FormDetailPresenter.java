package com.gxut.bitliker.usoftchinauu.presenter;

import android.content.Intent;
import android.os.Parcelable;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gxut.bitliker.usoftchinauu.model.BaseSelectModel;
import com.gxut.bitliker.usoftchinauu.model.FormDetail;
import com.gxut.bitliker.usoftchinauu.network.UrlHelper;
import com.gxut.bitliker.usoftchinauu.presenter.imp.IFormDetail;
import com.gxut.bitliker.usoftchinauu.ui.activity.ListFormDetailActivity;
import com.gxut.bitliker.usoftchinauu.util.SelectPopupBuilder;
import com.gxut.code.baseutil.ui.base.BaseActivity;
import com.gxut.code.baseutil.util.LogUtil;
import com.gxut.code.baseutil.util.Utils;
import com.gxut.code.network.HttpClient;
import com.gxut.code.network.request.Parameter;
import com.gxut.code.network.response.Failure;
import com.gxut.code.network.response.OnHttpCallback;
import com.gxut.code.network.response.Success;
import com.gxut.code.network.util.JSONUtil;

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
 * Created by Bitliker on 2017/8/29.
 */

public class FormDetailPresenter implements OnHttpCallback {
    private final int TAG_DELETE = 2;
    private final int LOAD_FORM_DETAIL = 11;
    private final int SUBMIT_SAVE = 12;
    private final int LOAD_MULTI_NODE = 13;
    private final int SUBMIT_OVER_TASK = 14;
    private final int SUBMIT_UNSUBMIT = 15;
    private final int SUBMIT_DELETE = 16;
    private IFormDetail iFormDetail;
    private String caller;
    private int id;
    private boolean inputing;
    private BaseActivity ct;

    public FormDetailPresenter(IFormDetail iFormDetail, BaseActivity ct, boolean inputing, Intent intent) {
        this.iFormDetail = iFormDetail;
        this.inputing = inputing;
        this.ct = ct;
        String title = intent.getStringExtra("title");
        caller = intent.getStringExtra("caller");
        id = intent.getIntExtra("id", 0);
        if (Utils.isEmpty(caller)) {
            this.iFormDetail.finish();
            return;
        }
        if (!Utils.isEmpty(title)) {
            iFormDetail.setTitle(title);
        }
        loadFormDetail();
    }

    public String getCaller() {
        return caller;
    }

    private void loadFormDetail() {
        iFormDetail.showProgress();
        String url = inputing ? UrlHelper.api().getformandgriddetailUrl(id) : UrlHelper.api().getformandgriddataUrl();
        HttpClient.api().request(new Parameter.Builder()
                .url(url)
                .addParams("condition", "1=1")
                .addParams("caller", caller)
                .addParams("id", id)
                .mode(Parameter.GET)
                .record(LOAD_FORM_DETAIL)
                .bulid(), this);
    }

    public void submit(List<FormDetail> formDetails) {
        iFormDetail.showProgress();
        Map<String, Object> gridStore = new HashMap<>();
        Map<String, Object> formStore = new HashMap<>();
        if (!Utils.isEmpty(formDetails)) {
            for (FormDetail e : formDetails) {
                if (!Utils.isEmpty(e.getValuesKey()) && !Utils.isEmpty(e.getPutValues())) {
                    if (e.getGroupId() == 0) {
                        formStore.put(e.getValuesKey(), e.getPutValues());
                    } else {
                        gridStore.put(e.getValuesKey(), e.getPutValues());
                    }
                } else if (!e.isTAG()) {
                    iFormDetail.showMessage(e.getCaption() + "字段没有填写");
                    return;
                }
            }
        }
        String url = id == 0 ? UrlHelper.api().commonSaveAndSubmitUrl(caller) : UrlHelper.api().commonUpdateUrl();
        Parameter.Builder builder = new Parameter.Builder();
        builder.url(url)
                .addParams("gridStore", gridStore.isEmpty() ? "{}" : JSONUtil.mapToJson(gridStore))
                .addParams("caller", caller)
                .addParams("formStore", formStore.isEmpty() ? "{}" : JSONUtil.mapToJson(formStore))
                .mode(Parameter.POST)
                .record(SUBMIT_SAVE);
        if (id > 0) {
            builder.addParams("keyid", id);
        }
        HttpClient.api().request(builder.bulid(), this);
    }

    private void loadMultiNode(int id) {
        if (id <= 0) {
            iFormDetail.showMessage("提交成功");
            end2List();
            return;
        }
        iFormDetail.showProgress();
        HttpClient.api().request(new Parameter.Builder()
                .url(UrlHelper.api().getMultiNodeUrl())
                .addParams("caller", caller)
                .addParams("id", id)
                .mode(Parameter.GET)
                .record(LOAD_MULTI_NODE)
                .bulid(), this);
    }

    private void submitTakeOverTask(int nodeId, String emCode) {
        iFormDetail.showProgress();
        Map<String, Object> params = new HashMap<>();
        params.put("nodeId", nodeId);
        params.put("em_code", emCode);
        HttpClient.api().request(new Parameter.Builder()
                .url(UrlHelper.api().takeOverTaskUrl())
                .addParams("params", JSONUtil.mapToJson(params))
                .addParams("_noc", 1)
                .mode(Parameter.POST)
                .record(SUBMIT_OVER_TASK)
                .bulid(), this);
    }


    public void unsubmit(boolean isDelete) {
        iFormDetail.showProgress();
        HttpClient.api().request(new Parameter.Builder()
                .url(UrlHelper.api().commonresUrl(caller))
                .addParams("caller", caller)
                .addParams("id", id)
                .addTag(TAG_DELETE, isDelete)
                .mode(Parameter.POST)
                .record(SUBMIT_UNSUBMIT)
                .bulid(), this);
    }

    private void delete() {
        iFormDetail.showProgress();
        LogUtil.i("delete()" + caller + "+id=" + id);
        HttpClient.api().request(new Parameter.Builder()
                .url(UrlHelper.api().commondeleteUrl(caller))
                .addParams("caller", caller)
                .addParams("id", id)
                .mode(Parameter.POST)
                .record(SUBMIT_DELETE)
                .bulid(), this);
    }

    @Override
    public void onSuccess(Success success) {
        switch (success.getRecord()) {
            case LOAD_FORM_DETAIL:
                handleFormDetail(success.getJsonObject("data", "datas"));
                break;
            case SUBMIT_SAVE:
                loadMultiNode(JSONUtil.getInt(success.getJsonObject(), "fp_id", "va_id", "wod_id"));
                break;
            case LOAD_MULTI_NODE:
                handMultiNode(success.getJSONArray("assigns"));
                break;
            case SUBMIT_OVER_TASK:
                end2List();
                break;
            case SUBMIT_UNSUBMIT:
                if ((Boolean) success.getTag(TAG_DELETE)) {
                    delete();
                } else {
                    inputing = true;
                    loadFormDetail();
                }
                break;
            case SUBMIT_DELETE:
                iFormDetail.showMessage("删除成功");
                ct.setResult(20);
                ct.finish();
                break;
        }
    }

    @Override
    public void onFailure(Failure failure) {
        iFormDetail.hideProgress();
        iFormDetail.showMessage(failure.getMessage());
    }

    private void handMultiNode(JSONArray assigns) {
        if (Utils.getSize(assigns) > 0) {
            JSONObject data = assigns.getJSONObject(0);
            if (data != null) {
                int nodeid = JSONUtil.getInt(data, "JP_NODEID");
                JSONArray candidates = JSONUtil.getJSONArray(data, "JP_CANDIDATES");
                if (Utils.isEmpty(candidates)) {
                    end2List();
                } else {
                    List<BaseSelectModel<Parcelable>> datas = new ArrayList<>();
                    for (int i = 0; i < candidates.size(); i++) {
                        String name = candidates.getString(i);
                        if (Utils.isEmpty(name)) continue;
                        datas.add(new BaseSelectModel<Parcelable>(name));
                    }
                    if (!Utils.isEmpty(datas)) {
                        showSelect(nodeid, datas);
                    } else {
                        end2List();
                    }
                }

            } else {
                end2List();
            }
        } else {
            end2List();
        }
    }

    private void handleFormDetail(final JSONObject data) {
        Observable.create(new ObservableOnSubscribe<List<FormDetail>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<FormDetail>> e) throws Exception {
                List<FormDetail> showFormDetails = new ArrayList<>();
                if (data.containsKey("formdetail") || data.containsKey("gridetail")) {
                    JSONArray formdetails = JSONUtil.getJSONArray(data, "formdetail");
                    JSONArray gridetails = JSONUtil.getJSONArray(data, "gridetail");
                    List<FormDetail> details = getFormDetailFromDetail(false, formdetails);
                    if (!Utils.isEmpty(details)) {
                        details.add(0, new FormDetail(false));
                        showFormDetails.addAll(details);
                    }
                    details = getFormDetailFromDetail(true, gridetails);
                    if (!Utils.isEmpty(details)) {
                        details.add(0, new FormDetail(true));
                        showFormDetails.addAll(details);
                    }
                } else {
                    JSONArray formconfigs = JSONUtil.getJSONArray(data, "formconfigs");
                    JSONArray formdatas = JSONUtil.getJSONArray(data, "formdata");
                    JSONArray gridconfigs = JSONUtil.getJSONArray(data, "gridconfigs");
                    JSONArray griddatas = JSONUtil.getJSONArray(data, "griddata");
                    List<FormDetail> details = null;
                    if (Utils.getSize(formdatas) > 0) {
                        details = getFormDetailFromConfig(false, formdatas.getJSONObject(0), formconfigs);
                        if (!Utils.isEmpty(details)) {
                            details.add(0, new FormDetail(false));
                            showFormDetails.addAll(details);
                        }
                    }
                    if (!Utils.isEmpty(griddatas) && !Utils.isEmpty(gridconfigs)) {
                        for (int i = 0; i < griddatas.size(); i++) {
                            details = getFormDetailFromConfig(true, griddatas.getJSONObject(i), gridconfigs);
                            if (!Utils.isEmpty(details)) {
                                details.add(0, new FormDetail(i));
                                showFormDetails.addAll(details);
                            }
                        }
                    }
                }
                e.onNext(showFormDetails);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<FormDetail>>() {
                    @Override
                    public void accept(@NonNull List<FormDetail> formDetails) throws Exception {
                        iFormDetail.showModes(inputing, formDetails);
                    }
                });
    }

    private List<FormDetail> getFormDetailFromDetail(boolean hasGroup, JSONArray details) {
        if (!Utils.isEmpty(details)) {
            FormDetail formDetail = null;
            JSONObject object = null;
            List<FormDetail> formDetails = new ArrayList<>();
            for (int i = 0; i < details.size(); i++) {
                object = details.getJSONObject(i);
                int id = JSONUtil.getInt(object, "fd_id", "gd_id");
                if (JSONUtil.getInt(object, "mfd_isdefault", "mdg_isdefault") == -1) {
                    formDetail = new FormDetail(hasGroup);
                    String field = JSONUtil.getText(object, "fd_field", "dg_field");
                    String values =JSONUtil. getText(object, "fd_value");
                    String caption =JSONUtil. getText(object, "fd_caption", "dg_caption");
                    formDetail.setValuesKey(field);
                    formDetail.setCaption(caption);
                    formDetail.setType(JSONUtil.getText(object, "fd_type", "dg_type"));
                    if (!Utils.isEmpty(values)) {
                        formDetail.setValues(values);
                    }
                    JSONArray coms = JSONUtil.getJSONArray(object, "COMBOSTORE");
                    if (!Utils.isEmpty(coms)) {
                        for (int j = 0; j < coms.size(); j++) {
                            JSONObject o = coms.getJSONObject(j);
                            String dlc_value =JSONUtil. getText(o, "DLC_VALUE");
                            String dlc_display =JSONUtil. getText(o, "DLC_DISPLAY");
                            formDetail.addCombostore(new FormDetail.Combostore(dlc_value, dlc_display));
                        }
                    }
                    formDetails.add(formDetail);
                }
            }
            return formDetails;
        }
        return null;
    }

    private List<FormDetail> getFormDetailFromConfig(boolean hasGroup, JSONObject object, JSONArray configs) {
        List<FormDetail> formDetails = new ArrayList<>();
        for (int i = 0; i < configs.size(); i++) {
            JSONObject o = configs.getJSONObject(i);
            if (JSONUtil.getInt(o, "MFD_ISDEFAULT", "MDG_ISDEFAULT", "mfd_isdefault", "mdg_isdefault") == -1) {
                String caption =JSONUtil.getText(o, "FD_CAPTION", "DG_CAPTION");
                String valuesKey =JSONUtil. getText(o, "FD_FIELD", "DG_FIELD");
                String values =JSONUtil. getText(object, valuesKey);
                if (Utils.isEmpty(caption) || Utils.isEmpty(valuesKey) || Utils.isEmpty(values))
                    continue;
                FormDetail detail = new FormDetail(hasGroup);
                detail.setCaption(caption);
                detail.setValues(values);
                detail.setValuesKey(valuesKey);
                detail.setType("values");
                formDetails.add(detail);
            }
        }
        return formDetails;
    }

    private void showSelect(final int nodeid, List<BaseSelectModel<Parcelable>> datas) {
        iFormDetail.hideProgress();
        new SelectPopupBuilder(ct)
                .setTitle("选择审批人")
                .setOnSelectListener(new SelectPopupBuilder.OnRadioSelectListener() {
                    @Override
                    public void callBack(BaseSelectModel data) {
                        if (data != null && !Utils.isEmpty(data.getName()))
                            submitTakeOverTask(nodeid, Utils.getFirstBrackets(data.getName()));
                    }
                })
                .setDatas(datas)
                .setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        end2List();
                    }
                })
                .builder();

    }

    private void end2List() {
        ct.startActivity(new Intent(ct, ListFormDetailActivity.class)
                .putExtra("caller", caller)
                .putExtra("title", ct.getSupportTitle()));
        ct.finish();
    }
}