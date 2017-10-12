package com.gxut.bitliker.usoftchinauu.service;

import com.gxut.code.baseutil.util.TimeUtil;
import com.gxut.code.baseutil.util.ToscatUtil;
import com.gxut.code.baseutil.util.Utils;
import com.gxut.bitliker.usoftchinauu.model.Work;
import com.gxut.bitliker.usoftchinauu.presenter.WorkPresenter;
import com.gxut.bitliker.usoftchinauu.presenter.imp.IWork;
import com.gxut.bitliker.usoftchinauu.util.AlarmUtil;
import com.gxut.bitliker.usoftchinauu.util.NotificationUtil;

import java.util.List;

import static com.gxut.bitliker.usoftchinauu.util.AlarmUtil.AUTOTASK_ACTION;

/**
 * Created by Bitliker on 2017/8/22.
 */

public class AutoTaskService implements IWork {
	private final long TIME_LONG = 2 * 60 * 60 * 1000;
	private static AutoTaskService api;

	private long lastTime;
	private WorkPresenter mPresenter;
	private List<Work> works;
	private long lastSubittime;

	public static AutoTaskService api() {
		AutoTaskService inst = api;
		if (inst == null) {
			synchronized (AutoTaskService.class) {
				inst = api;
				if (inst == null) {
					inst = new AutoTaskService();
					api = inst;
				}
			}
		}
		return inst;
	}

	private AutoTaskService() {
		lastTime = System.currentTimeMillis();
		mPresenter = new WorkPresenter(this);
	}


	public void start() {
		if (Utils.isEmpty(works) || TIME_LONG > (System.currentTimeMillis() - lastTime)) {
			loadData();
		} else {
			autoReckon();
		}
	}

	private void loadData() {
		lastTime = System.currentTimeMillis();
		mPresenter.loadData(0);
	}

	@Override
	public void showModel(List<Work> works) {
		this.works = works;
		autoReckon();
	}

	@Override
	public void doResult(boolean ok, String message) {
		lastSubittime = System.currentTimeMillis();
		if (ok) {
			NotificationUtil.sendNotification(message);
			loadData();
		} else {
			ToscatUtil.showLong(Utils.getContext(), message);
		}
	}

	//判断是否需要打卡
	private void autoReckon() {
		long neerTime = 0;
		for (Work w : works) {
			long time = getNextTime(w);
			if (time == 0) continue;
			if (time == -1) {
				if (System.currentTimeMillis() - lastSubittime > 3 * 60 * 1000) {
					mPresenter.submit(true);
				} else {
					AlarmUtil.startAlarm(AlarmUtil.AUTOTASK_REQUESTCODE, AUTOTASK_ACTION, System.currentTimeMillis() + 3 * 60 * 1000);
				}
				break;
			}
			if (neerTime == 0 || neerTime > time) {
				neerTime = time;
			}
		}
		AlarmUtil.startAlarm(AlarmUtil.AUTOTASK_REQUESTCODE, AUTOTASK_ACTION, neerTime);

	}


	/**
	 * 判断该班次的下一次打卡时间是什么时候
	 *
	 * @param w 班次
	 * @return -1：当前符合打卡 0：无法判断  other：下次打卡时间
	 */
	private long getNextTime(Work w) {
		String newTime = TimeUtil.long2Str(System.currentTimeMillis(), TimeUtil.HM);
		if (w.getWorkStart().compareTo(newTime) <= 0 && w.getWorkTime().compareTo(newTime) >= 0 && Utils.isEmpty(w.getWorkSignin())) {//符合上班范围
			return -1;
		} else if (w.getOffTime().compareTo(newTime) <= 0 && w.getOffEnd().compareTo(newTime) >= 0 && Utils.isEmpty(w.getOffSignin())) {//符合下班范围
			return -1;
		} else if (w.getWorkStart().compareTo(newTime) > 0) {
			return TimeUtil.hm2Long(w.getWorkStart()) + 10000;
		} else if (w.getOffTime().compareTo(newTime) > 0) {
			return TimeUtil.hm2Long(w.getOffTime()) + 10000;
		}
		return 0;
	}


}
