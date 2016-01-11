package com.fengx.railtool.po;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名称：railtool
 * 类描述：
 * versionCode:20151231, //文件更新版本号
 * fileList:[
 * {
 * FileListItem
 * },
 * {
 * FileListItem
 * },
 * ]
 * 创建人：wengyiming
 * 创建时间：16/1/11 下午11:56
 * 修改人：wengyiming
 * 修改时间：16/1/11 下午11:56
 * 修改备注：
 */
public class FileUpload implements Serializable {
    private String versionCode = "";
    private List<FileListItem> fileList;

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(final String mVersionCode) {
        versionCode = mVersionCode;
    }

    public List<FileListItem> getFileList() {
        return fileList;
    }

    public void setFileList(final List<FileListItem> mFileList) {
        fileList = mFileList;
    }
}
