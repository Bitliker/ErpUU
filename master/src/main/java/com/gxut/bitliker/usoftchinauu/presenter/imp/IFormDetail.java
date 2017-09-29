package com.gxut.bitliker.usoftchinauu.presenter.imp;

import com.gxut.bitliker.usoftchinauu.model.FormDetail;

import java.util.List;

/**
 * Created by Bitliker on 2017/8/29.
 */

public interface IFormDetail extends HttpImp {

    void setTitle(String title);

    void showModes(boolean inputing,List<FormDetail> models);

}
