package com.commonrail.mtf.mvp.ui.service;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;

import com.commonrail.mtf.db.Files;
import com.commonrail.mtf.db.FilesDao;
import com.commonrail.mtf.mvp.model.UpdateFileModel;
import com.commonrail.mtf.mvp.model.UpdateModel;
import com.commonrail.mtf.mvp.model.entity.FileListItem;
import com.commonrail.mtf.mvp.model.entity.FileUpload;
import com.commonrail.mtf.mvp.model.entity.Update;
import com.commonrail.mtf.mvp.model.impl.main.UpdateFilesModelImpl;
import com.commonrail.mtf.mvp.model.impl.main.UpdateModelImpl;
import com.commonrail.mtf.mvp.presenter.listener.mainlistener.OnCheckUpdateListener;
import com.commonrail.mtf.mvp.presenter.listener.mainlistener.OnUpdateFileListener;
import com.commonrail.mtf.util.Api.Config;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.ChannelUtil;
import com.commonrail.mtf.util.common.Constant;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.common.NetUtils;
import com.commonrail.mtf.util.common.SDCardUtils;
import com.commonrail.mtf.util.common.SPUtils;
import com.commonrail.mtf.util.db.DbUtil;
import com.commonrail.mtf.util.retrofit.RxUtils;
import com.yw.filedownloader.BaseDownloadTask;
import com.yw.filedownloader.FileDownloadListener;
import com.yw.filedownloader.FileDownloadQueueSet;
import com.yw.filedownloader.FileDownloader;
import com.yw.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;


public class FileCheckService extends Service implements OnCheckUpdateListener, OnUpdateFileListener {

    private final static String TAG = "FileCheckService";
    private final static String TMP_PATH = SDCardUtils.getSDCardPath() + File.separator + "Download" + File.separator + "railTool" + File.separator;
    private final static String TARGET_PATH = SDCardUtils.getSDCardPath() + File.separator;
    protected CompositeSubscription subscription;
    protected RtApi api = RxUtils.createApi(RtApi.class, Config.BASE_URL);

