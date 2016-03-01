package com.commonrail.mtf.mvp.presenter;

import com.commonrail.mtf.mvp.model.entity.Update;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/1 下午9:36
 * 修改人：wengyiming
 * 修改时间：16/3/1 下午9:36
 * 修改备注：
 */
public interface OnCheckUpdateListener {
    /**
     * 成功时回调
     *
     * @param mUpdate mUpdate
     */
    void onCheckUpdateSuccess(Update mUpdate);

    /**
     * 失败时回调，简单处理，没做什么
     */
    void onCheckUpdateError();
}
