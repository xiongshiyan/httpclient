package cn.zytx.common.ftp;

import java.io.IOException;

/**
 * @author xiongshiyan at 2018/4/8
 */
public interface Ftp {
    /**
     * 上传文件
     * @param connectBean 连接信息
     * @param uploadBean 上传文件信息
     * @throws IOException IO异常
     */
    public void upload(ConnectBean connectBean, UploadBean uploadBean) throws IOException;

    /**
     * 下载文件
     * @param connectBean 连接信息
     * @param downloadBean 下载文件信息
     * @throws IOException IO异常
     */
    public void download(ConnectBean connectBean, DownloadBean downloadBean) throws IOException;

    /**
     * 创建文件
     * @param connectBean 连接信息
     * @param remotePath 远程目录
     * @throws IOException IO异常
     */
    public void createDir(ConnectBean connectBean, String remotePath) throws IOException;
}
