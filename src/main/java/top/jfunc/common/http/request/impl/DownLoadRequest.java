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
        downLoadFileRequest.setDownLoadFile(filePath);
        return downLoadFileRequest;
    }
    public static DownLoadRequest of(String url , File file){
        DownLoadRequest downLoadFileRequest = new DownLoadRequest(url);
        downLoadFileRequest.setDownLoadFile(file);
        return downLoadFileRequest;
    }

    @Override
    public File getFile() {
        return file;
    }

    /**
     * 为文件下载确定信息
     */
    private File file = null;

    public DownLoadRequest setDownLoadFile(File file) {
        this.file = file;
        return this;
    }
    public DownLoadRequest setDownLoadFile(String filePath) {
        this.file = new File(filePath);
        return this;
    }
}
