package top.jfunc.common.http.request.holder.impl;

import top.jfunc.common.http.holder.DefaultFileHolder;
import top.jfunc.common.http.holder.FileHolder;
import top.jfunc.common.http.request.holder.HolderDownloadRequest;

import java.io.File;

/**
 * 下载为文件的请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class HolderDownLoadRequest extends BaseHolderHttpRequest<HolderDownLoadRequest> implements HolderDownloadRequest {
    public HolderDownLoadRequest(String url){
        super(url);
    }
    public static HolderDownLoadRequest of(String url){
        return new HolderDownLoadRequest(url);
    }
    public static HolderDownLoadRequest of(String url , String filePath){
        HolderDownLoadRequest downLoadFileRequest = new HolderDownLoadRequest(url);
        downLoadFileRequest.fileHolder().setFile(filePath);
        return downLoadFileRequest;
    }
    public static HolderDownLoadRequest of(String url , File file){
        HolderDownLoadRequest downLoadFileRequest = new HolderDownLoadRequest(url);
        downLoadFileRequest.fileHolder().setFile(file);
        return downLoadFileRequest;
    }

    private FileHolder fileHolder = new DefaultFileHolder();

    @Override
    public FileHolder fileHolder() {
        return this.fileHolder;
    }

    @Override
    public HolderDownLoadRequest setFile(File file) {
        fileHolder().setFile(file);
        return myself();
    }
}
