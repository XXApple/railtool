package com.commonrail.mtf.mvp.presenter.listener.modulelistener;

import com.commonrail.mtf.mvp.model.entity.Module;

import java.util.List;

/**
 * Created by wengyiming on 2016/3/29.
 */
public interface OnModuleListListener {
    void onLoadModuleListSuccess(List<Module> moduleList);

    void onLoadModuleListError(String error);
}
