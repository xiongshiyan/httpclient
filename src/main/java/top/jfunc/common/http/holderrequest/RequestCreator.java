package top.jfunc.common.http.holderrequest;

import top.jfunc.common.http.base.Method;
import top.jfunc.common.http.holderrequest.impl.*;

import java.io.File;

/**
 * 提供一些静态方法帮助创建各种{@link HolderHttpRequest}
 * 拿到各种{@link HolderHttpRequest}之后还可以相应地做些细致具体的设置
 * 没有Body这种,像{@link Method#HEAD}可以参考Get一样
 * 有Body这种,像{@link Method#PUT}可以参考Post一样
 * @see HolderHttpRequest
 * @see HolderStringBodyRequest
 * @see HolderMutableStringBodyRequest
 * @see HolderFormRequest
 * @see HolderUploadRequest
 * @see HolderDownloadRequest
 * @author xiongshiyan at 2019/7/1 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class RequestCreator {
    private RequestCreator(){}

    public static HolderCommonRequest get(String url){
        return HolderCommonRequest.of(url);
    }
    public static HolderCommonBodyRequest post(String url , String body){
        return HolderCommonBodyRequest.of(url, body, null);
    }
    public static HolderCommonBodyRequest post(String url , String body , String contentType){
        return HolderCommonBodyRequest.of(url, body, contentType);
    }
    public static HolderFormBodyRequest form(String url){
        return HolderFormBodyRequest.of(url);
    }
    public static HolderDownLoadRequest download(String url , File fileToSave){
        return HolderDownLoadRequest.of(url, fileToSave);
    }
    public static HolderUpLoadRequest upload(String url){
        return HolderUpLoadRequest.of(url);
    }

}
