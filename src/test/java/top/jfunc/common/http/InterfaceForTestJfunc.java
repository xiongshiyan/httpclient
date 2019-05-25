package top.jfunc.common.http;

import top.jfunc.common.http.annotation.HttpService;
import top.jfunc.common.http.annotation.method.*;
import top.jfunc.common.http.annotation.parameter.*;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.Response;

import java.util.Map;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@HttpService
public interface InterfaceForTestJfunc {

    @GET
    Response request(HttpRequest httpRequest);

    @GET("/get/{q}")
    Response list(@Path("q") String q, @Query("xx") int xx);
    @GET("/get/query")
    Response queryMap(@QueryMap Map<String , String> map);
    @GET
    Response url(@Url String url);

    @GET("get/query")
    Response header(@Header("naked") String naked);

    @Headers({"xx:xiongshiyan","yy:xsy"})
    @GET("get/query")
    Response headers(@Header("naked") String naked);

    @GET("get/query")
    Response headerMap(@HeaderMap Map<String , String> map);



    @GET("/get/query")
    Response download();

    @POST("/post/{id}")
    Response post(@Path("id") String id, @Body String xx);

    @Multipart
    @POST("/upload/only")
    Response upload(@Part FormFile... formFiles);
    @Multipart
    @POST("/upload/withParam")
    Response uploadWithParam(@Part("name") String name , @Part("age") int age , @Part FormFile... formFiles);

    @FormUrlEncoded
    @POST("/post/form")
    Response form(@Field("name") String name , @Field("age") int age);
    @FormUrlEncoded
    @POST("/post/form")
    Response formMap(@FieldMap Map<String  , String> params);
}
