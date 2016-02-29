package com.commonrail.mtf.util;

import android.content.Context;
import android.util.Log;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.db.DaoSession;
import com.commonrail.mtf.db.Files;
import com.commonrail.mtf.db.FilesDao;
import com.commonrail.mtf.db.InjectorDb;
import com.commonrail.mtf.db.InjectorDbDao;
import com.commonrail.mtf.util.common.L;

import java.util.List;

/*********
 * @author: wengyiming
 *********/
public class DbHelp {

    private static final String TAG = DbHelp.class.getSimpleName();
    private static DbHelp instance;
    private static Context appContext;
    private DaoSession mDaoSession;
    private FilesDao mFilesDao;
    private InjectorDbDao mInjectorDbDao;

    private DbHelp() {
    }

    public static DbHelp getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelp();
            if (appContext == null) {
                appContext = context.getApplicationContext();
            }
            instance.mDaoSession = AppClient.getDaoSession();
            instance.mFilesDao = instance.mDaoSession.getFilesDao();
            instance.mInjectorDbDao = instance.mDaoSession.getInjectorDbDao();
        }
        return instance;
    }

    public InjectorDbDao getInjectorDbDao() {
        return mInjectorDbDao;
    }

    public FilesDao getFilesDao() {
        return mFilesDao;
    }


    public void deleteAllTrip() {
        mFilesDao.deleteAll();
    }

    public void deleteTrip(long id) {
        mFilesDao.deleteByKey(id);
        Log.i(TAG, "delete");
    }

    public void deleteTrip(int status) {
        if (status == 0) {

        }
        Log.i(TAG, "delete");
    }

    public void deleteFiles(Files mFiles) {
        mFilesDao.delete(mFiles);
    }


    public Files queryFilesByStatus(long status) {
        return mFilesDao.load(status);
    }

    public Files loadFiles(long id) {
        return mFilesDao.load(id);
    }

    public List<Files> loadAllFile() {
        return mFilesDao.loadAll();
    }


    public List<Files> queryFile(String where, String... params) {
        return mFilesDao.queryRaw(where, params);
    }


    public long insertFie(Files trip) {
        return mFilesDao.insertOrReplace(trip);
    }

    public void updateFile(Files trip) {
        mFilesDao.update(trip);
    }


    public void saveFileLists(final List<Files> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        mFilesDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    Files trip = list.get(i);
                    mFilesDao.insertOrReplace(trip);
                }
            }
        });
    }


    public void saveInjectorLists(final List<InjectorDb> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        long i = 0;
        for (InjectorDb mInjectorDb : list) {
            long row = mInjectorDbDao.insertOrReplace(mInjectorDb);
            if (row != -1) {
                i++;
            }
        }
        L.e("DBHelp insert InjectorLists items" + i);


//        mInjectorDbDao.getSession().runInTx(new Runnable() {
//            @Override
//            public void run() {
//
//                for (int i = 0; i < list.size(); i++) {
//                    mInjectorDbDao.insertOrReplace(list.get(i));
//                }
//            }
//        });
    }

    public List<InjectorDb> loadAllInjector() {
        return mInjectorDbDao.loadAll();
    }


}