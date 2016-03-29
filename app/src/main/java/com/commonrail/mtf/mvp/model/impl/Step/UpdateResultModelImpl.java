package com.commonrail.mtf.mvp.model.impl.Step;

import com.commonrail.mtf.mvp.model.UpdateResultModel;
import com.commonrail.mtf.mvp.model.entity.Result;
import com.commonrail.mtf.mvp.presenter.listener.StepListener.OnUpdateStepResultListener;
import com.commonrail.mtf.util.Api.RtApi;
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
public class UpdateResultModelImpl implements UpdateResultModel {
    @Override
    public void updateStepResult(final CompositeSubscription subscription, RtApi api, HashMap<String, Object> map, final OnUpdateStepResultListener listener) {
        subscription.add(api.uploadMesResult(map)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Result<String>, String>() {
                    @Override
                    public String call(Result<String> t) {
                        L.e("uploadMesResult " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            return null;
                        }
                        return t.getData();
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String t) {
                        if (t != null) {
                            listener.onUpdateStepResultSuccess(t);
                        } else {
                            listener.onUpdateStepResultError("");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        listener.onUpdateStepResultError(throwable.toString());
                    }
                }));
    }
}
