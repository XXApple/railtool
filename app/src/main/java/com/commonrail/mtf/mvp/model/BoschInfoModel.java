package com.commonrail.mtf.mvp.model;

import com.commonrail.mtf.mvp.presenter.listener.modulelistener.OnBoschInfoListener;
import com.commonrail.mtf.util.Api.RtApi;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/29.
 */
public interface BoschInfoModel {
    void loadBoschInfo(CompositeSubscription subscription, RtApi api,String xh, OnBoschInfoListener listener);
}
