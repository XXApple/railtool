package com.commonrail.mtf.mvp.presenter.impl;

import com.commonrail.mtf.mvp.model.GetStepModel;
import com.commonrail.mtf.mvp.model.UpdateResultModel;
import com.commonrail.mtf.mvp.model.entity.StepList;
import com.commonrail.mtf.mvp.model.impl.Step.GetStepImpl;
import com.commonrail.mtf.mvp.model.impl.Step.UpdateResultModelImpl;
import com.commonrail.mtf.mvp.presenter.StepPresenter;
import com.commonrail.mtf.mvp.presenter.listener.StepListener.OnRepairStepListener;
import com.commonrail.mtf.mvp.presenter.listener.StepListener.OnUpdateStepResultListener;
import com.commonrail.mtf.mvp.ui.view.StepView;
import com.commonrail.mtf.util.Api.RtApi;

import java.util.HashMap;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/29.
 */
public class StepPresenterImpl implements StepPresenter, OnRepairStepListener, OnUpdateStepResultListener {
    private StepView stepView;
    private GetStepModel getStepModel;
    private UpdateResultModel updateResultModel;

    public StepPresenterImpl(StepView stepView) {
        this.stepView = stepView;
        getStepModel = new GetStepImpl();
        updateResultModel = new UpdateResultModelImpl();
    }

    @Override
    public void getRepairStep(CompositeSubscription subscription, RtApi api, HashMap<String, Object> map) {
        stepView.showLoading();
        getStepModel.loadStep(subscription, api, map, this);
    }

    @Override
    public void updateResult(CompositeSubscription subscription, RtApi api, HashMap<String, Object> map) {
        stepView.showLoading();
        updateResultModel.updateStepResult(subscription, api, map, this);
    }

    @Override
    public void onLoadRepairStepSuccess(StepList stepList) {
        stepView.hideLoading();
        stepView.setRepairStep(stepList);
    }

    @Override
    public void onLoadRepairStepError(String error) {
        stepView.hideLoading();
        stepView.showRepairStepError(error);
    }

    @Override
    public void onUpdateStepResultSuccess(String msg) {
        stepView.hideLoading();
        stepView.showUpdateResultSuccessed();
    }

    @Override
    public void onUpdateStepResultError(String error) {
        stepView.hideLoading();
        stepView.showUpdateResultError(error);

    }
}
