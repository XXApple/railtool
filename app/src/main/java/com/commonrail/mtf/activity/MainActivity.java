package com.commonrail.mtf.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.commonrail.mtf.R;
import com.commonrail.mtf.adapter.IndexAdapter;
import com.commonrail.mtf.base.BaseActivity;
import com.commonrail.mtf.po.Injector;
import com.commonrail.mtf.po.Result;
import com.commonrail.mtf.po.StepList;
import com.commonrail.mtf.po.Update;
import com.commonrail.mtf.po.User;
import com.commonrail.mtf.util.Api.Config;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.IntentUtils;
import com.commonrail.mtf.util.ReadAndCalculateUtil;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.retrofit.RxUtils;

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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.title_activity_main);
        api = RxUtils.createApi(RtApi.class, Config.BASE_URL);
        doLogin("");
        getIndexList("zh_CN");//"zh_CN";//en_US
        checkUpdate();
        updateFile();


        ReadAndCalculateUtil.init();
        ReadAndCalculateUtil.setReadKey("h2");
        ReadAndCalculateUtil.handleReadValue("7.760");
        L.e("ReadAndCalculateUtil","h2 meas:" + ReadAndCalculateUtil.DATA_MAP.get("h2"));

        ReadAndCalculateUtil.setReadKey("h3");
        ReadAndCalculateUtil.setCalcKey("crin1Formula1");
        ReadAndCalculateUtil.handleReadValue("3.860");
        L.e("ReadAndCalculateUtil","h3 meas:" + ReadAndCalculateUtil.DATA_MAP.get("h3"));
        L.e("ReadAndCalculateUtil","h3 suggest:" + ReadAndCalculateUtil.DATA_MAP.get("crin1Formula1"));
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

    private void checkUpdate() {
        subscription.add(api.appVersion("")
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Result<Update>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void call(Result<Update> t) {
                        L.e("appVersion： " + t.getStatus() + t.getMsg());
                        if (t.getStatus() == 200) {
                            Toast.makeText(getApplicationContext(), t.getMsg(), Toast.LENGTH_SHORT).show();
                            Update update = t.getData();
                            if (update != null) {
                                String vc = update.getAppVersionCode();
                                boolean forced = update.getForced();
                                String url = update.getUrl();
                                L.e(update.toString());
                            }

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

    private void updateFile() {
        subscription.add(api.updateFile("")
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Result<StepList>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void call(Result<StepList> t) {
                        L.e("appVersion： " + t.getStatus() + t.getMsg());
                        if (t.getStatus() == 200) {
                            Toast.makeText(getApplicationContext(), t.getMsg(), Toast.LENGTH_SHORT).show();

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
