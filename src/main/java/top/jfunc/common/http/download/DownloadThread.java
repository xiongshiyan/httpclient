package top.jfunc.common.http.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.http.request.RequestCreator;
import top.jfunc.common.http.smart.SmartHttpClient;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

/**
 * 下载线程
 * 每个线程负责下载一部分数据
 *
 * 10个字节数据，3个线程下载，block=4
 * 0 1 2 3 4 5 6 7 8 9
 * |_____| |_____| |_|
 *   t1       t2    t3
 * 设定N个线程，每个线程负责下载文件数量为
 * int block = length % N == 0 ? length / N : length / N +1;
 * int startPosition = threadId * block;//从网络文件的什么位置开始下载数据
   int endPosition = (threadId+1) * block;//下载到网络文件的什么位置结束
 * @author xiongshiyan at 2020/2/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
class DownloadThread extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(DownloadThread.class);

    private CountDownLatch countDownLatch;

    /**
     * 线程每次读取
     */
    private int bufferSize;

    /**
     * 线程编号，从0开始
     */
    private int threadId;
    /**
     * 每个线程下载的数据量
     */
    private int block;
    /**
     * 保存的文件
     */
    private File file;
    /**
     * 执行器
     */
    private SmartHttpClient smartHttpClient;
    /**
     * 下载请求
     */
    private DownloadRequest downloadRequest;

    DownloadThread(CountDownLatch countDownLatch , int bufferSize , int threadId, int block, File file , SmartHttpClient smartHttpClient , DownloadRequest downloadRequest) {
        this.countDownLatch = countDownLatch;
        this.bufferSize = bufferSize;
        this.threadId = threadId;
        this.block = block;
        this.file = file;
        this.smartHttpClient = smartHttpClient;
        this.downloadRequest = RequestCreator.clone(downloadRequest);
    }

    @Override
    public void run() {
        int startPosition = threadId * block;
        int endPosition = (threadId+1) * block - 1;
        logger.info("第"+ (threadId+1)+ "线程下载 " + startPosition + "-" + endPosition);
        //指示该线程要从网络文件的startposition位置开始下载，下载到endposition位置结束
        //Range:bytes=startposition-endposition
        try (RandomAccessFile accessFile = new RandomAccessFile(file, "rwd")){
            //移动指针到文件的某个位置
            accessFile.seek(startPosition);
            downloadRequest.addHeader(HttpHeaders.RANGE , "bytes="+ startPosition+ "-"+ endPosition);

            smartHttpClient.get(downloadRequest ,(statusCode, inputStream, rc, hd) ->{
                byte[] buffer = new byte[bufferSize];
                int len = 0;
                int total = len;
                while( (len = inputStream.read(buffer)) != -1 ){
                    accessFile.write(buffer, 0, len);
                    total += len;
                    logger.info("第"+ (threadId+1)+ "线程下载:" + total);
                }
                logger.info("第"+ (threadId+1)+ "线程下载完成:" + total);
                return file;
            });
        }catch (Exception e){
            logger.error(e.getMessage() , e);
        }finally {
            countDownLatch.countDown();
        }
    }
}
