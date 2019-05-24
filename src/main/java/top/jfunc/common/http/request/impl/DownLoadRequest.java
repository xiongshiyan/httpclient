package top.jfunc.common.http.request.impl;

import java.io.File;

/**
 * 下载为文件的请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DownLoadRequest extends BaseRequest<DownLoadRequest> implements top.jfunc.common.http.request.DownLoadRequest {
    public DownLoadRequest(String url){
        super(url);
    }
    public static DownLoadRequest of(String url){
        return new DownLoadRequest(url);
    }
    public static DownLoadRequest of(String url , String filePath){
        DownLoadRequest downLoadFileRequest = new DownLoadRequest(url);
        downLoadFileRequest.setFile(filePath);
        return downLoadFileRequest;
    }
    public static DownLoadRequest of(String url , File file){
        DownLoadRequest downLoadFileRequest = new DownLoadRequest(url);
        downLoadFileRequest.setFile(file);
        return downLoadFileRequest;
    }

    /**
     * 为文件下载确定信息
     */
    private File file = null;

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public DownLoadRequest setFile(File file) {
        this.file = file;
        return this;
    }

    public DownLoadRequest setFile(String filePath) {
        this.file = new File(filePath);
        return this;
    }
}
