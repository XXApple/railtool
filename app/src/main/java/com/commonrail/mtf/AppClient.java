package com.commonrail.mtf;

import android.app.Application;

import com.commonrail.mtf.util.FrescoConfig;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.db.DbCore;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.yw.filedownloader.FileDownloader;

/**
 * 项目名称：
 * 类描述：本app是为android 平板写的工具app，使用rxjava,okhttp,retrofit,greendao,使用mvp设计模式进行解耦分层
 *
 * mvp模型主要参考：http://rocko.xyz/2015/02/06/Android%E4%B8%AD%E7%9A%84MVP/#more
 *
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
