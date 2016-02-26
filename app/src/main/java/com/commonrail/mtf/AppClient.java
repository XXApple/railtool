package com.commonrail.mtf;

import android.app.Application;

import com.commonrail.mtf.db.DaoMaster;
import com.commonrail.mtf.db.DaoSession;
import com.commonrail.mtf.util.FrescoConfig;
import com.commonrail.mtf.util.common.Constant;
import com.commonrail.mtf.util.common.L;
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
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

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
//        PgyCrashManager.register(sInstance);
//        refWatcher = LeakCanary.install(sInstance);
        Fresco.initialize(sInstance, FrescoConfig.getImagePipelineConfig(sInstance));
    }


    /**
     * 取得DaoMaster
     */
    public static DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(sInstance, Constant.DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 取得DaoSession
     */
    public static DaoSession getDaoSession() {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }

        return daoSession;
    }

}
