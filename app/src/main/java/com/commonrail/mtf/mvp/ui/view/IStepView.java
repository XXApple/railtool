package com.commonrail.mtf.mvp.ui.view;

import com.commonrail.mtf.db.InjectorDb;
import com.commonrail.mtf.mvp.model.entity.FileUpload;
import com.commonrail.mtf.mvp.model.entity.Update;
import com.commonrail.mtf.mvp.model.entity.User;

import java.util.List;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/27 上午9:18
 * 修改人：wengyiming
 * 修改时间：16/3/27 上午9:18
 * 修改备注：
 */
public interface IStepView {

    void showLoading();

    void hideLoading();

    void showStepError();
    void showBLError();
    void showCheckUpdaterError();
    void showUpdateFileError();


    void setUserInfo(User user);

    void setInjectors(List<InjectorDb> injectors);

    void checkUpdate(Update mUpdate);

    void updateFile(FileUpload t);
}
