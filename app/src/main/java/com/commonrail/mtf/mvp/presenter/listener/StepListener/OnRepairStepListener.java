package com.commonrail.mtf.mvp.presenter.listener.StepListener;

import com.commonrail.mtf.mvp.model.entity.StepList;

/**
 * Created by wengyiming on 2016/3/29.
 */
public interface OnRepairStepListener {
    void onLoadRepairStepSuccess(StepList stepList);

    void onLoadRepairStepError(String error);
}
