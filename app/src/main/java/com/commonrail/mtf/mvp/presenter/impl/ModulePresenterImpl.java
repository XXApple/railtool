package com.commonrail.mtf.mvp.presenter.impl;

import com.commonrail.mtf.mvp.model.BoschInfoModel;
import com.commonrail.mtf.mvp.model.ModuleListModel;
import com.commonrail.mtf.mvp.model.entity.Bosch;
import com.commonrail.mtf.mvp.model.entity.Module;
import com.commonrail.mtf.mvp.model.impl.module.BoschInfoModelImpl;
import com.commonrail.mtf.mvp.model.impl.module.ModuleListModelImpl;
import com.commonrail.mtf.mvp.presenter.ModulePresenter;
import com.commonrail.mtf.mvp.presenter.listener.modulelistener.OnBoschInfoListener;
import com.commonrail.mtf.mvp.presenter.listener.modulelistener.OnModuleListListener;
import com.commonrail.mtf.mvp.ui.view.ModuleView;
import com.commonrail.mtf.util.Api.RtApi;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/29.
 */
public class ModulePresenterImpl implements ModulePresenter, OnModuleListListener, OnBoschInfoListener {
    private ModuleView mModuleView;
    private ModuleListModel mModuleListModel;
    private BoschInfoModel mBoschInfoModel;

    public ModulePresenterImpl(ModuleView moduleView) {
        this.mModuleView = moduleView;
        mModuleListModel = new ModuleListModelImpl();
        mBoschInfoModel = new BoschInfoModelImpl();
    }


    @Override
    public void getModuleList(CompositeSubscription subscription, RtApi api, String injectorType) {
        mModuleView.showLoading();
        mModuleListModel.loadModulist(subscription, api, injectorType, this);
    }

    @Override
    public void getBochInfo(CompositeSubscription subscription, RtApi api, String xh) {
        mBoschInfoModel.loadBoschInfo(subscription, api, xh, this);
    }

    @Override
    public void onLoadModuleListSuccess(List<Module> moduleList) {
        mModuleView.hideLoading();
        mModuleView.setModuleList(moduleList);
    }

    @Override
    public void onLoadModuleListError(String error) {
        mModuleView.hideLoading();
        mModuleView.showModuleListError(error);
    }

    @Override
    public void onLoadBoschInfoSuccess(Bosch boschInfo) {
        mModuleView.setBoschInfo(boschInfo);
    }

    @Override
    public void onLoadBoschInfoError(String error) {
        mModuleView.showBoschInfoError(error);
    }
}
