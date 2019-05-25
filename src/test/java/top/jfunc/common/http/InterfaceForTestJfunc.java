package top.jfunc.common.http;

import top.jfunc.common.http.annotation.method.*;
import top.jfunc.common.http.annotation.parameter.*;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.smart.Response;

import java.io.File;
import java.util.Map;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface InterfaceForTestJfunc {

    @GET
    Response request(HttpRequest httpRequest);

    @GET("/list/{id}")
    Response list(@Path("id") int id, @Query("xx") String xx);
    @GET
    Response url(@Url String url);
    @GET
    Response header(@Header("naked") String naked);


    @Download
    @GET("/get/download")
    Response download(File file);

    @POST("/post/{id}")
    Response post(@Path("id") int id, @Body String xx);

    @Multipart
    @POST("/post/multipart")
    Response multipart(@Part FormFile... formFiles);
    @Multipart
    @POST("/post/multipart/param")
    Response multipart2(@Part("name") String name , @Part FormFile... formFiles);

    @POST("/post/form")
    Response form(@Field("name") String name , @Field("age") int age);
    @POST("/post/formMap")
    Response formMap(@FieldMap Map<String  , String> params);
}
