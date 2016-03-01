package com.commonrail.mtf.util.db;

import com.commonrail.mtf.db.Files;
import com.commonrail.mtf.db.FilesDao;

/**
 * Created by wengyiming on 2016/3/1.
 */
public class FilesService extends BaseService<Files, Long> {
    public FilesService(FilesDao dao) {
        super(dao);
    }
}
