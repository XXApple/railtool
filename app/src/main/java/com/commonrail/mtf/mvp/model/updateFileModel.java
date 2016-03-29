package com.commonrail.mtf.mvp.model;

import com.commonrail.mtf.mvp.presenter.listener.mainlistener.OnUpdateFileListener;
import com.commonrail.mtf.util.Api.RtApi;

import rx.subscriptions.CompositeSubscription;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/1 下午9:50
 * 修改人：wengyiming
 * 修改时间：16/3/1 下午9:50
 * 修改备注：
 */
public interface UpdateFileModel {
    void checkFileUpdate(CompositeSubscription subscription,RtApi api, OnUpdateFileListener listener);
}
