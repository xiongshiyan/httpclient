package top.jfunc.common.http.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.http.smart.SmartHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 断点下载器
 * 在下载地址创建一个临时文件记录总大小和已下载大小【为多线程断点下载器打下基础】
 * @see InterruptBaseDownloadFileDownloader
 * @author xiongshiyan at 2020/2/16 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class InterruptBaseConfFileDownloader extends AbstractDownloader implements Downloader {
    private static final Logger logger = LoggerFactory.getLogger(InterruptBaseConfFileDownloader.class);

    public InterruptBaseConfFileDownloader(SmartHttpClient smartHttpClient, int bufferSize) {
        super(smartHttpClient, bufferSize);
    }
    public InterruptBaseConfFileDownloader(SmartHttpClient smartHttpClient) {
        super(smartHttpClient);
    }

    @Override
    public File download(DownloadRequest downloadRequest) throws IOException {
        long totalLength = getNetFileLength(downloadRequest);
        logger.info("totalLength : " + totalLength);
        //保存总文件大小
        DownloadUtil.saveFileLength(downloadRequest.getFile() , totalLength , null);
        //从文件获取已下载量
        long downloadLength = DownloadUtil.readDownloadedLength(downloadRequest.getFile());

        //文件记录的下载量和实际文件的大小不相等，那么以文件的为准，可以解决数据写入文件和长度写入文件的不一致的情况
        long fileLength = DownloadUtil.getDownloadedLength(downloadRequest.getFile());
        logger.info("校验文件长度[记录大小-文件实际大小] : " + downloadLength + "-" + fileLength + ",以后者大小为准");
        if(fileLength != downloadLength){
            downloadLength = fileLength;
            DownloadUtil.saveFileLength(downloadRequest.getFile() , totalLength , downloadLength);
        }

        downloadFileFromDownloaded(downloadRequest, totalLength, downloadLength);

        return downloadRequest.getFile();
    }

    private void downloadFileFromDownloaded(DownloadRequest downloadRequest, final long totalLength, final long downloadLength) throws IOException {
        try (RandomAccessFile accessFile = new RandomAccessFile(downloadRequest.getFile(), "rwd")){
            accessFile.seek(downloadLength);
            //添加Range头
            downloadRequest.addHeader(HttpHeaders.RANGE , "bytes=" + downloadLength + "-");
            getSmartHttpClient().download(downloadRequest , (statusCode, inputStream, rc, hd) ->{
                byte[] buffer = new byte[getBufferSize()];
                int len = 0;
                long downloaded = downloadLength;
                while( (len = inputStream.read(buffer)) != -1 ){
                    accessFile.write(buffer, 0, len);
                    downloaded += len;
                    DownloadUtil.saveFileLength(downloadRequest.getFile() , totalLength , downloaded);
                }

                if(downloaded == totalLength){
                    DownloadUtil.deleteConfFile(downloadRequest.getFile());
                }

                return downloadRequest.getFile();
            });

        }
    }
}
