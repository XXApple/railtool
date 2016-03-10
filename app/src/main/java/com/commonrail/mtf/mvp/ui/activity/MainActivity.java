package com.commonrail.mtf.mvp.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;
import com.commonrail.mtf.db.Files;
import com.commonrail.mtf.db.FilesDao;
import com.commonrail.mtf.db.InjectorDb;
import com.commonrail.mtf.mvp.model.entity.FileListItem;
import com.commonrail.mtf.mvp.model.entity.FileUpload;
import com.commonrail.mtf.mvp.model.entity.Update;
import com.commonrail.mtf.mvp.model.entity.User;
import com.commonrail.mtf.mvp.presenter.MainPresenter;
import com.commonrail.mtf.mvp.presenter.impl.MainPresenterIml;
import com.commonrail.mtf.mvp.ui.adapter.IndexAdapter;
import com.commonrail.mtf.mvp.ui.base.BaseActivity;
import com.commonrail.mtf.mvp.ui.view.MainView;
import com.commonrail.mtf.util.Api.Config;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.IntentUtils;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.ChannelUtil;
import com.commonrail.mtf.util.common.Constant;
import com.commonrail.mtf.util.common.DateTimeUtil;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.common.NetUtils;
import com.commonrail.mtf.util.common.SDCardUtils;
import com.commonrail.mtf.util.common.SPUtils;
import com.commonrail.mtf.util.db.DbCore;
import com.commonrail.mtf.util.db.DbUtil;
import com.commonrail.mtf.util.db.FilesService;
import com.commonrail.mtf.util.db.InjectorService;
import com.commonrail.mtf.util.retrofit.RxUtils;
import com.yw.filedownloader.BaseDownloadTask;
import com.yw.filedownloader.FileDownloadListener;
import com.yw.filedownloader.FileDownloadQueueSet;
import com.yw.filedownloader.FileDownloader;
import com.yw.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements MainView {

    @Bind(R.id.wendu)
    TextView wendu;
    @Bind(R.id.item_list)
    RecyclerView itemList;
    @Bind(R.id.uname)
    TextView uname;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.callFb)
    TextView callFb;
    @Bind(R.id.dateTime)
    TextView dateTime;


    private Dialog loadingDialog;
    private IndexAdapter mIndexAdapter;
    private final static String TMP_PATH = SDCardUtils.getSDCardPath() + File.separator + "Download" + File.separator + "railTool" + File.separator;
    private final static String TARGET_PATH = SDCardUtils.getSDCardPath() + File.separator;
    private FilesService filesService;
    private InjectorService injectorService;


    private MainPresenter mainPresenter;

    @Override
    protected void onResume() {
        super.onResume();
        subscription = RxUtils.getNewCompositeSubIfUnsubscribed(subscription);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RxUtils.unsubscribeIfNotNull(subscription);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.title_activity_main);
        dateTime.setText(DateTimeUtil.format(DateTimeUtil.withYearFormat, new Date(System.currentTimeMillis())));
        DbCore.enableQueryBuilderLog();
        filesService = DbUtil.getFilesService();
        injectorService = DbUtil.getInjectorService();
        api = RxUtils.createApi(RtApi.class, Config.BASE_URL);

        mIndexAdapter = new IndexAdapter(new ArrayList<InjectorDb>());
        itemList.setAdapter(mIndexAdapter);
        mainPresenter = new MainPresenterIml(this);
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("加载数据...");
        mainPresenter.getUser(subscription, api);
        mainPresenter.getInjectors(subscription, api);
        mainPresenter.checkUpdate(subscription, api);
        mainPresenter.updateFile(subscription, api);
