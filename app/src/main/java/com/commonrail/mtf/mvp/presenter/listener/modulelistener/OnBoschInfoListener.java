package com.commonrail.mtf.mvp.presenter.listener.modulelistener;

import com.commonrail.mtf.mvp.model.entity.Bosch;

/**
 * Created by wengyiming on 2016/3/29.
 */
public interface OnBoschInfoListener {
    void onLoadBoschInfoSuccess(Bosch boschInfo);

    void onLoadBoschInfoError(String error);
}
