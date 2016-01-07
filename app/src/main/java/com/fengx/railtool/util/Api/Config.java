package com.fengx.railtool.util.Api;

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
    String BASE_URL = "http://api.gongguizhijia.com/railtool/";
    String USER_LOGIN = "user/login";//用户登录
    String HOME_INDEX = "auth/getIndexList";//获取首页列表
    String MODULE_LIST = "auth/getModuleList";//获取模块列表
    String SEARCH_BOSCH = "auth/searchBosch";//查询喷油器信息
    String REPAIR_STEP = "auth/getRepairStep";//获取维修操作步骤
    String UPLOAD_MES_RESULT = "auth/uploadMesResult";//上传测量结果
    String UPDATE_VERSION = "appVersion";//APP版本更新
    String UPDATE_FILE = "updateFile";
    String SID = "sid";


}
