package cn.zytx.common.ftp.one;

import cn.zytx.common.ftp.ConnectBean;
import cn.zytx.common.ftp.DownloadBean;
import cn.zytx.common.ftp.UploadBean;
import cn.zytx.common.utils.IoUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author xiongshiyan
 * FTP 工具类
 */
public class FtpUtil {
    private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    /**
     * 创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
     * @param remotePath 远程目录
     * @throws IOException IO异常
     */
    public static boolean createDir(ConnectBean connectBean, String remotePath) throws IOException {
        FTPClient ftpClient = connect(connectBean);
        try {
            String directory = remotePath.endsWith("/") ? remotePath : remotePath +  "/";
            // 如果远程目录不存在，则递归创建远程服务器目录
            if ("/".equalsIgnoreCase(directory) || changeWorkingDirToRemotePath(ftpClient, directory)) {
                return true;
            }
            int start = directory.startsWith("/") ? 1 : 0;
            int end = directory.indexOf("/", start);

            String path = "";
            //String paths = "";
            while (true) {
                String subDirectory = new String(remotePath.substring(start, end).getBytes("GBK"), "iso-8859-1");
                path = path + "/" + subDirectory;
                if (!existDir(ftpClient,path)) {
                    if (ftpClient.makeDirectory(subDirectory)) {
                        changeWorkingDirToRemotePath(ftpClient, subDirectory);
                    } else {
                        logger.error("创建目录[" + subDirectory + "]失败");
                        changeWorkingDirToRemotePath(ftpClient, subDirectory);
                    }
                } else {
                    changeWorkingDirToRemotePath(ftpClient , subDirectory);
                }

                //paths = paths + "/" + subDirectory;
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        } finally {
            close(ftpClient);
        }
        return true;
    }


    /**
     * @see FtpUtil#upload(String, int, String, String, String, String, String, InputStream)
     */
    public static void upload(ConnectBean connectBean, UploadBean uploadBean) throws IOException{
        upload(connectBean,uploadBean.getRemotePath(),uploadBean.getDestFileName(),uploadBean.getInputSteam());
    }
    /**
     * @see FtpUtil#upload(String, int, String, String, String, String, String, InputStream)
     */
    public static void upload(ConnectBean connectBean,String remotePath, String destFileName, InputStream inputSteam) throws IOException{
        upload(connectBean.getHost(),connectBean.getPort(),connectBean.getEncoding(),connectBean.getUsername(),connectBean.getPassword(),
                remotePath,destFileName,inputSteam);
    }
    /**
     * 上传文件
     * @param host 主机
     * @param port 端口
     * @param encoding 编码
     * @param username 用户名
     * @param password 密码
     * @param remotePath 远程文件夹
     * @param destFileName 远程文件名字
     * @param inputSteam 流自动关闭流
     * @throws IOException 有任何IO异常就抛出
     */
    public static void upload(String host,int port,String encoding,String username,String password,
                              String remotePath, String destFileName, InputStream inputSteam) throws IOException{

        FTPClient ftpClient = connect(host, port, encoding, username, password);
        try {
            checkStatus(ftpClient);
            changeWorkingDirToRemotePath(ftpClient,remotePath);
            // 设置处理文件的类型为字节流的形式,如果缺省该句 传输txt正常 但图片和其他格式的文件传输出现乱码
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.storeFile(destFileName, inputSteam);
            printReplay(ftpClient);
        } finally {
            IoUtil.close(inputSteam);
            close(ftpClient);
        }
    }

    /**
     * @see FtpUtil#download(String, int, String, String, String, String, String, String)
     */
    public static void download(ConnectBean connectBean, DownloadBean downloadBean) throws IOException{
        download(connectBean.getHost(),connectBean.getPort(),connectBean.getEncoding(),connectBean.getUsername(),connectBean.getPassword(),
                downloadBean.getRemotePath(),downloadBean.getRemoteFileName(),downloadBean.getLocalFileName());
    }

    /**
     * @see FtpUtil#download(String, int, String, String, String, String, String, String)
     */
    public static void download(ConnectBean connectBean, String remotePath, String remoteFileName, String localFileName) throws IOException{
        download(connectBean.getHost(),connectBean.getPort(),connectBean.getEncoding(),connectBean.getUsername(),connectBean.getPassword(),
                remotePath,remoteFileName,localFileName);
    }
    /**
     * 下载文件
     * @param host 主机
     * @param port 端口
     * @param encoding 编码
     * @param username 用户名
     * @param password 密码
     * @param remotePath 远程目录
     * @param remoteFileName 远程文件
     * @param localFileName 本地文件，可以是绝对路径或者相对路径
     * @throws IOException 任何IO异常
     */
    public static void download(String host,int port,String encoding,String username,String password,
                                String remotePath, String remoteFileName, String localFileName) throws IOException{

        FTPClient ftpClient = connect(host, port, encoding, username, password);
        try {
            isExist(ftpClient,remotePath,remoteFileName);
            checkStatus(ftpClient);
            changeWorkingDirToRemotePath(ftpClient,remotePath);
            downloadRemoteFile2LocalFile(ftpClient,remoteFileName, localFileName);
        } finally {
            close(ftpClient);
        }
    }

    private static void downloadRemoteFile2LocalFile(FTPClient ftpClient,String remoteFileName, String localFileName) throws IOException{
        InputStream is = null;
        OutputStream os = null;
        try{
            is = ftpClient.retrieveFileStream(remoteFileName);
            os = new FileOutputStream(localFileName);
            byte[] buffer = new byte[1024 * 4];
            int read = 0;
            long downloadSize = 0;
            while((read = is.read(buffer)) != -1){
                os.write(buffer, 0, read);
                downloadSize += read;
                printDownloadSize(downloadSize);
            }
            os.flush();
            logger.info("downloadSize:" + downloadSize);
        }
        finally{
            IoUtil.close(is);
            IoUtil.close(os);
            //completePendingCommand之前必须调用close，否则耗时非常长，需要等待服务端关闭
            try {
                ftpClient.completePendingCommand();
                printReplay(ftpClient);
            } catch (IOException e) {
                logger.error(e.getMessage() , e);
            }
        }
    }
    private static void printDownloadSize(long downloadSize){
        String suffix = "KB";
        float downloaded = downloadSize / 1024f;
        if(downloaded > 1024){
            downloaded = downloaded / 1024f;
            suffix = "MB";
        }
        logger.debug(String.format("download size:%.2f %s", downloaded, suffix));
    }

    private static boolean changeWorkingDirToRemotePath(FTPClient ftpClient,String remotePath) throws IOException{
        boolean flag = ftpClient.changeWorkingDirectory(remotePath);
        printReplay(ftpClient);
        return flag;
    }

    private static void printReplay(FTPClient ftpClient){
        logger.debug("Code: " + ftpClient.getReplyCode() + ":replyString: " + ftpClient.getReplyString());
    }
    /**
     *判断ftp服务器目录是否存在
     */
    private static boolean existDir(FTPClient ftpClient , String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 检查某个路径下某个文件是否存在
     * @param remotePath 远程目录
     * @param fileName 远程文件
     */
    private static boolean isExist(FTPClient ftpClient,String remotePath, String fileName) throws IOException{
        checkStatus(ftpClient);
        changeWorkingDirToRemotePath(ftpClient,remotePath);
        printReplay(ftpClient);
        FTPFile[] ftpFiles = ftpClient.listFiles(remotePath);
        printReplay(ftpClient);
        if(ftpFiles.length == 0){
            throw new IOException("Error, no file in remote directory.");
        }
        for(FTPFile file : ftpFiles){
            if(file.isDirectory()){
                continue;
            }
            if(file.getName().equals(fileName)){
                return true;
            }
        }
        return false;
    }
    /**
     * 本方法用户登录远程的FTP服务器
     * @return FTPClient 返回为FTPClient对象
     * @throws IOException IO异常
     */
    private static FTPClient loginFtp(String host,int port,String encoding,String username,String password) throws IOException{
        FTPClient ftpClient = new FTPClient();
        // 配置端口
        ftpClient.setDefaultPort(port);
        // 配置编码集
        ftpClient.setControlEncoding(encoding);
        // 连接FTP服务器
        ftpClient.connect(host);
        ftpClient.enterLocalPassiveMode();
        // 登陆FTP服务器 URLEncoder.encode(password,"UTF-8")
        if(ftpClient.login(username, password)){
            printReplay(ftpClient);
            logger.info(" login success !");
            if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
                logger.info(" connect success !");
                return ftpClient;
            }
        }
        throw new IOException("login FTP server error, please check it!");
    }

    public static FTPClient loginFtp(ConnectBean bean) throws IOException{
        return loginFtp(bean.getHost(),bean.getPort(),bean.getEncoding(),bean.getUsername(),bean.getPassword());
    }

    private static FTPClient connect(String host,int port,String encoding,String username,String password) throws IOException{
        FTPClient ftpClient = loginFtp(host,port,encoding,username,password);
        return ftpClient;
    }
    private static FTPClient connect(ConnectBean bean) throws IOException{
        FTPClient ftpClient = loginFtp(bean.getHost(),bean.getPort(),bean.getEncoding(),bean.getUsername(),bean.getPassword());
        return ftpClient;
    }

    private static void close(FTPClient ftpClient){
        if(null != ftpClient && ftpClient.isConnected()){
            try{
                logger.info("ftpClient logout");
                ftpClient.logout();
                ftpClient = null;
            }
            catch(IOException e){
                logger.error(e.getMessage() , e);
            }
        }
    }
    private static void checkStatus(FTPClient ftpClient) throws IOException{
        if(ftpClient == null || !ftpClient.isConnected()){
            throw new IOException("FTP未连接，请连接后再执行操作");
        }
    }

}
