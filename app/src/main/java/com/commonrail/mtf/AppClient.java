package com.commonrail.mtf;

import android.app.Application;

import com.commonrail.mtf.util.FrescoConfig;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.db.DbCore;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.yw.filedownloader.FileDownloader;

/**
 * 项目名称：
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/01/06 下午10:34
 * 修改人：wengyiming
 * 修改时间：16/01/06 下午10:34
 * 修改备注：
 */
public class AppClient extends Application {
    private static AppClient sInstance;

//    private RefWatcher refWatcher;

    public static AppClient getInstance() {
        return sInstance;
    }

//    public static RefWatcher getRefWatcher(Context context) {
//        AppClient application = (AppClient) context.getApplicationContext();
//        return application.refWatcher;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloader.init(this);
        L.isDebug = true;
        sInstance = this;
        DbCore.init(this);


//        PgyCrashManager.register(sInstance);
//        refWatcher = LeakCanary.install(sInstance);
//        OkHttpClientManager.getInstance();
//        try {
//            OkHttpClientManager.setCertificates(getAssets().open("railtoolapi.keystore"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Fresco.initialize(sInstance, FrescoConfig.getImagePipelineConfig(sInstance));
    }

}
