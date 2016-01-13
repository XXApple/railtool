package com.fengx.railtool.po;

import java.io.Serializable;

/**
 * 项目名称：railtool
 * 类描述：
 * 
 * id:1, //唯一标识
 * injectorType:"Bosch", //喷油器类型
 * moduleName:"更换阀组件", //模块名称
 * moduleOrder:1 //
 * 
 * 
 * 创建人：wengyiming
 * 创建时间：16/1/11 下午11:34
 * 修改人：wengyiming
 * 修改时间：16/1/11 下午11:34
 * 修改备注：
 */
public class Module implements Serializable {
    private int id = 0;
    private String injectorType = "";
    private String moduleName = "";
    private String moduleOrder = "";

    public int getId() {
        return id;
    }

    public void setId(final int mId) {
        id = mId;
    }

    public String getInjectorType() {
        return injectorType;
    }

    public void setInjectorType(final String mInjectorType) {
        injectorType = mInjectorType;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(final String mModuleName) {
        moduleName = mModuleName;
    }

    public String getModuleOrder() {
        return moduleOrder;
    }

    public void setModuleOrder(final String mModuleOrder) {
        moduleOrder = mModuleOrder;
    }
}
