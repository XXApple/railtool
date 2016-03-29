package com.commonrail.mtf.mvp.ui.view;

import com.commonrail.mtf.db.InjectorDb;
import com.commonrail.mtf.mvp.model.entity.User;

import java.util.List;

/**
 * Created by wengyiming on 2016/3/1.
 */
public interface MainView {
    void showLoading();

    void hideLoading();

    void showUserError();


    void setUserInfo(User user);

    void setInjectors(List<InjectorDb> injectors, final String mMsg);

}
