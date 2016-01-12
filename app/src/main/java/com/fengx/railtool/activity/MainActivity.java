package com.fengx.railtool.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.fengx.railtool.R;
import com.fengx.railtool.adapter.IndexAdapter;
import com.fengx.railtool.base.BaseActivity;
import com.fengx.railtool.po.Injector;
import com.fengx.railtool.po.Result;
import com.fengx.railtool.po.User;
import com.fengx.railtool.util.Api.Config;
import com.fengx.railtool.util.Api.RtApi;
import com.fengx.railtool.util.IntentUtils;
import com.fengx.railtool.util.common.AppUtils;
import com.fengx.railtool.util.common.GlobalUtils;
import com.fengx.railtool.util.common.L;
import com.fengx.railtool.util.retrofit.RxUtils;

import java.util.List;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends BaseActivity {

    @Bind(R.id.wendu)
    TextView wendu;
    @Bind(R.id.item_list)
    RecyclerView itemList;
    @Bind(R.id.uname)
    TextView uname;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private RtApi api;
    private IndexAdapter mIndexAdapter;
    private CompositeSubscription subscription = new CompositeSubscription();


    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.title_activity_main);
        api = RxUtils.createApi(RtApi.class, Config.BASE_URL);
        doLogin("");
        getIndexList("zh_CN");//"zh_CN";//en_US

    }

    private void doLogin(final String username) {
        subscription.add(api.getUserInfo(username)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Result<User>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void call(Result<User> t) {
                        L.e("getUserInfo： " + t.getStatus() + t.getMsg());
                        if (t.getStatus() == 200) {
                            Toast.makeText(getApplicationContext(), t.getMsg(), Toast.LENGTH_SHORT).show();
                            String name = t.getData().getUname();
                            uname.setText(t.getData().getUname() + "你好，欢迎！");
                            L.e("uname: ", name);
                        } else {
                            uname.setText(t.getMsg());
                            GlobalUtils.showToastShort(MainActivity.this, getString(R.string.net_error));
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


    private void getIndexList(final String language) {
        subscription.add(api.getIndexList(AppUtils.getMap("language", language))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Result<List<Injector>>>() {
                    @Override
                    public void call(final Result<List<Injector>> t) {
                        L.e("getIndexList " + t.getStatus() + t.getMsg());
                        if (t.getStatus() == 200) {
                            Toast.makeText(getApplicationContext(), t.getMsg(), Toast.LENGTH_SHORT).show();
                            mIndexAdapter = new IndexAdapter(t.getData());
                            itemList.setAdapter(mIndexAdapter);
                            mIndexAdapter.setClick(new IndexAdapter.Click() {
                                @Override
                                public void itemClick(int p) {
                                    IntentUtils.enterModuleListActivity(MainActivity.this, t.getData().get(p).getInjectorType(), language);
                                }
                            });
                            mIndexAdapter.notifyDataSetChanged();
                        } else {
                            GlobalUtils.showToastShort(MainActivity.this, getString(R.string.net_error));
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


}
