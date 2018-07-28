package cn.zytx.common.ftp.one;

import cn.zytx.common.ftp.ConnectBean;
import cn.zytx.common.ftp.DownloadBean;
import cn.zytx.common.ftp.Ftp;
import cn.zytx.common.ftp.UploadBean;

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
