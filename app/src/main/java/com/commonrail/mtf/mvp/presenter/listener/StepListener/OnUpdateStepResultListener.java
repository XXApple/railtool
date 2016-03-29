package com.commonrail.mtf.mvp.presenter.listener.StepListener;

/**
 * Created by wengyiming on 2016/3/29.
 */
public interface OnUpdateStepResultListener {
    void onUpdateStepResultSuccess(String msg);

    void onUpdateStepResultError(String error);
}
