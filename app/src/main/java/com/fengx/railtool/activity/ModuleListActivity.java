package com.fengx.railtool.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengx.railtool.R;
import com.fengx.railtool.adapter.ModuleListAdapter;
import com.fengx.railtool.base.BaseActivity;
import com.fengx.railtool.po.Bosch;
import com.fengx.railtool.po.Module;
import com.fengx.railtool.po.Result;
import com.fengx.railtool.util.Api.Config;
import com.fengx.railtool.util.Api.RtApi;
import com.fengx.railtool.util.IntentUtils;
import com.fengx.railtool.util.common.GlobalUtils;
import com.fengx.railtool.util.common.L;
import com.fengx.railtool.util.retrofit.RxUtils;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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

    @Bind(R.id.rightImg)
    ImageView rightImg;
    @Bind(R.id.rightBtn)
    TextView rightBtn;
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

    @Override
    public int getLayoutRes() {
        return R.layout.activity_module_list;
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
        tips.setText(R.string.module_list_tips);
        api = RxUtils.createApi(RtApi.class, Config.BASE_URL);
        String injectorType = getIntent().getStringExtra("injectorType");
        String language = getIntent().getStringExtra("language");
        getModuleList(injectorType, language);
        initBoschInfoView(injectorType);
    }

    @OnClick(R.id.home_btn)
    public void homeBtn(View view) {
        finish();
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
            getBoschInfo(mInjectorType);

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
                .subscribe(new Action1<Result<Bosch>>() {
                    @Override
                    public void call(Result<Bosch> t) {
                        L.e("searchBosch " + t.getStatus() + t.getMsg());
                        if (t.getStatus() == 200) {
                            Toast.makeText(getApplicationContext(), t.getMsg(), Toast.LENGTH_SHORT).show();
                            GlobalUtils.hideKeyboard(ModuleListActivity.this, cs);
                            mBosch = t.getData();
                            cs.setText(mBosch.getCs());
                            yzkyh.setText(mBosch.getYzkyh());
                            yzxh.setText(mBosch.getYzxh());
                            fzjxh.setText(mBosch.getFzjxh());
                        } else {
                            GlobalUtils.showToastShort(ModuleListActivity.this, getString(R.string.net_error));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        GlobalUtils.showToastShort(ModuleListActivity.this, getString(R.string.net_error));
                    }
                }));
    }

    private void getModuleList(final String injectorType, final String language) {
        HashMap<String, String> map = new HashMap<>();
        map.put("injectorType", injectorType);
        map.put("language", language);
        L.e(map.toString());
        subscription.add(api.getModuleList(map)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Result<List<Module>>>() {
                    @Override
                    public void call(final Result<List<Module>> t) {
                        L.e("getModuleList " + t.getStatus() + t.getMsg());
                        if (t.getStatus() == 200) {
                            Toast.makeText(getApplicationContext(), t.getMsg(), Toast.LENGTH_SHORT).show();
                            mIndexAdapter = new ModuleListAdapter(t.getData());
                            itemList.setAdapter(mIndexAdapter);
                            mIndexAdapter.setClick(new ModuleListAdapter.Click() {
                                @Override
                                public void itemClick(int p) {
                                    Module mModule = t.getData().get(p);
                                    String xh = "";
                                    if (isBosch) {
                                        if (mBosch != null) {
                                            xh = mBosch.getXh();
                                        }
                                    }
                                    IntentUtils.enterStepActivity(ModuleListActivity.this, injectorType, language, mModule.getId(), xh);
                                }
                            });
                            mIndexAdapter.notifyDataSetChanged();
                        } else {
                            GlobalUtils.showToastShort(ModuleListActivity.this, getString(R.string.net_error));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        GlobalUtils.showToastShort(ModuleListActivity.this, getString(R.string.net_error));
                    }
                }));
    }

}
