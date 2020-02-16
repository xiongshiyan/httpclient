package top.jfunc.common.http;

import org.junit.Ignore;
import org.junit.Test;
import top.jfunc.common.http.download.Downloader;
import top.jfunc.common.http.download.InterruptBaseDownloadFileDownloader;
import top.jfunc.common.http.download.InterruptBaseConfFileDownloader;
import top.jfunc.common.http.download.MultiThreadDownloader;
import top.jfunc.common.http.request.RequestCreator;
import top.jfunc.common.http.request.basic.DownLoadRequest;
import top.jfunc.common.http.smart.*;
import top.jfunc.common.utils.CommonUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author xiongshiyan at 2020/2/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@Ignore
public class DownloadTest {
    DownLoadRequest downLoadRequest = RequestCreator.download("http://mirrors.tuna.tsinghua.edu.cn/apache/tomcat/tomcat-8/v8.5.51/bin/apache-tomcat-8.5.51.tar.gz"
        , new File("C:\\Users\\xiongshiyan\\Desktop\\tomcat-" + CommonUtil.randomString(16) + ".tar.gz"));
    @Test
    public void testCommonDownloadJDK() throws IOException{
        //1373
        NativeSmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        commonDownload(smartHttpClient);
    }
    @Test
    public void testCommonDownloadApache() throws IOException{
        //5195
        ApacheSmartHttpClient smartHttpClient = new ApacheSmartHttpClient();
        commonDownload(smartHttpClient);
    }
    @Test
    public void testCommonDownloadOkHttp3() throws IOException{
        //1705
        OkHttp3SmartHttpClient smartHttpClient = new OkHttp3SmartHttpClient();
        commonDownload(smartHttpClient);
    }
    @Test
    public void testCommonDownloadJodd() throws IOException{
        //1655
        JoddSmartHttpClient smartHttpClient = new JoddSmartHttpClient();
        commonDownload(smartHttpClient);
    }
    private void commonDownload(SmartHttpClient smartHttpClient) throws IOException{
        long l = System.currentTimeMillis();
        smartHttpClient.download(downLoadRequest);
        System.out.println(System.currentTimeMillis() - l);
    }





    @Test
    public void testMultiThreadJDK() throws IOException{
        //2007
        NativeSmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        multiThreadDownload(smartHttpClient);
    }
    @Test
    public void testMultiThreadApache() throws IOException{
        //3654
        ApacheSmartHttpClient smartHttpClient = new ApacheSmartHttpClient();
        multiThreadDownload(smartHttpClient);
    }
    @Test
    public void testMultiThreadOkHttp3() throws IOException{
        //7027
        OkHttp3SmartHttpClient smartHttpClient = new OkHttp3SmartHttpClient();
        multiThreadDownload(smartHttpClient);
    }
    @Test
    public void testMultiThreadJodd() throws IOException{
        //2020
        JoddSmartHttpClient smartHttpClient = new JoddSmartHttpClient();
        multiThreadDownload(smartHttpClient);
    }

    private void multiThreadDownload(SmartHttpClient smartHttpClient) throws IOException{
        long l = System.currentTimeMillis();
        MultiThreadDownloader downloader = new MultiThreadDownloader(smartHttpClient , 102400 , 10);
        downloader.download(downLoadRequest);
        System.out.println(System.currentTimeMillis() - l);
    }





    @Test
    public void testInterruptBaseConfFileDownloaderJDK() throws IOException{
        NativeSmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        interruptBaseConfFileDownloader(smartHttpClient);
    }
    @Test
    public void testInterruptBaseConfFileDownloaderApache() throws IOException{
        ApacheSmartHttpClient smartHttpClient = new ApacheSmartHttpClient();
        interruptBaseConfFileDownloader(smartHttpClient);
    }
    @Test
    public void testInterruptBaseConfFileDownloaderOkHttp3() throws IOException{
        OkHttp3SmartHttpClient smartHttpClient = new OkHttp3SmartHttpClient();
        interruptBaseConfFileDownloader(smartHttpClient);
    }
    @Test
    public void testInterruptBaseConfFileDownloaderJodd() throws IOException{
        JoddSmartHttpClient smartHttpClient = new JoddSmartHttpClient();
        interruptBaseConfFileDownloader(smartHttpClient);
    }

    private void interruptBaseConfFileDownloader(SmartHttpClient smartHttpClient) throws IOException{
        long l = System.currentTimeMillis();
        Downloader downloader = new InterruptBaseConfFileDownloader(smartHttpClient , 1024);
        DownLoadRequest downLoadRequest = RequestCreator.download("http://mirrors.tuna.tsinghua.edu.cn/apache/tomcat/tomcat-8/v8.5.51/bin/apache-tomcat-8.5.51.tar.gz"
                , new File("C:\\Users\\xiongshiyan\\Desktop\\tomcat.tar.gz"));
        downloader.download(downLoadRequest);
        System.out.println(System.currentTimeMillis() - l);
    }



    @Test
    public void testInterruptBaseDownloadFileDownloaderJDK() throws IOException{
        NativeSmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        interruptBaseDownloadFileDownloader(smartHttpClient);
    }
    @Test
    public void testInterruptBaseDownloadFileDownloaderApache() throws IOException{
        ApacheSmartHttpClient smartHttpClient = new ApacheSmartHttpClient();
        interruptBaseDownloadFileDownloader(smartHttpClient);
    }
    @Test
    public void testInterruptBaseDownloadFileDownloaderOkHttp3() throws IOException{
        OkHttp3SmartHttpClient smartHttpClient = new OkHttp3SmartHttpClient();
        interruptBaseDownloadFileDownloader(smartHttpClient);
    }
    @Test
    public void testInterruptBaseDownloadFileDownloaderJodd() throws IOException{
        JoddSmartHttpClient smartHttpClient = new JoddSmartHttpClient();
        interruptBaseDownloadFileDownloader(smartHttpClient);
    }

    private void interruptBaseDownloadFileDownloader(SmartHttpClient smartHttpClient) throws IOException{
        long l = System.currentTimeMillis();
        Downloader downloader = new InterruptBaseDownloadFileDownloader(smartHttpClient , 1024);
        DownLoadRequest downLoadRequest = RequestCreator.download("http://mirrors.tuna.tsinghua.edu.cn/apache/tomcat/tomcat-8/v8.5.51/bin/apache-tomcat-8.5.51.tar.gz"
                , new File("C:\\Users\\xiongshiyan\\Desktop\\tomcat.tar.gz"));
        downloader.download(downLoadRequest);
        System.out.println(System.currentTimeMillis() - l);
    }
}
