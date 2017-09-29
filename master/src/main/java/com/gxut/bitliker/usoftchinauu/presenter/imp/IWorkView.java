package com.gxut.bitliker.usoftchinauu.presenter.imp;

import com.gxut.bitliker.usoftchinauu.model.Locale;

/**
 * Created by Bitliker on 2017/8/16.
 */

public interface IWorkView extends IWork {

    void showProgress();

    void hideProgress();

    void neerLocale(Locale neerLocale);
}
