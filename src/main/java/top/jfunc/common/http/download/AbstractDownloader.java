package top.jfunc.common.http.download;

import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.http.smart.SmartHttpClient;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/2/16 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractDownloader implements Downloader{
    private SmartHttpClient smartHttpClient;
    private int bufferSize = 1024;

    public AbstractDownloader(SmartHttpClient smartHttpClient, int bufferSize) {
        this.smartHttpClient = smartHttpClient;
        this.bufferSize = bufferSize;
    }

    public AbstractDownloader(SmartHttpClient smartHttpClient) {
        this.smartHttpClient = smartHttpClient;
    }


    @Override
    public long getNetFileLength(DownloadRequest downloadRequest) throws IOException {
        return DownloadUtil.getNetFileLength(getSmartHttpClient() , downloadRequest);
    }

    public SmartHttpClient getSmartHttpClient() {
        return smartHttpClient;
    }

    public void setSmartHttpClient(SmartHttpClient smartHttpClient) {
        this.smartHttpClient = smartHttpClient;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
}
