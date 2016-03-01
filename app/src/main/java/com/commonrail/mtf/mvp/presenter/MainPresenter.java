package com.commonrail.mtf.mvp.presenter;

/**
 * Created by wengyiming on 2016/3/1.
 */
public interface MainPresenter {
    /**
     * 获取用户的逻辑
     */
    void getUser();

    void getInjectors(String language);

    void checkUpdate();

    void updateFile(int localFileVersion);
}
