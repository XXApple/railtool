package com.commonrail.mtf.util.Api;


import com.commonrail.mtf.db.InjectorDb;
import com.commonrail.mtf.mvp.model.entity.Bosch;
import com.commonrail.mtf.mvp.model.entity.FileUpload;
import com.commonrail.mtf.mvp.model.entity.Module;
import com.commonrail.mtf.mvp.model.entity.Result;
import com.commonrail.mtf.mvp.model.entity.StepList;
import com.commonrail.mtf.mvp.model.entity.Update;
import com.commonrail.mtf.mvp.model.entity.User;

import java.util.HashMap;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
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
    Observable<Result<List<InjectorDb>>> getIndexList(@Body HashMap<String, String> body);

    @POST(Config.MODULE_LIST)
    Observable<Result<List<Module>>> getModuleList(@Body HashMap<String, String> body);

    @POST(Config.SEARCH_BOSCH)
    Observable<Result<Bosch>> searchBosch(@Body HashMap<String, String> body);


    @POST(Config.REPAIR_STEP)
    Observable<Result<StepList>> getRepairStep(@Body HashMap<String, Object> body);


    @POST(Config.UPLOAD_MESRESULT)
    Observable<Result<String>> uploadMesResult(@Body HashMap<String, Object> body);

    @FormUrlEncoded
    @POST(Config.APP_VERSION)
    Observable<Result<Update>> appVersion(@Field("") String injectorType);

    @POST(Config.UPDATE_FILE)
    Observable<Result<FileUpload>> updateFile(@Body HashMap<String,Integer> currentVersion);


}
