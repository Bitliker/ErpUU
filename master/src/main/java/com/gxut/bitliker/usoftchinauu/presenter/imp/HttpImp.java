package com.gxut.bitliker.usoftchinauu.presenter.imp;

/**
 * Created by Bitliker on 2017/6/26.
 */

public interface HttpImp {
    void showProgress();

    void hideProgress();

    void finish();

    void showMessage(String message);
}
