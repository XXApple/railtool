package com.commonrail.mtf.mvp.model.entity;

import java.io.Serializable;
import java.util.List;


//
//{
//        "data": {
//        "versionList": [
//        {
//        "fileList": [
//        {
//        "fileLength": 49146,
//        "fileType": "jpg",
//        "localUrl": "/video/b.jpg",
//        "url": "http://139.196.110.128:8280/railtool/images/b.jpg"
//        }
//        ],
//        "versionCode": 20160229
//        },
//        {
//        "fileList": [
//        {
//        "fileLength": 49146,
//        "fileType": "jpg",
//        "localUrl": "/images/a.jpg",
//        "url": "http://139.196.110.128:8280/railtool/images/a.jpg"
//        },
//        {
//        "fileLength": 25059433,
//        "fileType": "mp4",
//        "localUrl": "/video/a.mp4",
//        "url": "http://139.196.110.128:8280/railtool/video/a.mp4"
//        }
//        ],
//        "versionCode": 20151231
//        }
//        ]
//        },
//        "msg": "查询成功",
//        "status": 200
//        }

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
    private int versionCode = 0;
    private List<FileListItem> fileList;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(final int mVersionCode) {
        versionCode = mVersionCode;
    }

    public List<FileListItem> getFileList() {
        return fileList;
    }

    public void setFileList(final List<FileListItem> mFileList) {
        fileList = mFileList;
    }


    @Override
    public String toString() {
        return "FileUpload{" +
                "versionCode=" + versionCode +
                ", fileList=" + fileList +
                '}';
    }
}
