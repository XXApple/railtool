package com.commonrail.mtf.mvp.presenter;

import com.commonrail.mtf.util.Api.RtApi;

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

    /**
     * 获取操作步骤
     */
    void getRepairStep(CompositeSubscription subscription, RtApi api);

    /**
     * 初始化蓝牙
     */
    void initBl();

    /**
     * 重新连接蓝牙
     */
    void connect2Bl(CompositeSubscription subscription, RtApi api);

    /**
     * 提交结果
     */
    void updateFile(CompositeSubscription subscription, RtApi api);
}
