package com.commonrail.mtf.mvp.model;

import com.commonrail.mtf.mvp.presenter.listener.mainlistener.OnInjectorsListener;
import com.commonrail.mtf.util.Api.RtApi;

import rx.subscriptions.CompositeSubscription;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/1 下午9:13
 * 修改人：wengyiming
 * 修改时间：16/3/1 下午9:13
 * 修改备注：
 */
public interface InjectorsModel {
    void loadInjectors(CompositeSubscription subscription,RtApi api,OnInjectorsListener listener);
}
