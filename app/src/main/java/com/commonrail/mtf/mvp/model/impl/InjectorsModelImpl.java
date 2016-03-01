package com.commonrail.mtf.mvp.model.impl;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;
import com.commonrail.mtf.db.InjectorDb;
import com.commonrail.mtf.mvp.model.InjectorsModel;
import com.commonrail.mtf.mvp.model.entity.Result;
import com.commonrail.mtf.mvp.presenter.OnInjectorsListener;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.Constant;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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
                .map(new Func1<Result<List<InjectorDb>>, List<InjectorDb>>() {
                    @Override
                    public List<InjectorDb> call(Result<List<InjectorDb>> t) {
                        L.e("getIndexList： " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            GlobalUtils.showToastShort(AppClient.getInstance(), AppClient.getInstance().getString(R.string.net_error));
                            return null;
                        }
//                        injectorService.saveOrUpdate(t.getData());
                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
                        return t.getData();
                    }
                })
                .subscribe(new Action1<List<InjectorDb>>() {
                    @Override
                    public void call(final List<InjectorDb> t) {
                        if (t == null) return;
                        if (!t.isEmpty()) {
                            listener.onInjectorsSuccess(t);
//                            fillRvData(t, language);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        listener.onInjectorsError();
//                        GlobalUtils.showToastShort(MainActivity.this, getString(R.string.net_error));
//                        L.e("getIndexList： load from db", injectorService.queryAll().size() + "");
//                        fillRvData(injectorService.queryAll(), language);

                    }
                }));
    }
}
