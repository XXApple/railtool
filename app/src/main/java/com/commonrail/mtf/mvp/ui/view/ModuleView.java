package com.commonrail.mtf.mvp.ui.view;

import com.commonrail.mtf.mvp.model.entity.Bosch;
import com.commonrail.mtf.mvp.model.entity.Module;

import java.util.List;

/**
 * Created by wengyiming on 2016/3/1.
 */
public interface ModuleView {
    void showLoading();

    void hideLoading();

    void showModuleListError(String error);

    void showBoschInfoError(String error);


    void setBoschInfo(Bosch boschInfo);

    void setModuleList(List<Module> moduleList);


}
