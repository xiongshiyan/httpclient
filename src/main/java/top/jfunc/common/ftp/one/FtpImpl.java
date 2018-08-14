package top.jfunc.common.ftp.one;

import top.jfunc.common.ftp.ConnectBean;
import top.jfunc.common.ftp.DownloadBean;
import top.jfunc.common.ftp.Ftp;
import top.jfunc.common.ftp.UploadBean;

import java.io.IOException;

/**
 * @author xiongshiyan at 2018/4/8
 */
public class FtpImpl implements Ftp {
    @Override
    public void upload(ConnectBean connectBean, UploadBean uploadBean) throws IOException{
        FtpUtil.upload(connectBean,uploadBean);
    }

    @Override
    public void download(ConnectBean connectBean, DownloadBean downloadBean) throws IOException{
        FtpUtil.download(connectBean,downloadBean);
    }

    @Override
    public void createDir(ConnectBean connectBean, String remotePath) throws IOException {
        FtpUtil.createDir(connectBean,remotePath);
    }
}
