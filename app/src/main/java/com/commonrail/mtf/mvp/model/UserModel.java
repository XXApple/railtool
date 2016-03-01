package com.commonrail.mtf.mvp.model;

import com.commonrail.mtf.mvp.presenter.OnUserListener;
import com.commonrail.mtf.util.Api.RtApi;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/1.
 */
public interface UserModel {
    void loadUser(CompositeSubscription subscription,RtApi api, OnUserListener listener);
}
