package com.commonrail.mtf.mvp.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/1/11 下午11:43
 * 修改人：wengyiming
 * 修改时间：16/1/11 下午11:43
 * 修改备注：
 */
public class StepList implements Serializable {
    private List<Step> stepList;
    private ModuleItem module;

    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> mStepList) {
        stepList = mStepList;
    }

    public ModuleItem getModule() {
        return module;
    }

    public void setModule(ModuleItem mModule) {
        module = mModule;
    }

    
    

}
