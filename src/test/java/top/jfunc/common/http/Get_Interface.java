package top.jfunc.common.http;

import top.jfunc.common.http.annotation.method.GET;
import top.jfunc.common.http.annotation.method.HEAD;
import top.jfunc.common.http.annotation.parameter.Body;
import top.jfunc.common.http.annotation.parameter.Path;
import top.jfunc.common.http.annotation.parameter.Query;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.Response;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface Get_Interface {

    @GET
    Response request(HttpRequest httpRequest);

    @GET("/list/{id}")
    Response list(@Path("id") int id, @Query("xx") String xx);

    @HEAD.POST("/list/{id}")
    Response post(@Path("id") int id, @Body String xx);
}
