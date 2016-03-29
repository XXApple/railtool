package com.commonrail.mtf.mvp.ui.view;

import com.commonrail.mtf.mvp.model.entity.StepList;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/27 上午9:18
 * 修改人：wengyiming
 * 修改时间：16/3/27 上午9:18
 * 修改备注：
 */
public interface StepView {

    void showLoading();

    void hideLoading();

    void showRepairStepError(String error);

    void showUpdateResultError(String error);

    void setRepairStep(StepList stepList);

    void showUpdateResultSuccessed();

}
