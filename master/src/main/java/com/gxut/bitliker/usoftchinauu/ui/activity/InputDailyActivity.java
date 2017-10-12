package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.config.AppConfig;
import com.gxut.bitliker.usoftchinauu.model.BaseSelectModel;
import com.gxut.bitliker.usoftchinauu.network.UrlHelper;
import com.gxut.bitliker.usoftchinauu.util.SelectPopupBuilder;
import com.gxut.code.baseutil.ui.base.BaseActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputDailyActivity extends BaseActivity implements OnHttpCallback {
    private final int TAKE_OVER_TASK = 14;
    private final int LOAD_MULTI_NODE = 13;
    private final int SUBMIT = 15;
    private final int SUBMIT_WORK = 12;
    private final int SUBMIT_EXPERIENCE = 11;

    private final int TAG_ID = 1;
    private final int TAG_NEER_SUB = 2;


    @BindView(R.id.commentET)
    AppCompatEditText commentET;
    @BindView(R.id.planET)
    AppCompatEditText planET;
    @BindView(R.id.experienceET)
    AppCompatEditText experienceET;
    private int id;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.list == item.getItemId()) {
            end2List();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_daily);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getIntExtra("id", 0);
            String summary = intent.getStringExtra("summary");
            String plan = intent.getStringExtra("plan");
            String experience = intent.getStringExtra("experience");
            commentET.setText(Utils.getText(summary));
            planET.setText(Utils.getText(plan));
            experienceET.setText(Utils.getText(experience));
        }
    }

    @OnClick({R.id.saveBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.saveBtn:
                save();
                break;
        }
    }


    private void save() {
        if (TextUtils.isEmpty(commentET.getText())) {
            showMessage(commentET.getHint().toString());
            return;
        }
        showProgress();
        boolean isUpdata = id > 0;
        Map<String, Object> formStore = new HashMap<>();
        String url = null;
        if (isUpdata) {
            url = UrlHelper.api().updateWorkDailyUrl();
            formStore.put("wd_id", id);
        } else {
            url = UrlHelper.api().addWorkReportUrl();
        }
        formStore.put("wd_comment", JSONUtil.toHttpString(Utils.getText(commentET)));//工作总结
        formStore.put("wd_plan", JSONUtil.toHttpString(Utils.getText(planET)));//工作计划
        formStore.put("wd_experience", JSONUtil.toHttpString(Utils.getText(experienceET)));//心得
        formStore.put("wd_empcode", AppConfig.api().getLoginUser().getEmCode());
        HttpClient.api().request(new Parameter.Builder()
                .url(url)
                .addParams("caller", "WorkDaily")
                .mode(Parameter.POST)
                .addParams("formStore", JSONUtil.mapToJson(formStore))
                .addTag(TAG_NEER_SUB, isUpdata)
                .record(SUBMIT_EXPERIENCE)
                .bulid(), this);

    }

    private void catchWorkContent(int id) {
        showProgress();
        HttpClient.api().request(new Parameter.Builder()
                .url(UrlHelper.api().catchWorkContentUrl())
                .addParams("caller", "WorkDaily")
                .addParams("id", id)
                .addTag(TAG_ID, id)
                .mode(Parameter.POST)
                .record(SUBMIT_WORK)
                .bulid(), this);
    }

    private void submit(int id) {
        showProgress();
        HttpClient.api().request(new Parameter.Builder()
                .url(UrlHelper.api().submitWorkDailyUrl())
                .addParams("caller", "WorkDaily")
                .addParams("id", id)
                .addTag(TAG_ID, id)
                .mode(Parameter.POST)
                .record(SUBMIT)
                .bulid(), this);
    }


    private void loadMultiNode(int id) {
        showProgress();
        HttpClient.api().request(new Parameter.Builder()
                .url(UrlHelper.api().getMultiNodeUrl())
                .addParams("caller", "WorkDaily")
                .addParams("id", id)
                .mode(Parameter.GET)
                .record(LOAD_MULTI_NODE)
                .bulid(), this);
    }

    private void submitTakeOverTask(int nodeId, String emCode) {
        showProgress();
        Map<String, Object> params = new HashMap<>();
        params.put("nodeId", nodeId);
        params.put("em_code", emCode);
        HttpClient.api().request(new Parameter.Builder()
                .url(UrlHelper.api().takeOverTaskUrl())
                .addParams("params", JSONUtil.mapToJson(params))
                .addParams("_noc", 1)
                .mode(Parameter.POST)
                .record(TAKE_OVER_TASK)
                .bulid(), this);
    }


    @Override
    public void onSuccess(Success success) {

        switch (success.getRecord()) {
            case SUBMIT_EXPERIENCE:
                boolean nerrSubmit = (boolean) success.getTag(TAG_NEER_SUB);
                if (nerrSubmit) {
                    submit(id);
                } else {
                    handExperience(success.getJSONArray("data"));
                }
                break;
            case SUBMIT_WORK:
                loadMultiNode((Integer) success.getTag(TAG_ID));
                break;
            case LOAD_MULTI_NODE:
                handMultiNode(success.getJSONArray("assigns"));
                break;
            case TAKE_OVER_TASK:
                end2List();
                break;
            case SUBMIT:
                catchWorkContent(id);
                break;
        }
        hideProgress();
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
        }
    }

    private void handExperience(JSONArray datas) {
        if (Utils.getSize(datas) > 0) {
            JSONObject data = datas.getJSONObject(0);
            if (data != null) {
                int id = JSONUtil.getInt(data, "WD_ID");
                catchWorkContent(id);
            }
        }
    }

    @Override
    public void onFailure(Failure failure) {
        showMessage(failure.getMessage());
    }

    private void showSelect(final int nodeid, List<BaseSelectModel<Parcelable>> datas) {
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
        startActivity(new Intent(ct, ListDailyActivity.class));
        finish();
    }
}
