package top.jfunc.common.http.request.impl;

import top.jfunc.common.http.holder.DefaultFileHolder;
import top.jfunc.common.http.holder.FileHolder;
import top.jfunc.common.http.request.DownloadRequest;

import java.io.File;

/**
 * 下载为文件的请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DownLoadRequest extends BaseRequest implements DownloadRequest {
    public DownLoadRequest(String url){
        super(url);
    }
    public static DownLoadRequest of(String url){
        return new DownLoadRequest(url);
    }
    public static DownLoadRequest of(String url , String filePath){
        DownLoadRequest downLoadFileRequest = new DownLoadRequest(url);
        downLoadFileRequest.fileHolder().setFile(filePath);
        return downLoadFileRequest;
    }
    public static DownLoadRequest of(String url , File file){
        DownLoadRequest downLoadFileRequest = new DownLoadRequest(url);
        downLoadFileRequest.fileHolder().setFile(file);
        return downLoadFileRequest;
    }

    private FileHolder fileHolder = new DefaultFileHolder();

    @Override
    public FileHolder fileHolder() {
        return this.fileHolder;
    }
}
