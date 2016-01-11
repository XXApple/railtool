package com.fengx.railtool.po;

import java.io.Serializable;

/**
 * 项目名称：railtool
 * 类描述：
 * <p/>
 * injectorType:"Bosch", //喷油器类型
 * orderNum:1, //列表排序
 * injectorName:"博世喷油器", //喷油器名称
 * iconUrl:"/images/bosch.jpg" //图标地址
 * <p/>
 * <p/>
 * <p/>
 
 * 创建人：wengyiming
 * 创建时间：16/1/11 下午9:13
 * 修改人：wengyiming
 * 修改时间：16/1/11 下午9:13
 * 修改备注：
 */
public class Injector implements Serializable {
    private String injectorType = "";
    private int orderNum = 0;
    private String injectorName = "";
    private String iconUrl = "";

    public String getInjectorType() {
        return injectorType;
    }

    public void setInjectorType(final String mInjectorType) {
        injectorType = mInjectorType;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(final int mOrderNum) {
        orderNum = mOrderNum;
    }

    public String getInjectorName() {
        return injectorName;
    }

    public void setInjectorName(final String mInjectorName) {
        injectorName = mInjectorName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(final String mIconUrl) {
        iconUrl = mIconUrl;
    }
}
