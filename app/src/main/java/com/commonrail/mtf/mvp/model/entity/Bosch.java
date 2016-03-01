package com.commonrail.mtf.mvp.model.entity;

import java.io.Serializable;

/**
 * 项目名称：railtool
 * 类描述：
 * <p/>
 * xh:"0445120019",//型号
 * cs:"RENAULT V.I. 4/6L",//厂商
 * yzxh:"0433171682",//油嘴型号
 * yzkyh:"DLLA140P1051",//油嘴刻印号
 * fzjxh:"F00RJ00447/1895",//阀组件型号
 * <p/>
 * <p/>
 * 创建人：wengyiming
 * 创建时间：16/1/11 下午11:37
 * 修改人：wengyiming
 * 修改时间：16/1/11 下午11:37
 * 修改备注：
 */
public class Bosch implements Serializable {
    private String xh = "";
    private String cs = "";
    private String yzxh = "";
    private String yzkyh = "";
    private String fzjxh = "";

    public String getXh() {
        return xh;
    }

    public void setXh(final String mXh) {
        xh = mXh;
    }

    public String getCs() {
        return cs;
    }

    public void setCs(final String mCs) {
        cs = mCs;
    }

    public String getYzxh() {
        return yzxh;
    }

    public void setYzxh(final String mYzxh) {
        yzxh = mYzxh;
    }

    public String getYzkyh() {
        return yzkyh;
    }

    public void setYzkyh(final String mYzkyh) {
        yzkyh = mYzkyh;
    }

    public String getFzjxh() {
        return fzjxh;
    }

    public void setFzjxh(final String mFzjxh) {
        fzjxh = mFzjxh;
    }
}
