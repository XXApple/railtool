package com.commonrail.mtf.mvp.presenter.impl;

import com.commonrail.mtf.mvp.model.UserModel;
import com.commonrail.mtf.mvp.model.impl.UserModelImpl;
import com.commonrail.mtf.mvp.presenter.MainPresenter;
import com.commonrail.mtf.mvp.ui.view.MainView;

/**
 * Created by wengyiming on 2016/3/1.
 */
public class MainPresenterIml implements MainPresenter {
    private UserModel userModel;
    private MainView mainView;

    public MainPresenterIml(MainView mainView) {
        this.mainView = mainView;
        userModel = new UserModelImpl();
    }

    @Override
    public void getUser() {

    }

    @Override
    public void getInjectors(String language) {

    }

    @Override
    public void checkUpdate() {

    }

    @Override
    public void updateFile(int localFileVersion) {

    }
}