//        doLogin("");
//        getIndexList("zh_CN");//"zh_CN";//en_US
//        checkUpdate();
//        updateFile();
        callFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AppUtils.callPhone(MainActivity.this, callFb.getText().toString().trim());
            }
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showLoading() {
        loadingDialog.show();
    }

    @Override
    public void hideLoading() {
        loadingDialog.hide();
    }

    @Override
    public void showUserError() {
        uname.setVisibility(View.INVISIBLE);
        Toast.makeText(AppClient.getInstance(), AppClient.getInstance().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
        L.e("showUserError", "获取用户信息失败");
        GlobalUtils.showToastShort(this, "获取用户信息失败");
    }

    @Override
    public void showInjectorsError() {
        Toast.makeText(AppClient.getInstance(), AppClient.getInstance().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
        L.e("showInjectorsError", "查找设备无结果");
        GlobalUtils.showToastShort(this, "查找设备型号无结果");
        L.e("从缓存数据库中加载", injectorService.queryAll().size() + "");
        fillRvData(injectorService.queryAll());
    }

    @Override
    public void showCheckUpdaterError() {
//        Toast.makeText(AppClient.getInstance(), "当前app是最新版本", Toast.LENGTH_SHORT).show();
        L.e("showCheckUpdaterError", "无更新");
    }

    @Override
    public void showUpdateFileError() {
//        Toast.makeText(AppClient.getInstance(), "当前数据是最新版本", Toast.LENGTH_SHORT).show();
        GlobalUtils.showToastShort(this, "文件版本是最新的");
        L.e("showUpdateFileError", "无新版文件");

    }

    @Override
    public void setUserInfo(User t) {
        uname.setVisibility(View.VISIBLE);
        String name = t.getUname();
        uname.setText(name + "你好，欢迎！");
        SPUtils.put(MainActivity.this, "amesdialMac", t.getAmesdialMac());
    }

    @Override
    public void setInjectors(List<InjectorDb> t) {
        injectorService.saveOrUpdate(t);
        fillRvData(t);
    }

    @Override
    public void checkUpdate(Update t) {
        if (t == null) return;
        final String vc = t.getAppVersionCode();
        boolean forced = t.getForced();
        final String url = t.getUrl();
        L.e(t.toString());
        if (!AppUtils.checkVersion(vc)) return;
        GlobalUtils.ShowDialog(MainActivity.this, "提示", "发现新版本，是否更新", !forced, new DialogInterface.OnClickListener() {
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
    public void updateFile(FileUpload t) {
        if (t == null) {
            L.e("updateFile", "请求结果为空");
            return;
        }
        int localFileVersion = (int) SPUtils.get(this, Constant.FILE_VERSION, 0);
        if (localFileVersion == 0) {
            localFileVersion = Integer.parseInt(ChannelUtil.getChannel(this));
        }
        L.e("updateFile", "请求结果不为空" + t.toString());
        //wifi网络下自动下载最新图片和视频资源
        if (NetUtils.isWifi(MainActivity.this)) {
            if (t.getFileList() == null || t.getFileList().isEmpty()) {
                L.e("updateFile", " 文件列表为空,当前版本即最新版本:" + localFileVersion + " 最新文件版本号为:" + t.getVersionCode());
                return;
            }
            L.e("updateFile", "本地文件版本号:" + localFileVersion + " 服务器文件版本号" + t.getVersionCode());
            if (localFileVersion < t.getVersionCode()) {//如果服务器文件版本大于本地文件版本,则有新的更新
                L.e("updateFile", "发现新的文件");
                downloadFiles(t.getFileList(), t.getVersionCode());
            }

        }
    }

    private void fillRvData(final List<InjectorDb> t) {
        mIndexAdapter.setInjectors(t);
        mIndexAdapter.notifyDataSetChanged();
        mIndexAdapter.setClick(new IndexAdapter.Click() {
            @Override
            public void itemClick(int p) {
                IntentUtils.enterModuleListActivity(MainActivity.this, t.get(p).getInjectorType(), t.get(p).getIconUrl());
            }
        });
    }


    private void downloadApkAndUpdate(String url, String savePath) {
        FileDownloader.getImpl().create(url)
                .setPath(savePath)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        L.e("pending:" + "已下载：" + soFarBytes + " 文件总大小：" + totalBytes);

                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        L.e("connected:" + "已下载：" + soFarBytes + " 文件总大小：" + totalBytes + "  百分比:" + (float) soFarBytes / (float) totalBytes * 100 + "%");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        L.e("progress:" + "已下载：" + soFarBytes + " 文件总大小：" + totalBytes + "  百分比" + (float) soFarBytes / (float) totalBytes * 100 + "%");
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        L.e("blockComplete:");
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        L.e("progress:" + soFarBytes + "" + ex.toString() + " 已下载:" + soFarBytes);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        L.e("completed:");
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        L.e("paused:" + "已下载：" + soFarBytes + " 文件总大小：" + totalBytes + "百分比:" + (float) soFarBytes / (float) totalBytes * 100 + "%");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        L.e("error:" + e.toString());
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        L.e("warn:");
                    }
                }).start();
    }


    private void downloadFiles(final List<FileListItem> fileList, int latestVersion) {
        L.e("downloadFiles", "服务器需要下载文件数" + fileList.size());
        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(queueTarget);
        final List<BaseDownloadTask> tasks = new ArrayList<>();

        List<Files> mFilesDbQuene = filesService.queryAll();
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
                filesService.save(localFile);
            }
        } else {//如果本地有未完成的记录,将未完成的任务加入待下载队列
            List mFiles = filesService.queryBuilder().where(FilesDao.Properties.FileStatus.eq(0)).build().list();
            if (mFiles != null && mFiles.size() > 0) {//且未完成数大于0,则继续下载
                L.e("downloadFiles", "且未完成数大于0,则继续下载");
                mFileTmpQuene.addAll(mFiles);
            } else {//本地有完整的记录,未完成的为0,即全部都已完成,保存最新文件版本号
                L.e("downloadFiles", "本地有完整的记录,未完成的为0,即全部都已完成,保存最新文件版本号");
                SPUtils.put(MainActivity.this, Constant.FILE_VERSION, latestVersion);
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
                List<Files> mFilesList = filesService.queryBuilder().where(FilesDao.Properties.FileLocalUrl.eq(localUrl)).build().list();
                for (Files mFiles : mFilesList) {
                    mFiles.setFileStatus(1);
                    L.e("completed", localUrl + " 标记为已完成");
                    filesService.saveOrUpdate(mFiles);
//                    filesService.refresh(mFiles);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<Files> mFilesList = filesService.queryBuilder().where(FilesDao.Properties.FileStatus.eq(0)).build().list();
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


//    private void updateFile() {
//        final int localFileVersion = (int) SPUtils.get(this, Constant.FILE_VERSION, 0);
//        HashMap<String, Integer> mHashMap = new HashMap<>();
//        mHashMap.put(Constant.FILE_VERSION, localFileVersion);
//        L.e("updateFile", mHashMap.toString());
//        subscription.add(api.updateFile(mHashMap)
//                .observeOn(Schedulers.io())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Func1<Result<FileUpload>, FileUpload>() {
//                    @Override
//                    public FileUpload call(Result<FileUpload> t) {
//                        L.e("updateFile： " + t.getStatus() + t.getMsg());
//                        if (t.getStatus() != 200) {
//                            GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
//                            return null;
//                        }
//                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
//                        return t.getData();
//                    }
//                })
//                .subscribe(new Action1<FileUpload>() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void call(FileUpload t) {
//                        if (t == null) {
//                            L.e("updateFile", "请求结果为空");
//                            return;
//                        }
//                        L.e("updateFile", "请求结果不为空" + t.toString());
//                        //wifi网络下自动下载最新图片和视频资源
//                        if (NetUtils.isWifi(MainActivity.this)) {
//                            if (t.getFileList() == null || t.getFileList().isEmpty()) {
//                                L.e("updateFile", " 文件列表为空,当前版本即最新版本:" + localFileVersion + " 最新文件版本号为:" + t.getVersionCode());
//                                return;
//                            }
//                            L.e("updateFile", "本地文件版本号:" + localFileVersion + " 服务器文件版本号" + t.getVersionCode());
//                            if (localFileVersion < t.getVersionCode()) {//如果服务器文件版本大于本地文件版本,则有新的更新
//                                L.e("updateFile", "发现新的文件");
//                                downloadFiles(t.getFileList(), t.getVersionCode());
//                            }
//
//                        }
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        L.e("updateFile", throwable.toString());
//                        GlobalUtils.showToastShort(MainActivity.this, getString(R.string.net_error));
//                    }
//                }));
//    }


//    private void doLogin(final String username) {
//        subscription.add(api.getUserInfo(username)
//                .observeOn(Schedulers.io())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Func1<Result<User>, User>() {
//                    @Override
//                    public User call(Result<User> t) {
//                        L.e("getUserInfo： " + t.getStatus() + t.getMsg());
//                        if (t.getStatus() != 200) {
//                            GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
//                            return null;
//                        }
//
//                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
//                        return t.getData();
//                    }
//                })
//                .subscribe(new Action1<User>() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void call(User t) {
//                        if (t == null) return;
//                        String name = t.getUname();
//                        uname.setText(name + "你好，欢迎！");
//                        SPUtils.put(MainActivity.this,"amesdialMac",t.getAmesdialMac());
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        L.e("" + throwable.toString());
//                        GlobalUtils.showToastShort(MainActivity.this, getString(R.string.net_error));
//                    }
//                }));
//    }
//
//
//    private void getIndexList(final String language) {
//        subscription.add(api.getIndexList(AppUtils.getMap("language", language))
//                .observeOn(Schedulers.io())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Func1<Result<List<InjectorDb>>, List<InjectorDb>>() {
//                    @Override
//                    public List<InjectorDb> call(Result<List<InjectorDb>> t) {
//                        L.e("getIndexList： " + t.getStatus() + t.getMsg());
//                        if (t.getStatus() != 200) {
//                            GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
//                            return null;
//                        }
//                        injectorService.saveOrUpdate(t.getData());
//                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
//                        return t.getData();
//                    }
//                })
//                .subscribe(new Action1<List<InjectorDb>>() {
//                    @Override
//                    public void call(final List<InjectorDb> t) {
//                        if (t == null) return;
//                        if (!t.isEmpty()) {
//                            fillRvData(t);
//                        }
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        L.e("" + throwable.toString());
//                       
//
//                    }
//                }));
//    }


//    private void checkUpdate() {
//        subscription.add(api.appVersion("")
//                .observeOn(Schedulers.io())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(new Func1<Result<Update>, Update>() {
//                    @Override
//                    public Update call(Result<Update> t) {
//                        L.e("checkUpdate： " + t.getStatus() + t.getMsg());
//                        if (t.getStatus() != 200) {
//                            GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
//                            return null;
//                        }
//                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
//                        return t.getData();
//                    }
//                })
//                .subscribe(new Action1<Update>() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void call(Update t) {
//                        if (t == null) return;
//                        final String vc = t.getAppVersionCode();
//                        boolean forced = t.getForced();
//                        final String url = t.getUrl();
//                        L.e(t.toString());
//                        if (!AppUtils.checkVersion(vc)) return;
//                        GlobalUtils.ShowDialog(MainActivity.this, "提示", "发现新版本，是否更新", !forced, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                //download and update
//                                String savePath1 = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "railtool" + vc + ".apk";
//                                downloadApkAndUpdate(url, savePath1);
//                            }
//                        }, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (dialog != null) dialog.dismiss();
//                            }
//                        });
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        L.e("checkUpdate" + throwable.toString());
//                        GlobalUtils.showToastShort(MainActivity.this, getString(R.string.net_error));
//                    }
//                }));
//    }
//    


}
