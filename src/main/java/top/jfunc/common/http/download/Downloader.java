package top.jfunc.common.http.download;

import top.jfunc.common.http.request.DownloadRequest;

import java.io.File;
import java.io.IOException;

/**
 * 高级下载器，目前支持多线程下载、单线程断点下载
 * 大文件上传最好不通过http，而是自定义协议的socket实现
 * @see MultiThreadDownloader
 * @see InterruptBaseConfFileDownloader
 * @see InterruptBaseDownloadFileDownloader
 * @author xiongshiyan at 2020/2/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface Downloader {
    /**
     * 多线程下载器
     * @param downloadRequest 下载请求
     * @return 下载的文件
     * @throws IOException IOException
     */
    File download(DownloadRequest downloadRequest) throws IOException;

    /**
     * 获取网络文件大小
     * @param downloadRequest 下载请求
     * @return 文件大小
     * @throws IOException IOException
     */
    long getNetFileLength(DownloadRequest downloadRequest) throws IOException;
}
