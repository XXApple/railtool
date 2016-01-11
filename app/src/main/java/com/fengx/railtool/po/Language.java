package com.fengx.railtool.po;

import java.io.Serializable;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/1/12 上午1:06
 * 修改人：wengyiming
 * 修改时间：16/1/12 上午1:06
 * 修改备注：
 */
public class Language implements Serializable {
    private String language = "zh_CN";

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String mLanguage) {
        language = mLanguage;
    }

    public Language(final String mLanguage) {
        language = mLanguage;
    }
}
