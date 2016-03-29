package com.commonrail.mtf.mvp.presenter;

import com.commonrail.mtf.util.Api.RtApi;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/1.
 */
public interface ModulePresenter {
    void getModuleList(CompositeSubscription subscription, RtApi api,String injectorType);

    void getBochInfo(CompositeSubscription subscription, RtApi api,String xh);
}
