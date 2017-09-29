package com.gxut.bitliker.usoftchinauu.presenter.imp;


import com.gxut.bitliker.usoftchinauu.model.Work;

import java.util.List;

/**
 * Created by Bitliker on 2017/6/26.
 */

public interface IWork {

    void showModel(List<Work> works);

    void doResult(boolean ok,String message);
}
