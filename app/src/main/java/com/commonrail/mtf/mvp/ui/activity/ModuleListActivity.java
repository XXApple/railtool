package com.commonrail.mtf.mvp.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
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
import android.widget.Toast;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;
import com.commonrail.mtf.mvp.model.entity.Bosch;
import com.commonrail.mtf.mvp.model.entity.Module;
import com.commonrail.mtf.mvp.model.entity.Result;
import com.commonrail.mtf.mvp.ui.adapter.ModuleListAdapter;
import com.commonrail.mtf.mvp.ui.base.BaseActivity;
import com.commonrail.mtf.util.IntentUtils;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.Constant;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.common.SPUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/1/12 下午8:41
 * 修改人：wengyiming
 * 修改时间：16/1/12 下午8:41
 * 修改备注：
 */
public class ModuleListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

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


    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;


    private ModuleListAdapter mIndexAdapter;
    private boolean isBosch = false;
    private Bosch mBosch = null;
    private String injectorType;
    private int moduleId = 0;
    private String moduleName = "";
    private String xh = "";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        initBoschInfoView(injectorType);
        
        
        mIndexAdapter = new ModuleListAdapter(new ArrayList<Module>());
        itemList.setAdapter(mIndexAdapter);

        itemList.setLayoutManager(new LinearLayoutManager(ModuleListActivity.this));
        itemList.setRefreshListener(ModuleListActivity.this);
        itemList.setRefreshingColorResources(R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(ModuleListActivity.this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
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

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        //弹窗申请打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        getModuleList(injectorType);
      
    }

    private void getModuleList(final String injectorType) {
        HashMap<String, String> map = new HashMap<>();
        map.put("injectorType", injectorType);
        map.put("language", Constant.LANGUAGE);
        this.injectorType = injectorType;
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
                            return;
                        }

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
//                                Intent intent = new Intent(ModuleListActivity.this, DeviceScanActivity.class);
//
//                                ModuleListActivity.this.startActivityForResult(intent, 0);
                                if (!mBluetoothAdapter.isEnabled()) {
                                    Toast.makeText(ModuleListActivity.this, "请务必打开蓝牙",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                IntentUtils.enterStep2Activity(ModuleListActivity.this,
                                        injectorType, moduleId,
                                        moduleName,
                                        xh,
                                        (String) SPUtils.get(ModuleListActivity.this, "amesdialMac", ""));
                            }
                        });
                        mIndexAdapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        Toast.makeText(AppClient.getInstance(), AppClient.getInstance().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                    }
                }));
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

            //this line is for test
            getBoschInfo(injectorTypeEt.getText().toString().trim());

        } else {
            isBosch = false;
            leftTips.setVisibility(View.VISIBLE);
            leftBoschTips.setVisibility(View.GONE);
        }
    }

    private void getBoschInfo(final String xh) {

        itemList.post(new Runnable() {
            @Override
            public void run() {
                itemList.getSwipeToRefresh().setRefreshing(true);
            }
        });
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
                            Toast.makeText(AppClient.getInstance(), AppClient.getInstance().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                            return null;
                        }
                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
                        return t.getData();
                    }
                })
                .subscribe(new Action1<Bosch>() {
                    @Override
                    public void call(Bosch t) {
                        GlobalUtils.hideKeyboard(ModuleListActivity.this, cs);
                        mBosch = t;
                        cs.setText(mBosch.getCs());
                        yzkyh.setText(mBosch.getYzkyh());
                        yzxh.setText(mBosch.getYzxh());
                        fzjxh.setText(mBosch.getFzjxh());

                        itemList.post(new Runnable() {
                            @Override
                            public void run() {
                                itemList.getSwipeToRefresh().setRefreshing(false);
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        itemList.post(new Runnable() {
                            @Override
                            public void run() {
                                itemList.getSwipeToRefresh().setRefreshing(false);
                            }
                        });
                        L.e("" + throwable.toString());
                        Toast.makeText(AppClient.getInstance(), AppClient.getInstance().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @OnClick(R.id.home_btn)
    public void homeBtn(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(ModuleListActivity.this, "请打开蓝牙",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


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
        getModuleList(injectorType);
    }
//    private void getRepairStep(final String injectorType, final String language, final int moduleId, final String xh) {
//
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("injectorType", injectorType);
//        map.put("language", language);
//        map.put("moduleId", moduleId);
//        if (!TextUtils.isEmpty(xh)) {
//            map.put("xh", xh);
//        }
//        subscription.add(api.getRepairStep(map)
//                .observeOn(Schedulers.io())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Result<StepList>>() {
//                    @Override
//                    public void call(Result<StepList> t) {
//                        L.e("getRepairStep " + t.getStatus() + t.getMsg() + "" + t.getData().toString());
//                        if (t.getStatus() == 200) {
//                            Toast.makeText(getApplicationContext(), t.getMsg(), Toast.LENGTH_SHORT).show();
//                            StepList mStepList = t.getData();
//                            if (mStepList != null) {
//                                ModuleItem mItem = mStepList.getModule();
//                                L.v("ModuleName:" + mItem.getModuleName());
//                                IntentUtils.enterStep2Activity(ModuleListActivity.this, injectorType, language, moduleId, xh);
//                            }
//
//                        } else {
//                            GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
//                        }
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        L.e("" + throwable.toString());
//                        GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
//                    }
//                }));
//    }
}
