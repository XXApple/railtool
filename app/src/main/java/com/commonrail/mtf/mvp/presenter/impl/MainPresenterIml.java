package com.commonrail.mtf.mvp.presenter.impl;

import com.commonrail.mtf.mvp.model.UserModel;
import com.commonrail.mtf.mvp.model.entity.User;
import com.commonrail.mtf.mvp.model.impl.UserModelImpl;
import com.commonrail.mtf.mvp.presenter.MainPresenter;
import com.commonrail.mtf.mvp.presenter.OnUserListener;
import com.commonrail.mtf.mvp.ui.view.MainView;
import com.commonrail.mtf.util.Api.RtApi;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/1.
 */
public class MainPresenterIml implements MainPresenter, OnUserListener {
    /*Presenter作为中间层，持有View和Model的引用*/
    private MainView mainView;
    private UserModel userModel;


    public MainPresenterIml(MainView mainView) {
        this.mainView = mainView;
        userModel = new UserModelImpl();
    }

    @Override
    public void getUser(CompositeSubscription subscription,RtApi api) {
        mainView.showLoading();
        userModel.loadUser(subscription,api, this);
    }

    @Override
    public void getInjectors(CompositeSubscription subscription, RtApi api, String language) {

    }

    @Override
    public void checkUpdate() {

    }

    @Override
    public void updateFile(int localFileVersion) {

    }

    @Override
    public void onSuccess(User user) {
        mainView.hideLoading();
        mainView.setUserInfo(user);
    }

    @Override
    public void onError() {
        mainView.hideLoading();
        mainView.showError();
    }
}
