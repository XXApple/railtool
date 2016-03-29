package com.commonrail.mtf.mvp.model;

import com.commonrail.mtf.mvp.presenter.listener.StepListener.OnRepairStepListener;
import com.commonrail.mtf.util.Api.RtApi;

import java.util.HashMap;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/1.
 */
public interface GetStepModel {
    void loadStep(CompositeSubscription subscription, RtApi api, HashMap<String, Object> map, OnRepairStepListener listener);
}
