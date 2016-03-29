package com.commonrail.mtf.mvp.model.impl.main;

import android.annotation.SuppressLint;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;
import com.commonrail.mtf.mvp.model.UpdateModel;
import com.commonrail.mtf.mvp.model.entity.Result;
import com.commonrail.mtf.mvp.model.entity.Update;
import com.commonrail.mtf.mvp.presenter.listener.mainlistener.OnCheckUpdateListener;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/1 下午9:39
 * 修改人：wengyiming
 * 修改时间：16/3/1 下午9:39
 * 修改备注：
 */
public class UpdateModelImpl implements UpdateModel {
    @Override
    public void checkUpdate(final CompositeSubscription subscription, final RtApi api, final OnCheckUpdateListener listener) {
        subscription.add(api.appVersion("")
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Result<Update>, Update>() {
                    @Override
                    public Update call(Result<Update> t) {
                        L.e("checkUpdate： " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            GlobalUtils.showToastShort(AppClient.getInstance(), AppClient.getInstance().getString(R.string.net_error));
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
                        if (t == null) {
                            listener.onCheckUpdateError();
                            return;
                        }
                        listener.onCheckUpdateSuccess(t);
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
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("checkUpdate" + throwable.toString());
                        listener.onCheckUpdateError();
//                        GlobalUtils.showToastShort(MainActivity.this, getString(R.string.net_error));
                    }
                }));
    }
}
