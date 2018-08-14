package top.jfunc.common.ftp;

import top.jfunc.common.ftp.ConnectBean;
import top.jfunc.common.ftp.DownloadBean;
import top.jfunc.common.ftp.Ftp;
import top.jfunc.common.ftp.UploadBean;
import top.jfunc.common.ftp.another.FtpUtil;
import top.jfunc.common.ftp.one.FtpImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiongshiyan at 2018/4/8
 */
public class FtpTest {
    private ConnectBean connectBean = null;
    @Before
    @Ignore
    public void before(){
        connectBean = new ConnectBean().setHost("192.168.1.190").setPort(21).setUsername("xiongshiyan").setPassword("xsy881026").setEncoding("ISO-8859-1").setPath("/report");
    }
    @Test@Ignore
    public void testUpload() throws IOException{
        Ftp ftp = new FtpImpl();
        UploadBean uploadBean = new UploadBean().setRemotePath("/report").setDestFileName("xx.txt").setInputSteam(new FileInputStream("C:\\Users\\xiongshiyan\\Desktop\\nohup.out"));
        ftp.upload(connectBean,uploadBean);
    }
    @Test@Ignore
    public void testDownload() throws IOException{
        Ftp ftp = new FtpImpl();
        DownloadBean downloadBean = new DownloadBean().setRemotePath("/report").setRemoteFileName("xx.txt").setLocalFileName("C:\\Users\\xiongshiyan\\Desktop\\yy.txt");
        ftp.download(connectBean,downloadBean);
    }
    @Test@Ignore
    public void testCreateDir() throws IOException{
        Ftp ftp = new FtpImpl();
        ftp.createDir(connectBean,"/xx/xx/data/");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test@Ignore
    public void testup() throws Exception{
        Map<String, String> localFilePathAndRemoteFileName = new LinkedHashMap<>(2);
        localFilePathAndRemoteFileName.put("C:\\Users\\xiongshiyan\\Desktop\\nohup.out", "/xx/shell/data/1.out");
        localFilePathAndRemoteFileName.put("C:\\Users\\xiongshiyan\\Desktop\\yy.txt", "/xx/shell/data/2.txt");

        FtpUtil.upload(connectBean, localFilePathAndRemoteFileName);
    }

    @Test@Ignore
    public void testdown() throws Exception{
        String localFolderPath = "C:\\Users\\xiongshiyan\\Desktop";
        List<String> remoteFiles = new ArrayList<>(3);
        remoteFiles.add("1.out");
        remoteFiles.add("2.txt");

        FtpUtil.download(connectBean, remoteFiles, localFolderPath);
    }

    @Test@Ignore
    public void testDelete() throws Exception{
        String pathName = "xx.txt";
        FtpUtil.deleteFile(connectBean, pathName);
    }

    //移动文件
    @Test@Ignore
    public void testMove () throws Exception{
        String form = "1.out";
        String to = "2.out";
        FtpUtil.renameFile(connectBean, form, to);
    }
}
