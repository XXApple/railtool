package com.fengx.railtool.util.Api;


import com.fengx.railtool.po.Result;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

/**
 * 项目名称：jianyue
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
    Observable<Result<String>> login(@Field("username") String username, @Field("password") String password);
}
