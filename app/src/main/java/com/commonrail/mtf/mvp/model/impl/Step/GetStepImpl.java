package com.commonrail.mtf.mvp.model.impl.Step;

import com.commonrail.mtf.mvp.model.GetStepModel;
import com.commonrail.mtf.mvp.model.entity.Result;
import com.commonrail.mtf.mvp.model.entity.StepList;
import com.commonrail.mtf.mvp.presenter.listener.StepListener.OnRepairStepListener;
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
public class GetStepImpl implements GetStepModel {
    @Override
    public void loadStep(final CompositeSubscription subscription, RtApi api, HashMap<String, Object> map, final OnRepairStepListener listener) {
        subscription.add(api.getRepairStep(map)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Result<StepList>, StepList>() {
                    @Override
                    public StepList call(Result<StepList> t) {
                        L.e("getRepairStep " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            listener.onLoadRepairStepError(t.getMsg());
                            subscription.unsubscribe();
                            return null;
                        }
                        return t.getData();
                    }
                })
                .subscribe(new Action1<StepList>() {
                    @Override
                    public void call(StepList mStepList) {
                        if (mStepList != null) {
                            listener.onLoadRepairStepSuccess(mStepList);
                        } else {
                            listener.onLoadRepairStepError("");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        listener.onLoadRepairStepError(throwable.toString());
                    }
                }));
    }
}
