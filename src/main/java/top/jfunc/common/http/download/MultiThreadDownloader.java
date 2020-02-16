package top.jfunc.common.http.download;

import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.http.smart.SmartHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

/**
 * 多线程下载器，依赖于服务器支持Range请求头以及head方法
 * 另外说明：多线程下载其本质是启用更多线程抢占服务器资源，如果服务器限制了
 *      或者本机的配置使得不一定能够提高下载速度，在特定的情况下可能速度更慢
 *
 * @author xiongshiyan at 2020/2/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class MultiThreadDownloader extends AbstractDownloader implements Downloader {

    private int threadSize = 3;

    public MultiThreadDownloader(SmartHttpClient smartHttpClient , int bufferSize, int threadSize) {
        super(smartHttpClient, bufferSize);
        this.threadSize = threadSize;
    }
    public MultiThreadDownloader(SmartHttpClient smartHttpClient, int bufferSize) {
        super(smartHttpClient, bufferSize);
    }
    public MultiThreadDownloader(SmartHttpClient smartHttpClient) {
        super(smartHttpClient);
    }

    @Override
    public File download(DownloadRequest downloadRequest) throws IOException {
        //获取网络文件的大小：字节数
        long contentLength = DownloadUtil.getNetFileLength(getSmartHttpClient() , downloadRequest);

        //生成本地文件并设置其大小为网络文件的大小
        try (RandomAccessFile accessFile = new RandomAccessFile(downloadRequest.getFile(), "rwd")){
            accessFile.setLength(contentLength);
        }

        //过个线程下载结束才返回
        CountDownLatch countDownLatch = new CountDownLatch(getThreadSize());
        startDownloadThread(downloadRequest, contentLength, countDownLatch);

        //等待所有线程结束
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return downloadRequest.getFile();
    }

    private void startDownloadThread(DownloadRequest downloadRequest, long contentLength, CountDownLatch countDownLatch) {
        //计算每条线程负责下载的数据量
        int threadSize = getThreadSize();
        long block = contentLength % threadSize == 0 ? contentLength / threadSize : contentLength / threadSize + 1;
        for(int threadId = 0; threadId < threadSize; threadId++){
            new DownloadThread(countDownLatch ,getBufferSize(),
                    threadId , block , downloadRequest.getFile() ,
                    getSmartHttpClient(), downloadRequest).start();
        }
    }

    public int getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }
}
