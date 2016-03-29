package com.commonrail.mtf.mvp.model;

import com.commonrail.mtf.mvp.presenter.listener.StepListener.OnUpdateStepResultListener;
import com.commonrail.mtf.util.Api.RtApi;

import java.util.HashMap;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/1.
 */
public interface UpdateResultModel {
    void updateStepResult(CompositeSubscription subscription, RtApi api,HashMap<String, Object> map, OnUpdateStepResultListener listener);
}
