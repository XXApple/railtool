package com.commonrail.mtf.po;

import java.io.Serializable;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/1/11 下午8:58
 * 修改人：wengyiming
 * 修改时间：16/1/11 下午8:58
 * 修改备注：
 */
public class User implements Serializable {
    private String uname = "";
    private String amesdialMac = "";

    public String getUname() {
        return uname;
    }

    public void setUname(String mUname) {
        uname = mUname;
    }

    public String getAmesdialMac() {
        return amesdialMac;
    }

    public void setAmesdialMac(String mAmesdialMac) {
        amesdialMac = mAmesdialMac;
    }
}
