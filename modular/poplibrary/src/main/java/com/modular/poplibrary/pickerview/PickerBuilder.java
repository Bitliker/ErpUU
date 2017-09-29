package com.modular.poplibrary.pickerview;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;

import com.modular.poplibrary.pickerview.lib.WheelView;

import java.util.Calendar;

/**
 * Created by Bitliker on 2017/7/5.
 */

public class PickerBuilder {
    private Context ct;
    private Type type;
    private int gravity = Gravity.CENTER;//内容显示位置 默认居中
    private String submit = "确定";//确定按钮文字
    private String cancel = "取消";//取消按钮文字
    private String title = "选择时间";//标题文字
    private int submitColor;//确定按钮颜色
    private int cancelColor;//取消按钮颜色
    private int TitleColor;//标题颜色

    private int wheelBgColor;//滚轮背景颜色
    private int titleBgColor;//标题背景颜色
    private int submitTextSize = 17;//确定取消按钮大小
    private int titleTextSize = 18;//标题字体大小
    private int contentTextSize = 18;//内容字体大小

    private Calendar date;//当前选中时间
    private Calendar startDate;//开始时间
    private Calendar endDate;//终止时间

    private boolean cyclicable = true;//是否循环
    private boolean cancelable = true;//是否能取消
    public ViewGroup decorView;//显示pickerview的根View,默认是activity的根view

    private int selectTextColor; //当前选择中的文字颜色
    private int noSelectTextColor; //没选择中的文字的颜色
    private int dividerColor; //分割线的颜色
    private int noSeletctBg; //显示时的外部背景色颜色,默认是灰色
    private WheelView.DividerType dividerType;//分隔线类型
    private float lineSpacingMultiplier = 1.6F;// 条目间距倍数 默认1.6
    private boolean isDialog;//是否是对话框模式

    public PickerBuilder(Context ct) {
        this.ct = ct;
    }

    public PickerBuilder setType(Type type) {
        this.type = type;
        return this;
    }

    public PickerBuilder setSubmitColor(int submitColor) {
        this.submitColor = submitColor;
        return this;
    }

    public PickerBuilder setCancelColor(int cancelColor) {
        this.cancelColor = cancelColor;
        return this;
    }

    public PickerBuilder setTitleColor(int titleColor) {
        TitleColor = titleColor;
        return this;
    }

    public PickerBuilder setWheelBgColor(int wheelBgColor) {
        this.wheelBgColor = wheelBgColor;
        return this;
    }

    public PickerBuilder setTitleBgColor(int titleBgColor) {
        this.titleBgColor = titleBgColor;
        return this;
    }

    public PickerBuilder setSubmitTextSize(int submitTextSize) {
        this.submitTextSize = submitTextSize;
        return this;
    }

    public PickerBuilder setTitleTextSize(int titleTextSize) {
        this.titleTextSize = titleTextSize;
        return this;
    }

    public PickerBuilder setContentTextSize(int contentTextSize) {
        this.contentTextSize = contentTextSize;
        return this;
    }

    public PickerBuilder setDate(Calendar date) {
        this.date = date;
        return this;
    }

    public PickerBuilder setStartDate(Calendar startDate) {
        this.startDate = startDate;
        return this;
    }

    public PickerBuilder setEndDate(Calendar endDate) {
        this.endDate = endDate;
        return this;
    }

    public PickerBuilder setCyclicable(boolean cyclicable) {
        this.cyclicable = cyclicable;
        return this;
    }

    public PickerBuilder setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public PickerBuilder setDecorView(ViewGroup decorView) {
        this.decorView = decorView;
        return this;
    }

    public PickerBuilder setSelectTextColor(int selectTextColor) {
        this.selectTextColor = selectTextColor;
        return this;
    }

    public PickerBuilder setNoSelectTextColor(int noSelectTextColor) {
        this.noSelectTextColor = noSelectTextColor;
        return this;
    }

    public PickerBuilder setNoSeletctBg(int noSeletctBg) {
        this.noSeletctBg = noSeletctBg;
        return this;
    }

    public PickerBuilder setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public PickerBuilder setSubmit(String submit) {
        this.submit = submit;
        return this;
    }

    public PickerBuilder setCancel(String cancel) {
        this.cancel = cancel;
        return this;
    }

    public PickerBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public PickerBuilder setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        return this;
    }


    public PickerBuilder setDividerType(WheelView.DividerType dividerType) {
        this.dividerType = dividerType;
        return this;
    }

    public PickerBuilder setLineSpacingMultiplier(float lineSpacingMultiplier) {
        this.lineSpacingMultiplier = lineSpacingMultiplier;
        return this;
    }

    public PickerBuilder setDialog(boolean dialog) {
        isDialog = dialog;
        return this;
    }

    public PickerBuilder setRang(Calendar startDate, Calendar endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }

    public void show(TimePickerView.OnTimeSelectListener onTimeSelectListener) {
        TimePickerView.Builder builder = new TimePickerView.Builder(ct, onTimeSelectListener)
                .setCancelText(cancel)//取消按钮文字
                .setSubmitText(submit)//确认按钮文字
                .setTitleText(title)//标题文字
                .isCyclic(cyclicable)//是否循环滚动
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(isDialog)
                .setOutSideCancelable(cancelable)//是否显示为对话框样式
                ;

        if (submitColor != 0) {
            builder.setSubmitColor(submitColor);
        } if (cancelColor != 0) {
            builder.setCancelColor(cancelColor);
        } if (TitleColor != 0) {
            builder.setTitleColor(TitleColor);
        } if (wheelBgColor != 0) {
            builder.setBgColor(wheelBgColor);
        } if (titleBgColor != 0) {
            builder.setTitleBgColor(titleBgColor);
        } if (submitTextSize != 0) {
            builder.setSubCalSize(submitTextSize);
        } if (titleTextSize != 0) {
            builder.setTitleSize(titleTextSize);
        } if (contentTextSize != 0) {
            builder.setContentSize(contentTextSize);
        } if (selectTextColor != 0) {
            builder.setTextColorCenter(selectTextColor);
        } if (noSelectTextColor != 0) {
            builder.setTextColorOut(noSelectTextColor);
        } if (dividerColor != 0) {
            builder.setDividerColor(dividerColor);
        } if (noSeletctBg != 0) {
            builder.setBackgroundId(noSeletctBg);
        }
        if (dividerType != null) {
            builder.setDividerType(dividerType);
            builder.setLineSpacingMultiplier(lineSpacingMultiplier);
        }
        if (date == null) {
            date = Calendar.getInstance();
        }
        builder.setDate(date);
        if (type == null || type == Type.YMD_HMS) {
            builder.setType(new boolean[]{true, true, true, true, true, true});
        } else if (type == Type.YMD) {
            builder.setType(new boolean[]{true, true, true, false, false, false});
        } else if (type == Type.YM) {
            builder.setType(new boolean[]{true, true, false, false, false, false});
        } else if (type == Type.HMS) {
            builder.setType(new boolean[]{false, false, false, true, true, true});
        } else if (type == Type.HM) {
            builder.setType(new boolean[]{false, false, false, true, true, false});
        }
        builder.setLabel("年", "月", "日", "时", "分", "秒");
        builder.build().show();
    }


    public enum Type {
        YMD_HMS, YMD, YM, HMS, HM
    }

}
