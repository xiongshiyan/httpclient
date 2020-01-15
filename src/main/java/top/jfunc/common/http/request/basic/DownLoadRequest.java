package top.jfunc.common.http.request.basic;

import top.jfunc.common.http.request.DownloadRequest;

import java.io.File;

/**
 * 下载为文件的请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DownLoadRequest extends BaseHttpRequest<DownLoadRequest> implements DownloadRequest {
    public DownLoadRequest(String url){
        super(url);
    }
    public DownLoadRequest(){
    }
    public static DownLoadRequest of(String url){
        return new DownLoadRequest(url);
    }
    public static DownLoadRequest of(){
        return new DownLoadRequest();
    }
    public static DownLoadRequest of(String url , File file){
        DownLoadRequest downLoadFileRequest = new DownLoadRequest(url);
        downLoadFileRequest.setFile(file);
        return downLoadFileRequest;
    }

    private File file;

    @Override
    public DownLoadRequest setFile(File file) {
        this.file = file;
        return myself();
    }

    @Override
    public File getFile() {
        return file;
    }
}
