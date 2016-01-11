package com.fengx.railtool.po;

import java.io.Serializable;

/**
 * 项目名称：railtool
 * 类描述：
 * <p/>
 * appVersionCode:1, //版本号，app可以根据此版本号比对是否和本地版本一致
 * forced:true,//是否强制更新，如果是强制更新，必须更新才可使用软件
 * url:"http://railtool.gongguizhijia.com/download/railtool.apk"//APK下载url
 * <p/>
 * <p/>
 * <p/>
 * 创建人：wengyiming
 * 创建时间：16/1/11 下午11:54
 * 修改人：wengyiming
 * 修改时间：16/1/11 下午11:54
 * 修改备注：
 */
public class Update implements Serializable {
    private String appVersionCode = "";
    private String forced = "";
    private String url = "";

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(final String mAppVersionCode) {
        appVersionCode = mAppVersionCode;
    }

    public String getForced() {
        return forced;
    }

    public void setForced(final String mForced) {
        forced = mForced;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String mUrl) {
        url = mUrl;
    }
}
