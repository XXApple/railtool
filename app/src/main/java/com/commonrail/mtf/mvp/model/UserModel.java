package com.commonrail.mtf.mvp.model;

import com.commonrail.mtf.mvp.presenter.OnUserListener;

/**
 * Created by wengyiming on 2016/3/1.
 */
public interface UserModel {
    void loadUser( OnUserListener listener);
}
