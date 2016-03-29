package com.commonrail.mtf.mvp.presenter.listener.mainlistener;

import com.commonrail.mtf.mvp.model.entity.User;

/**
 * Created by wengyiming on 2016/3/1.
 */
public interface OnUserListener {
    /**
     * 成功时回调
     *
     * @param user user
     */
    void onSuccess(User user);

    /**
     * 失败时回调，简单处理，没做什么
     */
    void onError();
}
