package com.fengx.railtool.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.fengx.railtool.R;
import com.fengx.railtool.base.BaseActivity;
import com.fengx.railtool.po.Result;
import com.fengx.railtool.po.StepList;
import com.fengx.railtool.util.Api.Config;
import com.fengx.railtool.util.Api.RtApi;
import com.fengx.railtool.util.common.GlobalUtils;
import com.fengx.railtool.util.common.L;
import com.fengx.railtool.util.retrofit.RxUtils;

import java.util.HashMap;
import java.util.Objects;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/1/12 下午10:48
 * 修改人：wengyiming
 * 修改时间：16/1/12 下午10:48
 * 修改备注：
 */
public class StepActivity extends BaseActivity {

    private RtApi api;
    private CompositeSubscription subscription = new CompositeSubscription();

    @Override
    public int getLayoutRes() {
        return R.layout.activity_step;
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


        String injectorType = getIntent().getStringExtra("injectorType");
        String language = getIntent().getStringExtra("language");
        int moduleId = getIntent().getIntExtra("moduleId", 0);
        String xh = getIntent().getStringExtra("xh");
        getRepairStep(injectorType, language, moduleId, xh);

    }


    private void getRepairStep(String injectorType, String language, int moduleId, String xh) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("injectorType", injectorType);
        map.put("language", language);
        map.put("moduleId", moduleId);
        if (!TextUtils.isEmpty(xh))
            map.put("xh", xh);

        L.e(map.toString());

        subscription.add(api.getRepairStep(map)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Result<StepList>>() {
                    @Override
                    public void call(Result<StepList> t) {
                        L.e("getRepairStep " + t.getStatus() + t.getMsg());
                        if (t.getStatus() == 200) {
                            Toast.makeText(getApplicationContext(), t.getMsg(), Toast.LENGTH_SHORT).show();
                            StepList mStepList = t.getData();
                            if (mStepList != null) {
                                L.v("ModuleName:" + mStepList.getModule().getModuleName() + "\n StepList size:" + mStepList.getStepList().size() + "");
                                toolbar.setTitle("ModuleName:" + mStepList.getModule().getModuleName());
                                toolbar.setSubtitle("StepList size:" + mStepList.getStepList().size() + "");
                            } else {
                                toolbar.setTitle("mStepList=null");
                            }
                        } else {
                            GlobalUtils.showToastShort(StepActivity.this, getString(R.string.net_error));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        GlobalUtils.showToastShort(StepActivity.this, getString(R.string.net_error));
                    }
                }));
    }

}
