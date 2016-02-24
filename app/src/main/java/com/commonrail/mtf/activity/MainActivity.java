package com.commonrail.mtf.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;
import com.commonrail.mtf.adapter.IndexAdapter;
import com.commonrail.mtf.base.BaseActivity;
import com.commonrail.mtf.po.FileListItem;
import com.commonrail.mtf.po.FileUpload;
import com.commonrail.mtf.po.Injector;
import com.commonrail.mtf.po.Result;
import com.commonrail.mtf.po.Update;
import com.commonrail.mtf.po.User;
import com.commonrail.mtf.util.Api.Config;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.IntentUtils;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.DateTimeUtil;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.common.NetUtils;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

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


    private IndexAdapter mIndexAdapter;

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
        api = RxUtils.createApi(RtApi.class, Config.BASE_URL);
        doLogin("");
        getIndexList("zh_CN");//"zh_CN";//en_US
        checkUpdate();
        updateFile();
        callFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AppUtils.callPhone(MainActivity.this, callFb.getText().toString().trim());
            }
        });

        final float scale = getActivity().getResources().getDisplayMetrics().density;
        L.e("scale:" + scale + "");

//        String url = "http://dl.game.qidian.com/apknew/game/dzz/dzz.apk";
//        String savePath1 = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "tmp1";
//        L.e("savePath"+savePath1);
//        downloadApkAndUpdate(url, savePath1);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public Activity getActivity() {
        return this;
    }


    private void doLogin(final String username) {
        subscription.add(api.getUserInfo(username)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Result<User>, User>() {
                    @Override
                    public User call(Result<User> t) {
                        L.e("getUserInfo： " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
                            return null;
                        }
                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
                        return t.getData();
                    }
                })
                .subscribe(new Action1<User>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void call(User t) {
                        if (t == null) return;
                        String name = t.getUname();
                        uname.setText(name + "你好，欢迎！");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        GlobalUtils.showToastShort(MainActivity.this, getString(R.string.net_error));
                    }
                }));
    }


    private void getIndexList(final String language) {
        subscription.add(api.getIndexList(AppUtils.getMap("language", language))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Result<List<Injector>>, List<Injector>>() {
                    @Override
                    public List<Injector> call(Result<List<Injector>> t) {
                        L.e("getUserInfo： " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
                            return null;
                        }
                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
                        return t.getData();
                    }
                })
                .subscribe(new Action1<List<Injector>>() {
                    @Override
                    public void call(final List<Injector> t) {
                        if (!t.isEmpty()) {
                            mIndexAdapter = new IndexAdapter(t);
                            itemList.setAdapter(mIndexAdapter);
                            mIndexAdapter.setClick(new IndexAdapter.Click() {
                                @Override
                                public void itemClick(int p) {
                                    IntentUtils.enterModuleListActivity(MainActivity.this, t.get(p).getInjectorType(), language);
                                }
                            });
                            mIndexAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        GlobalUtils.showToastShort(MainActivity.this, getString(R.string.net_error));
                    }
                }));
    }

    private void checkUpdate() {
        subscription.add(api.appVersion("")
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Result<Update>, Update>() {
                    @Override
                    public Update call(Result<Update> t) {
                        L.e("getUserInfo： " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
                            return null;
                        }
                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
                        return t.getData();
                    }
                })
                .subscribe(new Action1<Update>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void call(Update t) {
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
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        GlobalUtils.showToastShort(MainActivity.this, getString(R.string.net_error));
                    }
                }));
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

    private void updateFile() {
        subscription.add(api.updateFile("")
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Result<FileUpload>, FileUpload>() {
                    @Override
                    public FileUpload call(Result<FileUpload> t) {
                        L.e("getUserInfo： " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
                            return null;
                        }
                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
                        return t.getData();
                    }
                })
                .subscribe(new Action1<FileUpload>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void call(FileUpload t) {
                        if (t != null) {
//                            if(t.getVersionCode()){
//                            }
                            //wifi网络下自动下载最新图片和视频资源
                            if (NetUtils.isWifi(MainActivity.this)) {
                                downloadFiles(t.getFileList());
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        GlobalUtils.showToastShort(MainActivity.this, getString(R.string.net_error));
                    }
                }));
    }

    private void downloadFiles(final List<FileListItem> fileList) {
        final FileDownloadListener queueTarget = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
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
                final int index = (int) task.getTag();
                FileListItem fileListItem = fileList.get(index);
                String fileType = fileListItem.getFileType();
                String filePath = task.getPath();
                /**
                 * TODO 将下载完成的对应的文件按照类型拷贝至对应文件夹下，待开发
                 *
                 */
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

        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(queueTarget);

        final List<BaseDownloadTask> tasks = new ArrayList<>();

        String URLS[] = new String[fileList.size()];
        for (int i = 0; i < fileList.size(); i++) {
            URLS[i] = fileList.get(0).getUrl();
        }
        String filePath = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "railtool" + File.separator;
        for (int i = 0; i < URLS.length; i++) {
            tasks.add(FileDownloader.getImpl().create(URLS[i]).setTag(i).setPath(filePath));
        }
        queueSet.disableCallbackProgressTimes(); // do not need for each task callback `FileDownloadListener#progress`,
// we just consider which task will complete. so in this way reduce ipc will be effective optimization

// each task will auto retry 1 time if download fail
        queueSet.setAutoRetryTimes(1);
        queueSet.downloadSequentially(tasks);//队列一个一个下载
        queueSet.downloadTogether(tasks);//同时下载
        queueSet.start();
    }


}
