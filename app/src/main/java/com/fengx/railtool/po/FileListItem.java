package com.fengx.railtool.po;

import java.io.Serializable;

/**
 * 项目名称：railtool
 * 类描述：
 * url:"http://railtool.gongguizhijia.com/file/a.zip",//文件下载路径
 * fileType:"zip",//文件类型
 * fileLength:1024,//文件大小
 * 创建人：wengyiming
 * 创建时间：16/1/11 下午11:57
 * 修改人：wengyiming
 * 修改时间：16/1/11 下午11:57
 * 修改备注：
 */
public class FileListItem implements Serializable {
    private String url = "";
    private String fileType = "";
    private String fileLength = "";

    public String getUrl() {
        return url;
    }

    public void setUrl(final String mUrl) {
        url = mUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(final String mFileType) {
        fileType = mFileType;
    }

    public String getFileLength() {
        return fileLength;
    }

    public void setFileLength(final String mFileLength) {
        fileLength = mFileLength;
    }
}