    public FileCheckService() {

    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        subscription = RxUtils.getNewCompositeSubIfUnsubscribed(subscription);
        final UpdateModel mUpdateModel = new UpdateModelImpl();
        final UpdateFileModel mUpdateFileModel = new UpdateFilesModelImpl();
        mUpdateFileModel.checkFileUpdate(subscription, api, this);
        mUpdateModel.checkUpdate(subscription, api, this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(subscription);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCheckUpdateSuccess(final Update t) {
        if (t == null) return;
        final String vc = t.getAppVersionCode();
        boolean forced = t.getForced();
        final String url = t.getUrl();
        L.e(t.toString());
        if (!AppUtils.checkVersion(vc)) return;
        GlobalUtils.ShowDialog(this, "提示", "发现新版本，是否更新", !forced, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //download and update
                String savePath1 = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "railtool" + vc + ".apk";
                downloadApkAndUpdate(url, savePath1);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) dialog.dismiss();
            }
        });
    }


    @Override
    public void onUpdateFilesSuccess(final FileUpload t) {
        if (t == null) {
            L.e(TAG, "请求结果为空\n");
            return;
        }
        int localFileVersion = (int) SPUtils.get(this, Constant.FILE_VERSION, 0);
        if (localFileVersion == 0) {
            localFileVersion = Integer.parseInt(ChannelUtil.getChannel(this));
        }
        L.e(TAG, "请求结果不为空\n" + t.toString());
        //wifi网络下自动下载最新图片和视频资源
        if (NetUtils.isWifi(this)) {
            if (t.getFileList() == null || t.getFileList().isEmpty()) {
                L.e(TAG, " 文件列表为空,当前版本即最新版本:" + localFileVersion + " 最新文件版本号为:" + t.getVersionCode() + "\n");
                return;
            }
            L.e(TAG, "本地文件版本号:" + localFileVersion + " 服务器文件版本号" + t.getVersionCode());
            if (localFileVersion < t.getVersionCode()) {//如果服务器文件版本大于本地文件版本,则有新的更新
                L.e(TAG, "发现新的文件\n");
                downloadFiles(t.getFileList(), t.getVersionCode());
            }

        }
    }

    @Override
    public void onUpdateFilesError() {
        L.e(TAG, "无新版文件\n");
    }

    @Override
    public void onCheckUpdateError() {
        L.e(TAG, "无更新\n");
    }


    private void downloadApkAndUpdate(String url, String savePath) {
        FileDownloader.getImpl().create(url)
                .setPath(savePath)
                .setListener(mApkDownloadListener).start();
    }

    private void downloadFiles(final List<FileListItem> fileList, int latestVersion) {
        L.e(TAG, "服务器需要下载文件数" + fileList.size());
        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(queueTarget);
        final List<BaseDownloadTask> tasks = new ArrayList<>();

        List<Files> mFilesDbQuene = DbUtil.getFilesService().queryAll();
//        L.e("downloadFiles", "本地记录条数：" + mFilesDbQuene.size());
        List<Files> mFileTmpQuene = new ArrayList<>();//创建一个临时的待下载队列
        if (mFilesDbQuene == null || mFilesDbQuene.isEmpty()) {
            //本地没有任何记录,说明需要更新
            L.e(TAG, "本地没有任何记录,说明需要更新");
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
                L.e(TAG, "且未完成数大于0,则继续下载");
                mFileTmpQuene.addAll(mFiles);
            } else {//本地有完整的记录,未完成的为0,即全部都已完成,保存最新文件版本号
                L.e(TAG, "本地有完整的记录,未完成的为0,即全部都已完成,保存最新文件版本号");
                SPUtils.put(this, Constant.FILE_VERSION, latestVersion);
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
            L.e(TAG, "循环创建下载任务" + mFilesDb.getFileLocalUrl());
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

    final FileDownloadListener queueTarget = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            L.e(TAG, task.getTag().toString());
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
            L.e(TAG, localUrl + " 下载完成");
            try {
                File tmpFile = new File(TMP_PATH + localUrl);
                File targetFile = new File(TARGET_PATH + localUrl);
                SDCardUtils.copyfile(tmpFile, targetFile, true);
                List<Files> mFilesList = DbUtil.getFilesService().queryBuilder().where(FilesDao.Properties.FileLocalUrl.eq(localUrl)).build().list();
                for (Files mFiles : mFilesList) {
                    mFiles.setFileStatus(1);
                    L.e(TAG, localUrl + " 标记为已完成");
                    DbUtil.getFilesService().saveOrUpdate(mFiles);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<Files> mFilesList = DbUtil.getFilesService().queryBuilder().where(FilesDao.Properties.FileStatus.eq(0)).build().list();
            if (mFilesList != null && mFilesList.size() > 0) {//且未完成数大于0,则继续下载
                L.e(TAG, "且未完成数大于0");
            } else {
                L.e(TAG, "下载记录中全部都已完成");
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


    private FileDownloadListener mApkDownloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            L.e(TAG, "pending:" + "已下载：" + soFarBytes + " 文件总大小：" + totalBytes);

        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            L.e(TAG, "connected:" + "已下载：" + soFarBytes + " 文件总大小：" + totalBytes + "  百分比:" + (float) soFarBytes / (float) totalBytes * 100 + "%");
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            L.e(TAG, "progress:" + "已下载：" + soFarBytes + " 文件总大小：" + totalBytes + "  百分比" + (float) soFarBytes / (float) totalBytes * 100 + "%");
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
            L.e("blockComplete:");
        }

        @Override
        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
            L.e(TAG, "progress:" + soFarBytes + "" + ex.toString() + " 已下载:" + soFarBytes);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            L.e("completed:");
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            L.e(TAG, "paused:" + "已下载：" + soFarBytes + " 文件总大小：" + totalBytes + "百分比:" + (float) soFarBytes / (float) totalBytes * 100 + "%");
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            L.e(TAG, "error:" + e.toString());
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            L.e(TAG, "warn:");
        }
    };

}
