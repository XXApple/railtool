package com.commonrail.mtf.mvp.model.impl.main;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;
import com.commonrail.mtf.db.InjectorDb;
import com.commonrail.mtf.mvp.model.InjectorsModel;
import com.commonrail.mtf.mvp.model.entity.Result;
import com.commonrail.mtf.mvp.presenter.listener.mainlistener.OnInjectorsListener;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.Constant;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.db.DbUtil;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/3/1 下午9:15
 * 修改人：wengyiming
 * 修改时间：16/3/1 下午9:15
 * 修改备注：
 */
public class InjectorsModelImpl implements InjectorsModel {
    @Override
    public void loadInjectors(final CompositeSubscription subscription, final RtApi api, final OnInjectorsListener listener) {
        subscription.add(api.getIndexList(AppUtils.getMap("language", Constant.LANGUAGE))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Result<List<InjectorDb>>>() {
                    @Override
                    public void call(final Result<List<InjectorDb>> t) {
                        if (t == null) {
                            //请求结果为null,取缓存
                            getFromDbCache(listener, AppClient.getInstance().getString(R.string.net_error));
                            return;
                        }
                        L.e("getIndexList： " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200 || t.getData() == null || t.getData().isEmpty()) {
                            //code不正确或者数据为空,取缓存返回服务器消息
                            getFromDbCache(listener, t.getMsg());
                            return;
                        }
                        //请求数据正常,刷新本地缓存,返回服务器消息
                        listener.onInjectorsResult(t.getData(), t.getMsg());
                        DbUtil.getInjectorService().saveOrUpdate(t.getData());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        //所有的异常情况,取缓存,返回异常消息
                        getFromDbCache(listener, throwable.toString());
                    }
                }));
    }

    private void getFromDbCache(final OnInjectorsListener listener, String msg) {
        L.e("从缓存数据库中加载", DbUtil.getInjectorService().queryAll().size() + "");
        listener.onInjectorsResult(DbUtil.getInjectorService().queryAll(), msg);
    }
}
