package com.gxut.bitliker.usoftchinauu.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.gxut.bitliker.baseutil.ui.base.BaseActivity;
import com.gxut.bitliker.baseutil.util.Utils;
import com.gxut.bitliker.httpclient.HttpClient;
import com.gxut.bitliker.httpclient.request.Parameter;
import com.gxut.bitliker.httpclient.response.Failure;
import com.gxut.bitliker.httpclient.response.OnHttpCallback;
import com.gxut.bitliker.httpclient.response.Success;
import com.gxut.bitliker.usoftchinauu.R;
import com.gxut.bitliker.usoftchinauu.model.Daily;
import com.gxut.bitliker.usoftchinauu.network.UrlHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsDailyActivity extends BaseActivity implements OnHttpCallback {

	private final int TAG_DELETE = 2;
	private final int UNSUBMIT = 11;
	private final int DELETE = 12;
	@BindView(R.id.summaryTV)
	TextView summaryTV;
	@BindView(R.id.planTV)
	TextView planTV;
	@BindView(R.id.experienceTV)
	TextView experienceTV;
	@BindView(R.id.contentWeb)
	WebView contentWeb;
	@BindView(R.id.statusTV)
	TextView statusTV;
	@BindView(R.id.timeTV)
	TextView timeTV;
	private int id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_daily);
		ButterKnife.bind(this);
		initView();
	}


	@OnClick({R.id.uncommitBtn, R.id.deleteBtn})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.uncommitBtn:
				unsubmit(false);
				break;
			case R.id.deleteBtn:
				unsubmit(true);
				break;
		}
	}


	private void initView() {
		Intent intent = getIntent();
		if (intent != null) {
			Daily daily = intent.getParcelableExtra("model");
			id = daily.getId();
			summaryTV.setText(daily.getComment());
			planTV.setText(daily.getPlan());
			experienceTV.setText(daily.getExperience());
			if (!Utils.isEmpty(daily.getWebText())) {
				contentWeb.loadDataWithBaseURL(null, daily.getWebText(), "text/html", "utf-8", null);
				contentWeb.setVisibility(View.VISIBLE);
			} else {
				contentWeb.setVisibility(View.GONE);
			}
			String status = daily.getStatus();
			if (status.equals("已审核")) {
				findViewById(R.id.uncommitBtn).setVisibility(View.GONE);
				findViewById(R.id.deleteBtn).setVisibility(View.GONE);
			}
			timeTV.setText(daily.getDate());
			statusTV.setText(status);
		}
	}




	private void unsubmit(boolean isDelete) {
		showProgress();
		HttpClient.api().request(new Parameter.Builder()
				.url(UrlHelper.api().commonresUrl("WorkDaily"))
				.addParams("caller", "WorkDaily")
				.addParams("id", id)
				.addTag(TAG_DELETE, isDelete)
				.mode(Parameter.POST)
				.record(UNSUBMIT)
				.bulid(), this);
	}

	private void delete() {
		showProgress();
		HttpClient.api().request(new Parameter.Builder()
				.url(UrlHelper.api().commondeleteUrl("WorkDaily"))
				.addParams("caller", "WorkDaily")
				.addParams("id", id)
				.mode(Parameter.POST)
				.record(DELETE)
				.bulid(), this);
	}

	@Override
	public void onSuccess(Success success) {
		hideProgress();
		switch (success.getRecord()) {
			case UNSUBMIT:
				if ((Boolean) success.getTag(TAG_DELETE)) {
					delete();
				} else {
					startActivity(new Intent(ct, InputDailyActivity.class)
							.putExtra("id", id)
							.putExtra("summary", Utils.getText(summaryTV))
							.putExtra("plan", Utils.getText(planTV))
							.putExtra("experience", Utils.getText(experienceTV)));
				}
				break;
			case DELETE:
				showMessage("删除成功");
				setResult(20);
				break;
		}
		finish();
	}

	@Override
	public void onFailure(Failure failure) {
		showMessage(failure.getMessage());
	}
}
