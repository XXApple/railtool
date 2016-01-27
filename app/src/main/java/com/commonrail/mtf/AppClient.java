package com.commonrail.mtf;

import android.app.Application;
import android.content.Context;

import com.commonrail.mtf.util.FrescoConfig;
import com.commonrail.mtf.util.common.L;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.liulishuo.filedownloader.FileDownloader;
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
    private RefWatcher refWatcher;

    public static AppClient getInstance() {
        return sInstance;
    }

    public static RefWatcher getRefWatcher(Context context) {
        AppClient application = (AppClient) context.getApplicationContext();

        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloader.init(this);
        L.isDebug = true;
        sInstance = this;
//        PgyCrashManager.register(sInstance);
        refWatcher = LeakCanary.install(sInstance);
        Fresco.initialize(sInstance, FrescoConfig.getImagePipelineConfig(sInstance));
    }
}
