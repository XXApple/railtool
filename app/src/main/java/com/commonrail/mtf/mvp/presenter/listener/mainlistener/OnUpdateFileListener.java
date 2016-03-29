package com.commonrail.mtf.mvp.presenter.listener.mainlistener;

import com.commonrail.mtf.mvp.model.entity.FileUpload;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/1 下午9:50
 * 修改人：wengyiming
 * 修改时间：16/3/1 下午9:50
 * 修改备注：
 */
public interface OnUpdateFileListener {
    /**
     * 成功时回调
     *
     * @param t t
     */
    void onUpdateFilesSuccess(FileUpload t);

    /**
     * 失败时回调，简单处理，没做什么
     */
    void onUpdateFilesError();
}
