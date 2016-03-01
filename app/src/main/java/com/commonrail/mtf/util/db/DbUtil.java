package com.commonrail.mtf.util.db;

import com.commonrail.mtf.db.FilesDao;
import com.commonrail.mtf.db.InjectorDbDao;

/**
 * Created by wengyiming on 2016/3/1.
 */
public class DbUtil {
    private static FilesService cardService;
    private static InjectorService personService;

    private static FilesDao getFilesDao() {
        return DbCore.getDaoSession().getFilesDao();
    }

    private static InjectorDbDao getInjectorDbDao() {
        return DbCore.getDaoSession().getInjectorDbDao();
    }

    public static FilesService getFilesService() {
        if (cardService == null) {
            cardService = new FilesService(getFilesDao());
        }
        return cardService;
    }

    public static InjectorService getInjectorService() {
        if (personService == null) {
            personService = new InjectorService(getInjectorDbDao());
        }
        return personService;
    }
}
