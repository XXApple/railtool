package com.commonrail.mtf.mvp.model.entity;

import java.io.Serializable;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/1/11 下午11:48
 * 修改人：wengyiming
 * 修改时间：16/1/11 下午11:48
 * 修改备注：
 */
public class ModuleItem implements Serializable {
    private int id = 1;
    private String moduleName = "";

    public int getId() {
        return id;
    }

    public void setId(final int mId) {
        id = mId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(final String mModuleName) {
        moduleName = mModuleName;
    }

    @Override
    public String toString() {
        return "ModuleItem{" +
                "id='" + id + '\'' +
                ", moduleName='" + moduleName + '\'' +
                '}';
    }
}
