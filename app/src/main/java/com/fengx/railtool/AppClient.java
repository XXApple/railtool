package com.fengx.railtool;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.fengx.railtool.util.FrescoConfig;
import com.pgyersdk.crash.PgyCrashManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * 项目名称：
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：15/11/16 下午10:34
 * 修改人：wengyiming
 * 修改时间：15/11/16 下午10:34
 * 修改备注：
 */
public class AppClient extends Application {
    private static AppClient sInstance;

    public static AppClient getInstance() {
        return sInstance;
    }

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        AppClient application = (AppClient) context.getApplicationContext();

        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        PgyCrashManager.register(sInstance);
        refWatcher = LeakCanary.install(sInstance);
        Fresco.initialize(sInstance, FrescoConfig.getImagePipelineConfig(sInstance));
    }
}
