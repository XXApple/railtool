package com.commonrail.mtf.mvp.presenter.listener.mainlistener;

import com.commonrail.mtf.db.InjectorDb;

import java.util.List;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/1 下午9:14
 * 修改人：wengyiming
 * 修改时间：16/3/1 下午9:14
 * 修改备注：
 */
public interface OnInjectorsListener {
    /**
     * 成功或者失败时回调
     *
     * @param mInjectorDbs mInjectorDbs
     * @param mMsg
     */
    void onInjectorsResult(List<InjectorDb> mInjectorDbs, final String mMsg);

    
    
}
