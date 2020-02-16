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
 * 先在下载地址创建一个临时文件，写入数据，待下载完成重命名即可
 * 还需要一个临时文件记录已下载大小和总大小
 * @author xiongshiyan at 2020/2/16 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class InterruptibleDownloader implements Downloader{
    private static final Logger logger = LoggerFactory.getLogger(InterruptibleDownloader.class);

    private SmartHttpClient smartHttpClient;
    private FileService fileService = new FileService();

    public InterruptibleDownloader(SmartHttpClient smartHttpClient) {
        this.smartHttpClient = smartHttpClient;
    }

    @Override
    public File download(DownloadRequest downloadRequest) throws IOException {
        long totalLength = fileService.getNetFileLength(smartHttpClient , downloadRequest);
        logger.info("totalLength : " + totalLength);
        //保存总文件大小
        fileService.saveFileLength(downloadRequest.getFile() , totalLength , null);
        //从文件获取已下载量
        long recordedLength = fileService.getDownloadedLength(downloadRequest.getFile());
        logger.info("recordedLength : " + recordedLength);

        //文件记录的下载量和实际文件的大小不相等，那么以文件的为准，可以解决数据写入文件和长度写入文件的不一致的情况
        long fileLength = downloadRequest.getFile().length();
        logger.info("校验文件长度 : " + recordedLength + "[r]-" + fileLength + "[f],以后者为准");
        if(fileLength != recordedLength){
            recordedLength = fileLength;
            fileService.saveFileLength(downloadRequest.getFile() , totalLength , recordedLength);
        }

        final long downloadLength = recordedLength;

        try (RandomAccessFile accessFile = new RandomAccessFile(downloadRequest.getFile(), "rwd")){
            accessFile.seek(recordedLength);
            //添加Range头
            downloadRequest.addHeader(HttpHeaders.RANGE , "bytes=" + recordedLength + "-");
            smartHttpClient.download(downloadRequest , (statusCode, inputStream, rc, hd) ->{
                byte[] buffer = new byte[1024];
                int len = 0;
                long downloaded = downloadLength;
                while( (len = inputStream.read(buffer)) != -1 ){
                    accessFile.write(buffer, 0, len);
                    downloaded += len;
                    fileService.saveFileLength(downloadRequest.getFile() , totalLength , downloaded);
                }

                if(downloaded == totalLength){
                    fileService.deleteConfFile(downloadRequest.getFile());
                }

                return downloadRequest.getFile();
            });

        }


        return downloadRequest.getFile();
    }
}
