package com.commonrail.mtf;

import android.app.Application;

import com.commonrail.mtf.util.FrescoConfig;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.db.DbCore;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.pgyersdk.crash.PgyCrashManager;
import com.yw.filedownloader.FileDownloader;

/**
 * 项目名称：
 * 类描述：本app是为android 平板写的工具app，使用rxjava,okhttp,retrofit,greendao,使用mvp设计模式进行解耦分层
 * <p/>
 * mvp模型主要参考：http://rocko.xyz/2015/02/06/Android%E4%B8%AD%E7%9A%84MVP/#more
 * <p/>
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
        init();
    }

    private void init() {
        FileDownloader.init(this);//初始化文件下载器
        L.isDebug = true;//开启日志收集
        sInstance = this;//app全局上下文
        DbCore.init(this);//初始化数据库
//        Dexter.initialize(this);//初始化运行时权限检查
        PgyCrashManager.register(this);//全局运行时崩溃收集
        Fresco.initialize(sInstance, FrescoConfig.getImagePipelineConfig(sInstance));//初始化图片加载库
    }

}
