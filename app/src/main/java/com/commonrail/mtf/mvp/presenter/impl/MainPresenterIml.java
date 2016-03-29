package com.commonrail.mtf.mvp.presenter.impl;

import com.commonrail.mtf.db.InjectorDb;
import com.commonrail.mtf.mvp.model.InjectorsModel;
import com.commonrail.mtf.mvp.model.UserModel;
import com.commonrail.mtf.mvp.model.entity.User;
import com.commonrail.mtf.mvp.model.impl.main.InjectorsModelImpl;
import com.commonrail.mtf.mvp.model.impl.main.UserModelImpl;
import com.commonrail.mtf.mvp.presenter.MainPresenter;
import com.commonrail.mtf.mvp.presenter.listener.mainlistener.OnInjectorsListener;
import com.commonrail.mtf.mvp.presenter.listener.mainlistener.OnUserListener;
import com.commonrail.mtf.mvp.ui.view.MainView;
import com.commonrail.mtf.util.Api.RtApi;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/1.
 */
public class MainPresenterIml implements MainPresenter, OnUserListener, OnInjectorsListener {
    /*Presenter作为中间层，持有View和Model的引用*/
    private MainView mainView;
    private UserModel userModel;
    private InjectorsModel mInjectorsModel;

    public MainPresenterIml(MainView mainView) {
        this.mainView = mainView;
        userModel = new UserModelImpl();
        mInjectorsModel = new InjectorsModelImpl();
    }


    @Override
    public void getUser(CompositeSubscription subscription, RtApi api) {
        mainView.showLoading();
        userModel.loadUser(subscription, api, this);

    }

    @Override
    public void getInjectors(CompositeSubscription subscription, RtApi api) {
        mainView.showLoading();
        mInjectorsModel.loadInjectors(subscription, api, this);

    }


    @Override
    public void onSuccess(User user) {
        mainView.hideLoading();
        mainView.setUserInfo(user);
    }

    @Override
    public void onError() {
        mainView.hideLoading();
        mainView.showUserError();
    }

    @Override
    public void onInjectorsResult(final List<InjectorDb> mInjectorDbs, final String mMsg) {
        mainView.hideLoading();
        mainView.setInjectors(mInjectorDbs, mMsg);
    }

}
