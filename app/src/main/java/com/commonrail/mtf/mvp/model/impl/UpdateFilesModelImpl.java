package com.commonrail.mtf.mvp.model.impl;

import android.annotation.SuppressLint;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;
import com.commonrail.mtf.mvp.model.UpdateFileModel;
import com.commonrail.mtf.mvp.model.entity.FileUpload;
import com.commonrail.mtf.mvp.model.entity.Result;
import com.commonrail.mtf.mvp.presenter.OnUpdateFileListener;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.common.Constant;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.common.SPUtils;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/1 下午9:52
 * 修改人：wengyiming
 * 修改时间：16/3/1 下午9:52
 * 修改备注：
 */
public class UpdateFilesModelImpl implements UpdateFileModel {
    @Override
    public void checkFileUpdate (final CompositeSubscription subscription, final RtApi api, final OnUpdateFileListener listener) {
        final int localFileVersion = (int) SPUtils.get(AppClient.getInstance(), Constant.FILE_VERSION, 0);
        HashMap<String, Integer> mHashMap = new HashMap<>();
        mHashMap.put(Constant.FILE_VERSION, localFileVersion);
        L.e("updateFile", mHashMap.toString());
        subscription.add(api.updateFile(mHashMap)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Result<FileUpload>, FileUpload>() {
                    @Override
                    public FileUpload call(Result<FileUpload> t) {
                        L.e("updateFile： " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            GlobalUtils.showToastShort(AppClient.getInstance(), AppClient.getInstance().getString(R.string.net_error));
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
                        if (t == null) {
                            L.e("updateFile", "请求结果为空");
                            listener.onUpdateFilesError();
                            return;
                        }
                        listener.onUpdateFilesSuccess(t);
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
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("updateFile", throwable.toString());
//                        GlobalUtils.showToastShort(MainActivity.this, getString(R.string.net_error));
                        listener.onUpdateFilesError();
                    }
                }));
    }
}
