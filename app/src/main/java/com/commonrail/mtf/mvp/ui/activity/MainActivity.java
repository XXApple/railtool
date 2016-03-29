package com.commonrail.mtf.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;
import com.commonrail.mtf.db.InjectorDb;
import com.commonrail.mtf.mvp.model.entity.User;
import com.commonrail.mtf.mvp.presenter.MainPresenter;
import com.commonrail.mtf.mvp.presenter.impl.MainPresenterIml;
import com.commonrail.mtf.mvp.ui.adapter.IndexAdapter;
import com.commonrail.mtf.mvp.ui.base.BaseActivity;
import com.commonrail.mtf.mvp.ui.view.MainView;
import com.commonrail.mtf.util.Api.Config;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.BlueToothUtils.BluetoothUtils;
import com.commonrail.mtf.util.IntentUtils;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.DateTimeUtil;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.common.SPUtils;
import com.commonrail.mtf.util.db.DbCore;
import com.commonrail.mtf.util.retrofit.RxUtils;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.pgyersdk.crash.PgyCrashManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;


public class MainActivity extends BaseActivity implements MainView, SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.wendu)
    TextView wendu;
    @Bind(R.id.item_list)
    SuperRecyclerView itemList;
    @Bind(R.id.uname)
    TextView uname;
    @Bind(R.id.callFb)
    TextView callFb;
    @Bind(R.id.dateTime)
    TextView dateTime;

    private IndexAdapter mIndexAdapter;
    private MainPresenter mainPresenter;

    @Override
    protected void onResume() {
        super.onResume();
        subscription = RxUtils.getNewCompositeSubIfUnsubscribed(subscription);
        mainPresenter.getUser(subscription, api);
        mainPresenter.getInjectors(subscription, api);
        startFileCheckService();
    }


    @Override
    protected void onPause() {
        super.onPause();
        RxUtils.unsubscribeIfNotNull(subscription);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showLoading() {
        itemList.post(new Runnable() {
            @Override
            public void run() {
                itemList.getSwipeToRefresh().setRefreshing(true);
            }
        });
    }

    @Override
    public void hideLoading() {
        itemList.post(new Runnable() {
            @Override
            public void run() {
                itemList.getSwipeToRefresh().setRefreshing(false);
            }
        });
    }

    @Override
    public void showUserError() {
        uname.setVisibility(View.INVISIBLE);
        Toast.makeText(AppClient.getInstance(), AppClient.getInstance().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
        L.e("showUserError", "获取用户信息失败\n");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setUserInfo(User t) {
        uname.setVisibility(View.VISIBLE);
        String name = t.getUname();
        uname.setText(name + " 你好，欢迎！");
        SPUtils.put(MainActivity.this, "amesdialMac", t.getAmesdialMac());
    }

    @Override
    public void setInjectors(List<InjectorDb> t, final String mMsg) {
        Toast.makeText(MainActivity.this, mMsg, Toast.LENGTH_SHORT).show();
        fillRvData(t);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothUtils.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(MainActivity.this, "请打开蓝牙",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        mainPresenter.getUser(subscription, api);
        mainPresenter.getInjectors(subscription, api);
    }

    private void init() {
        DbCore.enableQueryBuilderLog();
        PgyCrashManager.register(this);
        if (toolbar != null) toolbar.setVisibility(View.GONE);
        dateTime.setText(DateTimeUtil.format(DateTimeUtil.withYearFormat, new Date(System.currentTimeMillis())));
        api = RxUtils.createApi(RtApi.class, Config.BASE_URL);
        mIndexAdapter = new IndexAdapter(new ArrayList<InjectorDb>());
        itemList.setAdapter(mIndexAdapter);
        itemList.setLayoutManager(new GridLayoutManager(this, 3));
        itemList.setRefreshListener(this);
        itemList.setRefreshingColorResources(R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary);

        mainPresenter = new MainPresenterIml(this);
        callFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AppUtils.callPhone(MainActivity.this, callFb.getText().toString().trim());
            }
        });
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothUtils.openBluetooth(MainActivity.this);
            }
        }, 300);

    }

    private void fillRvData(final List<InjectorDb> t) {
        mIndexAdapter.setInjectors(t);
        mIndexAdapter.notifyDataSetChanged();
        mIndexAdapter.setClick(new IndexAdapter.Click() {
            @Override
            public void itemClick(int p) {
                IntentUtils.enterModuleListActivity(MainActivity.this, t.get(p).getInjectorType(), t.get(p).getIconUrl());
            }
        });
    }

    private void startFileCheckService() {
        Intent mIntent = new Intent();
        mIntent.setAction("com.commonrail.mtf.mvp.ui.service.FileCheckService");//你定义的service的action
        mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
        startService(mIntent);
    }
}