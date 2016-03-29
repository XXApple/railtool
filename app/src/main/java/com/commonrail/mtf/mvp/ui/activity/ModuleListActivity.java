package com.commonrail.mtf.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commonrail.mtf.R;
import com.commonrail.mtf.mvp.model.entity.Bosch;
import com.commonrail.mtf.mvp.model.entity.Module;
import com.commonrail.mtf.mvp.presenter.ModulePresenter;
import com.commonrail.mtf.mvp.presenter.impl.ModulePresenterImpl;
import com.commonrail.mtf.mvp.ui.adapter.ModuleListAdapter;
import com.commonrail.mtf.mvp.ui.base.BaseActivity;
import com.commonrail.mtf.mvp.ui.view.ModuleView;
import com.commonrail.mtf.util.IntentUtils;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.common.SPUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/1/12 下午8:41
 * 修改人：wengyiming
 * 修改时间：16/1/12 下午8:41
 * 修改备注：
 */
public class ModuleListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, ModuleView {

    @Bind(R.id.home_btn)
    LinearLayout homeBtn;
    @Bind(R.id.item_list)
    SuperRecyclerView itemList;
    @Bind(R.id.tips)
    TextView tips;


    @Bind(R.id.left_tips)
    LinearLayout leftTips;
    @Bind(R.id.left_bosch_tips)
    LinearLayout leftBoschTips;

    @Bind(R.id.injectorTypeEt)
    EditText injectorTypeEt;
    @Bind(R.id.cs)
    TextView cs;
    @Bind(R.id.yzkyh)
    TextView yzkyh;
    @Bind(R.id.yzxh)
    TextView yzxh;
    @Bind(R.id.fzjxh)
    TextView fzjxh;
    @Bind(R.id.injectorTypeImage)
    SimpleDraweeView injectorIcon;

    private ModuleListAdapter mIndexAdapter;
    private boolean isBosch = false;
    private Bosch mBosch = null;
    private String injectorType;
    private int moduleId = 0;
    private String moduleName = "";
    private String xh = "";
    private ModulePresenter mModulePresenter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        mModulePresenter = new ModulePresenterImpl(this);
        if (toolbar != null) {
            toolbar.setVisibility(View.GONE);
            toolbar.setTitle(R.string.app_name);
            toolbar.setSubtitle(R.string.title_activity_main);
            tips.setText(R.string.module_list_tips);
        }
        injectorTypeEt.clearFocus();
        injectorType = getIntent().getStringExtra("injectorType");
        String injectorIconUrl = getIntent().getStringExtra("injectorIcon");
        injectorIcon.setImageURI(AppUtils.getFileFrescoUri(injectorIconUrl));
        mIndexAdapter = new ModuleListAdapter(new ArrayList<Module>());
        itemList.setAdapter(mIndexAdapter);
        itemList.setLayoutManager(new LinearLayoutManager(ModuleListActivity.this));
        itemList.setRefreshListener(ModuleListActivity.this);
        itemList.setRefreshingColorResources(R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary);
        initBoschInfoView(injectorType);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_module_list;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mModulePresenter.getModuleList(subscription, api, injectorType);
    }

    private void initBoschInfoView(final String mInjectorType) {
        if (TextUtils.equals(mInjectorType, "Bosch")) {
            isBosch = true;
            leftTips.setVisibility(View.GONE);
            leftBoschTips.setVisibility(View.VISIBLE);
            injectorTypeEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                }

                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    if (s.length() == 10) {
                        getBoschInfo(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(final Editable s) {
                }
            });
        } else {
            isBosch = false;
            leftTips.setVisibility(View.VISIBLE);
            leftBoschTips.setVisibility(View.GONE);
        }
    }

    private void getBoschInfo(final String xh) {
        mModulePresenter.getBochInfo(subscription, api, xh);
    }

    @OnClick(R.id.home_btn)
    public void homeBtn(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK) {
                String mDeviceName = data.getStringExtra(Step2Activity.EXTRAS_DEVICE_NAME);
                String mDeviceAddress = data.getStringExtra(Step2Activity.EXTRAS_DEVICE_ADDRESS);
                if (!TextUtils.isEmpty(mDeviceName)) {
                    toolbar.setSubtitle(mDeviceName);
                }
                String servcerDeviceAddress = (String) SPUtils.get(ModuleListActivity.this, "amesdialMac", "");
                L.e("扫描的蓝牙设备:", mDeviceAddress);
                L.e("服务器配置的蓝牙设备:", servcerDeviceAddress);
                IntentUtils.enterStep2Activity(ModuleListActivity.this, injectorType, moduleId, moduleName, xh, mDeviceAddress);
            }
        }
    }

    @Override
    public void onRefresh() {
        mModulePresenter.getModuleList(subscription, api, injectorType);
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
    public void showModuleListError(String error) {
    }

    @Override
    public void showBoschInfoError(String error) {

    }

    @Override
    public void setBoschInfo(Bosch t) {
        GlobalUtils.hideKeyboard(ModuleListActivity.this, cs);
        mBosch = t;
        cs.setText(mBosch.getCs());
        yzkyh.setText(mBosch.getYzkyh());
        yzxh.setText(mBosch.getYzxh());
        fzjxh.setText(mBosch.getFzjxh());
    }

    @Override
    public void setModuleList(final List<Module> t) {
        mIndexAdapter.setInjectors((ArrayList<Module>) t);
        mIndexAdapter.setClick(new ModuleListAdapter.Click() {
            @Override
            public void itemClick(int p) {
                Module mModule = t.get(p);
                if (isBosch) {
                    if (mBosch != null) {
                        xh = mBosch.getXh();
                    }
                }
                moduleName = mModule.getModuleName();
                moduleId = mModule.getId();
                IntentUtils.enterStep2Activity(ModuleListActivity.this,
                        injectorType, moduleId,
                        moduleName,
                        xh,
                        (String) SPUtils.get(ModuleListActivity.this, "amesdialMac", ""));
            }
        });
        mIndexAdapter.notifyDataSetChanged();
    }
}
