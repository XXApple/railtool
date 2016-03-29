package com.commonrail.mtf.mvp.presenter;

import com.commonrail.mtf.util.Api.RtApi;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/1.
 */
public interface MainPresenter {

    /**
     * 获取用户的逻辑
     */
    void getUser(CompositeSubscription subscription,RtApi api);

    void getInjectors(CompositeSubscription subscription, RtApi api);

    void checkUpdate(CompositeSubscription subscription, RtApi api);

    void updateFile(CompositeSubscription subscription, RtApi api);
}
