package com.commonrail.mtf.util.db;

import com.commonrail.mtf.db.InjectorDb;
import com.commonrail.mtf.db.InjectorDbDao;

/**
 * Created by wengyiming on 2016/3/1.
 */
public class InjectorService extends BaseService<InjectorDb, Long> {
    public InjectorService(InjectorDbDao dao) {
        super(dao);
    }
}
