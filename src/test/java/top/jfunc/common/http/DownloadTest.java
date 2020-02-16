package top.jfunc.common.http;

import org.junit.Test;
import top.jfunc.common.http.download.Downloader;
import top.jfunc.common.http.download.InterruptibleDownloader;
import top.jfunc.common.http.download.MultiThreadDownloader;
import top.jfunc.common.http.request.RequestCreator;
import top.jfunc.common.http.request.basic.DownLoadRequest;
import top.jfunc.common.http.smart.*;

import java.io.File;
import java.io.IOException;

/**
 * @author xiongshiyan at 2020/2/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DownloadTest {
    DownLoadRequest downLoadRequest = RequestCreator.download("http://dzgtest.palmte.cn/upload/Git-2.14.1-64-bit.exe"
        , new File("C:\\Users\\xiongshiyan\\Desktop\\Git-2.14.1-64-bit.exe"));
    @Test
    public void testCommonJDK() throws IOException{
        //124187
        NativeSmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        download(smartHttpClient);
    }
    @Test
    public void testCommonApache() throws IOException{
        //124885
        ApacheSmartHttpClient smartHttpClient = new ApacheSmartHttpClient();
        download(smartHttpClient);
    }
    @Test
    public void testCommonOkhttp3() throws IOException{
        //124076
        OkHttp3SmartHttpClient smartHttpClient = new OkHttp3SmartHttpClient();
        download(smartHttpClient);
    }
    @Test
    public void testCommonJodd() throws IOException{
        //128865
        JoddSmartHttpClient smartHttpClient = new JoddSmartHttpClient();
        download(smartHttpClient);
    }
    private void download(SmartHttpClient smartHttpClient) throws IOException{
        long l = System.currentTimeMillis();
        File download = smartHttpClient.download(downLoadRequest);
        System.out.println(System.currentTimeMillis() - l);
    }



    @Test
    public void testMultiThreadJDK() throws IOException{
        //125475
        NativeSmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        multiThreadDownload(smartHttpClient);
    }
    @Test
    public void testMultiThreadApache() throws IOException{
        //131794
        ApacheSmartHttpClient smartHttpClient = new ApacheSmartHttpClient();
        multiThreadDownload(smartHttpClient);
    }
    @Test
    public void testMultiThreadOkhttp3() throws IOException{
        //125838
        OkHttp3SmartHttpClient smartHttpClient = new OkHttp3SmartHttpClient();
        multiThreadDownload(smartHttpClient);
    }
    @Test
    public void testMultiThreadJodd() throws IOException{
        //125698
        JoddSmartHttpClient smartHttpClient = new JoddSmartHttpClient();
        multiThreadDownload(smartHttpClient);
    }

    private void multiThreadDownload(SmartHttpClient smartHttpClient) throws IOException{
        long l = System.currentTimeMillis();
        MultiThreadDownloader downloader = new MultiThreadDownloader(smartHttpClient , 10 , 102400);
        File download = downloader.download(downLoadRequest);
        System.out.println(System.currentTimeMillis() - l);
    }


    @Test
    public void testInterruptibleJDK() throws IOException{
        //70,289,183:
        NativeSmartHttpClient smartHttpClient = new NativeSmartHttpClient();
        interruptibleDownload(smartHttpClient);
    }
    @Test
    public void testInterruptibleApache() throws IOException{
        //131794
        ApacheSmartHttpClient smartHttpClient = new ApacheSmartHttpClient();
        interruptibleDownload(smartHttpClient);
    }
    @Test
    public void testInterruptibleOkhttp3() throws IOException{
        //125838
        OkHttp3SmartHttpClient smartHttpClient = new OkHttp3SmartHttpClient();
        interruptibleDownload(smartHttpClient);
    }
    @Test
    public void testInterruptibleThreadJodd() throws IOException{
        //125698
        JoddSmartHttpClient smartHttpClient = new JoddSmartHttpClient();
        interruptibleDownload(smartHttpClient);
    }

    private void interruptibleDownload(SmartHttpClient smartHttpClient) throws IOException{
        long l = System.currentTimeMillis();
        Downloader downloader = new InterruptibleDownloader(smartHttpClient);
        downloader.download(downLoadRequest);
        System.out.println(System.currentTimeMillis() - l);
    }
}
