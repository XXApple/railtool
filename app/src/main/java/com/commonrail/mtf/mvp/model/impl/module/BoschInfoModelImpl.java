package com.commonrail.mtf.mvp.model.impl.module;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.mvp.model.BoschInfoModel;
import com.commonrail.mtf.mvp.model.entity.Bosch;
import com.commonrail.mtf.mvp.model.entity.Result;
import com.commonrail.mtf.mvp.presenter.listener.modulelistener.OnBoschInfoListener;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/29.
 */
public class BoschInfoModelImpl implements BoschInfoModel {
    @Override
    public void loadBoschInfo(final CompositeSubscription subscription, RtApi api, String xh, final OnBoschInfoListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("xh", xh);
        subscription.add(api.searchBosch(map)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Result<Bosch>, Bosch>() {
                    @Override
                    public Bosch call(Result<Bosch> t) {
                        L.e("searchBosch： " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            listener.onLoadBoschInfoError(t.getMsg());
                            subscription.unsubscribe();
                            return null;
                        }
                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
                        return t.getData();
                    }
                })
                .subscribe(new Action1<Bosch>() {
                    @Override
                    public void call(Bosch t) {
                        if (t != null) {
                            listener.onLoadBoschInfoSuccess(t);
                        } else {
                            listener.onLoadBoschInfoError("");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        listener.onLoadBoschInfoError(throwable.toString());
                    }
                }));
    }
}
