package com.commonrail.mtf.util.Api;


import com.commonrail.mtf.po.Bosch;
import com.commonrail.mtf.po.Injector;
import com.commonrail.mtf.po.Module;
import com.commonrail.mtf.po.Result;
import com.commonrail.mtf.po.StepList;
import com.commonrail.mtf.po.Update;
import com.commonrail.mtf.po.User;

import java.util.HashMap;
import java.util.List;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：15/11/16 下午10:34
 * 修改人：wengyiming
 * 修改时间：15/11/16 下午10:34
 * 修改备注：
 */
public interface RtApi {
    @FormUrlEncoded
    @POST(Config.USER_LOGIN)
    Observable<Result<User>> getUserInfo(@Field("") String username);


    @POST(Config.HOME_INDEX)
    Observable<Result<List<Injector>>> getIndexList(@Body HashMap<String, String> body);

    @POST(Config.MODULE_LIST)
    Observable<Result<List<Module>>> getModuleList(@Body HashMap<String, String> body);

    @POST(Config.SEARCH_BOSCH)
    Observable<Result<Bosch>> searchBosch(@Body HashMap<String, String> body);


    @POST(Config.REPAIR_STEP)
    Observable<Result<StepList>> getRepairStep(@Body HashMap<String, Object> body);


    @POST(Config.UPLOAD_MESRESULT)
    Observable<Result<StepList>> uploadMesResult(@Field("injectorType") String injectorType, @Field("moduleId") String moduleId, @Field("values") String values);

    @FormUrlEncoded
    @POST(Config.APP_VERSION)
    Observable<Result<Update>> appVersion(@Field("") String injectorType);

    @FormUrlEncoded
    @POST(Config.UPDATE_FILE)
    Observable<Result<StepList>> updateFile(@Field("") String injectorType);


}
