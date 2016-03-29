package com.commonrail.mtf.mvp.model.impl.module;

import android.widget.Toast;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;
import com.commonrail.mtf.mvp.model.ModuleListModel;
import com.commonrail.mtf.mvp.model.entity.Module;
import com.commonrail.mtf.mvp.model.entity.Result;
import com.commonrail.mtf.mvp.presenter.listener.modulelistener.OnModuleListListener;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.common.Constant;
import com.commonrail.mtf.util.common.L;

import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by wengyiming on 2016/3/29.
 */
public class ModuleListModelImpl implements ModuleListModel {
    @Override
    public void loadModulist(final CompositeSubscription subscription, RtApi api, String injectorType, final OnModuleListListener listener) {
        HashMap<String, String> map = new HashMap<>();
        map.put("injectorType", injectorType);
        map.put("language", Constant.LANGUAGE);
        L.e(map.toString());
        subscription.add(api.getModuleList(map)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Result<List<Module>>, List<Module>>() {
                    @Override
                    public List<Module> call(Result<List<Module>> t) {
                        L.e("getModuleList " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            listener.onLoadModuleListError(t.getMsg());
                            subscription.unsubscribe();
                            return null;
                        }
                        Toast.makeText(AppClient.getInstance(), t.getMsg(), Toast.LENGTH_SHORT).show();
                        return t.getData();
                    }
                })
                .subscribe(new Action1<List<Module>>() {
                    @Override
                    public void call(final List<Module> t) {
                        if (t == null || t.isEmpty()) {
                            Toast.makeText(AppClient.getInstance(), AppClient.getInstance().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                            listener.onLoadModuleListError("");
                        } else {
                            listener.onLoadModuleListSuccess(t);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        listener.onLoadModuleListError(throwable.toString());
                    }
                }));
    }
}
