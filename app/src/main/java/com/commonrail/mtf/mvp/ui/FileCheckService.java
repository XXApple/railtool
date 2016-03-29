package com.commonrail.mtf.mvp.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.commonrail.mtf.db.Files;
import com.commonrail.mtf.db.FilesDao;
import com.commonrail.mtf.mvp.model.UpdateFileModel;
import com.commonrail.mtf.mvp.model.entity.FileListItem;
import com.commonrail.mtf.util.common.Constant;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.common.SDCardUtils;
import com.commonrail.mtf.util.common.SPUtils;
import com.commonrail.mtf.util.db.DbUtil;
import com.yw.filedownloader.BaseDownloadTask;
import com.yw.filedownloader.FileDownloadListener;
import com.yw.filedownloader.FileDownloadQueueSet;
import com.yw.filedownloader.FileDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileCheckService extends Service {

    private final static String TMP_PATH = SDCardUtils.getSDCardPath() + File.separator + "Download" + File.separator + "railTool" + File.separator;
    private final static String TARGET_PATH = SDCardUtils.getSDCardPath() + File.separator;

    private UpdateFileModel mUpdateFileModel;

    final FileDownloadListener queueTarget = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            L.e("FileDownloadListener", task.getTag().toString());
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
        }

        @Override
        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            String localUrl = (String) task.getTag();
            L.e("completed", localUrl + " 下载完成");
            try {
                File tmpFile = new File(TMP_PATH + localUrl);
                File targetFile = new File(TARGET_PATH + localUrl);
                SDCardUtils.copyfile(tmpFile, targetFile, true);
                List<Files> mFilesList = DbUtil.getFilesService().queryBuilder().where(FilesDao.Properties.FileLocalUrl.eq(localUrl)).build().list();
                for (Files mFiles : mFilesList) {
                    mFiles.setFileStatus(1);
                    L.e("completed", localUrl + " 标记为已完成");
                    DbUtil.getFilesService().saveOrUpdate(mFiles);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<Files> mFilesList = DbUtil.getFilesService().queryBuilder().where(FilesDao.Properties.FileStatus.eq(0)).build().list();
            if (mFilesList != null && mFilesList.size() > 0) {//且未完成数大于0,则继续下载
                L.e("downloadFiles", "且未完成数大于0");
            } else {
                L.e("downloadFiles", "下载记录中全部都已完成");
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
        }

        @Override
        protected void warn(BaseDownloadTask task) {
        }
    };


    private void downloadFiles(final List<FileListItem> fileList, int latestVersion) {
        L.e("downloadFiles", "服务器需要下载文件数" + fileList.size());
        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(queueTarget);
        final List<BaseDownloadTask> tasks = new ArrayList<>();

        List<Files> mFilesDbQuene = DbUtil.getFilesService().queryAll();
//        L.e("downloadFiles", "本地记录条数：" + mFilesDbQuene.size());
        List<Files> mFileTmpQuene = new ArrayList<>();//创建一个临时的待下载队列
        if (mFilesDbQuene == null || mFilesDbQuene.isEmpty()) {
            //本地没有任何记录,说明需要更新
            L.e("downloadFiles", "本地没有任何记录,说明需要更新");
            for (int i = 0; i < fileList.size(); i++) {
                FileListItem mFileListItem = fileList.get(i);
                Files localFile = new Files();
                localFile.setFileStatus(0);
                localFile.setFileLen(mFileListItem.getFileLength());
                localFile.setFileLocalUrl(mFileListItem.getLocalUrl());
                localFile.setFileType(mFileListItem.getFileType());
                localFile.setFileUrl(mFileListItem.getUrl());
                mFileTmpQuene.add(localFile);//加入待下载任务
                DbUtil.getFilesService().save(localFile);
            }
        } else {//如果本地有未完成的记录,将未完成的任务加入待下载队列
            List mFiles = DbUtil.getFilesService().queryBuilder().where(FilesDao.Properties.FileStatus.eq(0)).build().list();
            if (mFiles != null && mFiles.size() > 0) {//且未完成数大于0,则继续下载
                L.e("downloadFiles", "且未完成数大于0,则继续下载");
                mFileTmpQuene.addAll(mFiles);
            } else {//本地有完整的记录,未完成的为0,即全部都已完成,保存最新文件版本号
                L.e("downloadFiles", "本地有完整的记录,未完成的为0,即全部都已完成,保存最新文件版本号");
                SPUtils.put(FileCheckService.this, Constant.FILE_VERSION, latestVersion);
            }
        }
        //创建下载任务
        for (int i = 0; i < mFileTmpQuene.size(); i++) {
            Files mFilesDb = mFileTmpQuene.get(i);
            tasks.add(FileDownloader.
                    getImpl()
                    .create(mFilesDb.getFileUrl())
                    .setPath(TMP_PATH + mFilesDb.getFileLocalUrl())
                    .setTag(mFilesDb.getFileLocalUrl()));
            L.e("downloadFiles", "循环创建下载任务" + mFilesDb.getFileLocalUrl());
        }
        // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
        queueSet.disableCallbackProgressTimes();
        // 所有任务在下载失败的时候都自动重试一次
        queueSet.setAutoRetryTimes(1);
//        // 串行执行该任务队列
//        queueSet.downloadSequentially(tasks);
        // 并行执行该任务队列
        queueSet.downloadTogether(tasks);
        queueSet.start();
    }

    public FileCheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
