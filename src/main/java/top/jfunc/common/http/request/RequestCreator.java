package top.jfunc.common.http.request;

import top.jfunc.common.http.request.basic.*;

import java.io.File;

/**
 * 提供一些静态方法帮助创建各种{@link HttpRequest}
 * 拿到各种{@link HttpRequest}之后还可以相应地做些细致具体的设置
 * 没有Body这种,像{@link top.jfunc.common.http.Method#HEAD}可以参考Get一样
 * 有Body这种,像{@link top.jfunc.common.http.Method#PUT}可以参考Post一样
 * @see HttpRequest
 * @see StringBodyRequest
 * @see MutableStringBodyRequest
 * @see FormRequest
 * @see UploadRequest
 * @see DownLoadRequest
 * @author xiongshiyan at 2019/7/1 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class RequestCreator {
    private RequestCreator(){}

    public static HttpRequest get(String url){
        return CommonRequest.of(url);
    }
    public static MutableStringBodyRequest post(String url , String body){
        return CommonBodyRequest.of(url, body, null);
    }
    public static MutableStringBodyRequest post(String url , String body , String contentType){
        return CommonBodyRequest.of(url, body, contentType);
    }
    public static FormRequest form(String url){
        return FormBodyRequest.of(url);
    }
    public static DownloadRequest download(String url , File fileToSave){
        return DownLoadRequest.of(url, fileToSave);
    }
    public static UploadRequest upload(String url){
        return UpLoadRequest.of(url);
    }

}
