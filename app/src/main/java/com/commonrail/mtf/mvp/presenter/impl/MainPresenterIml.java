package com.commonrail.mtf.mvp.presenter.impl;

import com.commonrail.mtf.db.InjectorDb;
import com.commonrail.mtf.mvp.model.InjectorsModel;
import com.commonrail.mtf.mvp.model.UpdateFileModel;
import com.commonrail.mtf.mvp.model.UpdateModel;
import com.commonrail.mtf.mvp.model.UserModel;
import com.commonrail.mtf.mvp.model.entity.FileUpload;
import com.commonrail.mtf.mvp.model.entity.Update;
import com.commonrail.mtf.mvp.model.entity.User;
import com.commonrail.mtf.mvp.model.impl.InjectorsModelImpl;
import com.commonrail.mtf.mvp.model.impl.UpdateFilesModelImpl;
import com.commonrail.mtf.mvp.model.impl.UpdateModelImpl;
import com.commonrail.mtf.mvp.model.impl.UserModelImpl;
import com.commonrail.mtf.mvp.presenter.MainPresenter;
import com.commonrail.mtf.mvp.presenter.OnCheckUpdateListener;
import com.commonrail.mtf.mvp.presenter.OnInjectorsListener;
import com.commonrail.mtf.mvp.presenter.OnUpdateFileListener;
import com.commonrail.mtf.mvp.presenter.OnUserListener;
import com.commonrail.mtf.mvp.ui.view.MainView;
import com.commonrail.mtf.util.Api.RtApi;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/1.
 */
public class MainPresenterIml implements MainPresenter, OnUserListener, OnInjectorsListener, OnCheckUpdateListener, OnUpdateFileListener {
    /*Presenter作为中间层，持有View和Model的引用*/
    private MainView mainView;
    private UserModel userModel;
    private InjectorsModel mInjectorsModel;
    private UpdateModel mUpdateModel;
    private UpdateFileModel mUpdateFileModel;

    public MainPresenterIml(MainView mainView) {
        this.mainView = mainView;
        userModel = new UserModelImpl();
        mInjectorsModel = new InjectorsModelImpl();
        mUpdateModel = new UpdateModelImpl();
        mUpdateFileModel = new UpdateFilesModelImpl();
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
    public void checkUpdate(CompositeSubscription subscription, RtApi api) {
        mUpdateModel.checkUpdate(subscription, api, this);
    }

    @Override
    public void updateFile(CompositeSubscription subscription, RtApi api) {
        mUpdateFileModel.checkFileUpdate(subscription, api, this);
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
    public void onInjectorsSuccess(final List<InjectorDb> mInjectorDbs) {
        mainView.hideLoading();
        mainView.setInjectors(mInjectorDbs);
    }

    @Override
    public void onInjectorsError() {
        mainView.hideLoading();
        mainView.showInjectorsError();
    }

    @Override
    public void onCheckUpdateSuccess(final Update t) {
        mainView.checkUpdate(t);

    }

    @Override
    public void onCheckUpdateError() {
        mainView.showCheckUpdaterError();
    }

    @Override
    public void onUpdateFilesSuccess(final FileUpload t) {
        mainView.updateFile(t);
    }

    @Override
    public void onUpdateFilesError() {
        mainView.showUpdateFileError();
    }
}
