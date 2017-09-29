package com.modular.poplibrary.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.modular.poplibrary.R;
import com.modular.poplibrary.pickerview.OptionsPickerView;

public class DemoPockerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_pocker);
    }


    //自定义视图
    private void timeSelect() {
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
//                String tx = options1Items.get(options1).getPickerViewText()
//                        + options2Items.get(options1).get(option2)
//                        + options3Items.get(options1).get(option2).get(options3).getPickerViewText();
//                tvOptions.setText(tx);
            }
        })
                .setSubmitText("确定")//确定按钮文字
                .setCancelText("取消")//取消按钮文字
                .setTitleText("城市选择")//标题
                .setSubCalSize(18)//确定和取消文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLUE)//确定按钮文字颜色
                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                .setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
                .setBgColor(0xFF000000)//滚轮背景颜色 Night mode
                .setContentTextSize(18)//滚轮文字大小
                .setLinkage(false)//设置是否联动，默认true
                .setLabels("省", "市", "区")//设置选择的三级单位
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setCyclic(false, false, false)//循环与否
                .setSelectOptions(1, 1, 1)  //设置默认选中项
                .setOutSideCancelable(false)//点击外部dismiss default true
                .isDialog(true)//是否显示为对话框样式
                .build();
//        pvOptions.setPicker(options1Items, options2Items, options3Items);//添加数据源
    }


    private void timeSelect2() {
//        // 注意：自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针
//        // 具体可参考demo 里面的两个自定义布局
//        final OptionsPickerView pvCustomOptions = null;
//        OptionsPickerView.Builder builder = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
//            @Override
//            public void onOptionsSelect(int options1, int option2, int options3, View v) {
//                //返回的分别是三个级别的选中位置
//                String tx = cardItem.get(options1).getPickerViewText();
//                btn_CustomOptions.setText(tx);
//            }
//        });
//        builder.setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
//            @Override
//            public void customLayout(View v) {
//                //自定义布局中的控件初始化及事件处理
//                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
//                final TextView tvAdd = (TextView) v.findViewById(R.id.tv_add);
//                ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
//                tvSubmit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        pvCustomOptions.returnData(tvSubmit);
//                    }
//                });
//                ivCancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        pvCustomOptions.dismiss();
//                    }
//                });
//
//                tvAdd.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        getData();
//                        pvCustomOptions.setPicker(cardItem);
//                    }
//                });
//
//            }
//        });
//        pvCustomOptions = builder.build();
//        pvCustomOptions.setPicker(cardItem);//添加数据
    }
}
