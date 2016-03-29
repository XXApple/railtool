package com.commonrail.mtf.mvp.model;

import com.commonrail.mtf.mvp.presenter.listener.modulelistener.OnModuleListListener;
import com.commonrail.mtf.util.Api.RtApi;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/1.
 */
public interface ModuleListModel {
    void loadModulist(CompositeSubscription subscription, RtApi api, String injectorType, OnModuleListListener listener);
}
