package com.commonrail.mtf.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;
import com.commonrail.mtf.util.common.ViewUtils;
import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;

/**
 * 项目名称：jianyue
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：15/11/16 下午10:34
 * 修改人：wengyiming
 * 修改时间：15/11/16 下午10:34
 * 修改备注：
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected static String TAG = "BaseActivity";
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View decorView = getWindow().getDecorView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ViewUtils.toggleHideyBar(decorView);
        setContentView(getLayoutRes());
        TAG = getActivity().getClass().getSimpleName();
        ButterKnife.bind(getActivity());
        initToolBar();
    }

    @LayoutRes
    public abstract int getLayoutRes();

    public abstract Activity getActivity();

    @SuppressWarnings("ConstantConditions")
    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar == null)
            return;
        toolbar.setTitle(null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.drawable.logo);
//        toolbar.setNavigationIcon(R.drawable.logo);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(getActivity());
        super.onDestroy();
        RefWatcher refWatcher = AppClient.getRefWatcher(getActivity());
        refWatcher.watch(getActivity());
    }
}
