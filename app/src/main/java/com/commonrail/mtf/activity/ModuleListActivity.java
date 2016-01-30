package com.commonrail.mtf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;
import com.commonrail.mtf.activity.bluetooth.DeviceScanActivity;
import com.commonrail.mtf.adapter.ModuleListAdapter;
import com.commonrail.mtf.base.BaseActivity;
import com.commonrail.mtf.po.Bosch;
import com.commonrail.mtf.po.Module;
import com.commonrail.mtf.po.Result;
import com.commonrail.mtf.util.Api.Config;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.IntentUtils;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.retrofit.RxUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/1/12 下午8:41
 * 修改人：wengyiming
 * 修改时间：16/1/12 下午8:41
 * 修改备注：
 */
public class ModuleListActivity extends BaseActivity {

    @Bind(R.id.home_btn)
    LinearLayout homeBtn;
    @Bind(R.id.item_list)
    RecyclerView itemList;
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

    private RtApi api;
    private ModuleListAdapter mIndexAdapter;
    private CompositeSubscription subscription = new CompositeSubscription();
    private boolean isBosch = false;
    private Bosch mBosch = null;
    private String injectorType;
    private String language;
    private int moduleId = 0;
    private String xh = "";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.title_activity_main);
        tips.setText(R.string.module_list_tips);
        injectorTypeEt.clearFocus();
        api = RxUtils.createApi(RtApi.class, Config.BASE_URL);
        String injectorType = getIntent().getStringExtra("injectorType");
        String language = getIntent().getStringExtra("language");
        getModuleList(injectorType, language);
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

    private void getModuleList(final String injectorType, final String language) {
        HashMap<String, String> map = new HashMap<>();
        map.put("injectorType", injectorType);
        map.put("language", language);
        this.injectorType = injectorType;
        this.language = language;
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
                            GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
                            return null;
                        }
                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
                        return t.getData();
                    }
                })
                .subscribe(new Action1<List<Module>>() {
                    @Override
                    public void call(final List<Module> t) {
                        if (t == null || t.isEmpty()) {
                            return;
                        }
                        mIndexAdapter = new ModuleListAdapter(t);
                        itemList.setAdapter(mIndexAdapter);
                        mIndexAdapter.setClick(new ModuleListAdapter.Click() {
                            @Override
                            public void itemClick(int p) {
                                Module mModule = t.get(p);
                                if (isBosch) {
                                    if (mBosch != null) {
                                        xh = mBosch.getXh();
                                    }
                                }
                                moduleId = mModule.getId();
                                Intent intent = new Intent(ModuleListActivity.this, DeviceScanActivity.class);

                                ModuleListActivity.this.startActivityForResult(intent, 0);
                            }
                        });
                        mIndexAdapter.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
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
                            GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
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
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        GlobalUtils.showToastShort(ModuleListActivity.this, getString(R.string.net_error));
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
        if (data != null) {
            if (resultCode == RESULT_OK) {
                String mDeviceName = data.getStringExtra(Step2Activity.EXTRAS_DEVICE_NAME);
                String mDeviceAddress = data.getStringExtra(Step2Activity.EXTRAS_DEVICE_ADDRESS);
                if (!TextUtils.isEmpty(mDeviceName)) {
                    toolbar.setSubtitle(mDeviceName);
                    L.e("链接蓝牙设备", mDeviceName);
                }
                IntentUtils.enterStep2Activity(ModuleListActivity.this, injectorType, language, moduleId, xh, mDeviceName, mDeviceAddress);

            }
        }
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
