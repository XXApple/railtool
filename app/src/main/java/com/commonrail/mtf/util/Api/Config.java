package com.commonrail.mtf.util.Api;

/**
 * 项目名称：jianyue
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：15/11/16 下午10:14
 * 修改人：wengyiming
 * 修改时间：15/11/16 下午10:14
 * 修改备注：
 */
public interface Config {

//    http://api.gongguizhijia.com/railtool/auth/getIndexList
//    http://139.196.110.128:8280/railtool/auth/getIndexList


    String BASE_URL = "http://139.196.110.128:8280/railtool/";
    String USER_LOGIN = "auth/getUserInfo";
    String HOME_INDEX = "auth/getIndexList";
    String MODULE_LIST = "auth/getModuleList";
    String SEARCH_BOSCH = "auth/searchBosch";
    String REPAIR_STEP = "auth/getRepairStep";
    String UPLOAD_MESRESULT = "auth/uploadMesResult";
    String APP_VERSION = "auth/appVersion";
    String UPDATE_FILE = "auth/updateFile";


}
