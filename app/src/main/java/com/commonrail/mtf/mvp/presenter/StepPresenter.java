package com.commonrail.mtf.mvp.presenter;

import com.commonrail.mtf.util.Api.RtApi;

import java.util.HashMap;

import rx.subscriptions.CompositeSubscription;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/26 下午10:47
 * 修改人：wengyiming
 * 修改时间：16/3/26 下午10:47
 * 修改备注：
 */
public interface StepPresenter {
    void getRepairStep(CompositeSubscription subscription, RtApi api, HashMap<String, Object> map);

    void updateResult(CompositeSubscription subscription, RtApi api, HashMap<String, Object> map);
}
