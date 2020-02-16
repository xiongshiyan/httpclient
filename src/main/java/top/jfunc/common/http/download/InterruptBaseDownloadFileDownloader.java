package top.jfunc.common.http.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.http.smart.SmartHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 断点下载器
 * 根据已下载的文件，不需要每次都记录下载量
 * @see InterruptBaseConfFileDownloader
 * @author xiongshiyan at 2020/2/16 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class InterruptBaseDownloadFileDownloader extends AbstractDownloader implements Downloader {
    private static final Logger logger = LoggerFactory.getLogger(InterruptBaseDownloadFileDownloader.class);

    public InterruptBaseDownloadFileDownloader(SmartHttpClient smartHttpClient, int bufferSize) {
        super(smartHttpClient , bufferSize);
    }

    public InterruptBaseDownloadFileDownloader(SmartHttpClient smartHttpClient) {
        super(smartHttpClient);
    }

    @Override
    public File download(DownloadRequest downloadRequest) throws IOException {
        long totalLength = getNetFileLength(downloadRequest);
        logger.info("totalLength    : " + totalLength);
        //从文件获取已下载量
        final long downloadLength = DownloadUtil.getDownloadedLength(downloadRequest.getFile());
        logger.info("downloadLength : " + downloadLength);

        downloadFileFromDownloaded(downloadRequest, totalLength, downloadLength);

        return downloadRequest.getFile();
    }

    private void downloadFileFromDownloaded(DownloadRequest downloadRequest, long totalLength, long downloadLength) throws IOException {
        if(totalLength == downloadLength){
            return;
        }

        //以追加方式写入
        try (FileOutputStream outputStream = new FileOutputStream(downloadRequest.getFile() , true)){
            //添加Range头
            downloadRequest.addHeader(HttpHeaders.RANGE , "bytes=" + downloadLength + "-");
            getSmartHttpClient().download(downloadRequest , (statusCode, inputStream, rc, hd) ->{
                byte[] buffer = new byte[getBufferSize()];
                int len = 0;
                while( (len = inputStream.read(buffer)) != -1 ){
                    outputStream.write(buffer, 0, len);
                    outputStream.flush();
                }

                return downloadRequest.getFile();
            });
        }
    }
}
