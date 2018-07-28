package cn.zytx.common.ftp.another;

import cn.zytx.common.ftp.ConnectBean;
import cn.zytx.common.ftp.DownloadBean;
import cn.zytx.common.ftp.Ftp;
import cn.zytx.common.ftp.UploadBean;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author xiongshiyan at 2018/4/8
 * 目录在连接的时候指定
 */
public class FtpImpl implements Ftp {
    @Override
    public void upload(ConnectBean connectBean, UploadBean uploadBean) throws IOException {
        String destFile = uploadBean.getRemotePath() + uploadBean.getDestFileName();
        String localFileName = uploadBean.getUploadFileName();
        HashMap<String, String> map = new HashMap<>(2);
        map.put(localFileName,destFile);
        FtpUtil.upload(connectBean,map);
    }

    @Override
    public void download(ConnectBean connectBean, DownloadBean downloadBean) throws IOException {
        String dir = downloadBean.getLocalFileName().substring(0, downloadBean.getLocalFileName().lastIndexOf("/"));
        FtpUtil.download(connectBean,downloadBean.getRemoteFileName(),dir);
    }

    @Override
    public void createDir(ConnectBean connectBean, String remotePath) throws IOException {
        FtpUtil.createDir(connectBean,remotePath);
    }
}
