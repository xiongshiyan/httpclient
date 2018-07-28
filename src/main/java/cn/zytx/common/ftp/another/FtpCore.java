package cn.zytx.common.ftp.another;

import cn.zytx.common.ftp.ConnectBean;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FTP核心操作
 */
class FtpCore {
    private FTPClient ftpClient;
    private static final Logger log = LoggerFactory.getLogger(FtpCore.class);
 
    /**
     * 根路径为"/",如果需要链接服务器之后跳转到路径，则在path中定义
     */
    public boolean connectServer(ConnectBean ftpConfig) throws IOException {
        String server = ftpConfig.getHost();
        int port = ftpConfig.getPort();
        String username = ftpConfig.getUsername();
        String password = ftpConfig.getPassword();
        String path = ftpConfig.getPath();
        return connectServer(server, port,ftpConfig.getEncoding(), username, password, path);
    }
 
    /**
     * 连接ftp服务器
     * @param server 服务器ip
     * @param port 端口，通常为21
     * @param username 用户名
     * @param password 密码
     * @param path 进入服务器之后的默认路径
     * @return 连接成功返回true，否则返回false
     * @throws IOException IO异常
     */
 
     boolean connectServer(String server, int port,String encoding, String username,String password, String path) throws IOException {
        ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.setControlEncoding(encoding);
 
        log.info("Connected to " + server + ".");
        log.info("FTP server reply code:" + ftpClient.getReplyCode());
 
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            if (ftpClient.login(username, password)) {
                // Path is the sub-path of the FTP path
 
                if (null != path && path.length() != 0) {
                    ftpClient.changeWorkingDirectory(path);
                }
                return true;
            }
        }
        disconnect();
        return false;
    }
 
    /**
     * 断开与远程服务器的连接
     */
     void disconnect() throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }
 
    /**
     * 
     * 从FTP服务器上下载文件,支持断点续传，下载百分比汇报
     * @param remote 远程文件路径及名称
     * @param local 本地文件完整绝对路径
     * @return 下载的状态
     * @throws IOException IO异常
     */
 
     DownloadStatus download(String remote, String local)throws IOException {
 
        // 设置被动模式
        ftpClient.enterLocalPassiveMode();
        // 设置以二进制方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        DownloadStatus result;
 
        // 检查远程文件是否存在
        FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
 
        if (files.length != 1) {
            log.error("远程文件不存在");
            return DownloadStatus.RemoteFileNotExist;
        }
 
        long lRemoteSize = files[0].getSize();
 
        File f = new File(local);
 
        // 本地存在文件，进行断点下载
        if (f.exists()) {
            long localSize = f.length();
            // 判断本地文件大小是否大于远程文件大小
            if (localSize >= lRemoteSize) {
                log.error("本地文件大于远程文件，下载中止");
                return DownloadStatus.LocalFileBiggerThanRemoteFile;
            }
 
            // 进行断点续传，并记录状态
 
            FileOutputStream out = new FileOutputStream(f, true);
 
            ftpClient.setRestartOffset(localSize);
 
            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
            byte[] bytes = new byte[1024];
            long step = lRemoteSize / 100;
            step = step == 0 ? 1 : step;// 文件过小，step可能为0
            long process = localSize / step;
            int c;
 
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
                localSize += c;
                long nowProcess = localSize / step;
                if (nowProcess > process) {
                    process = nowProcess;
                    if (process % 10 == 0) {
                        log.debug("下载进度：" + process);
                    }
                }
            }
 
            in.close();
            out.close();
            boolean isDo = ftpClient.completePendingCommand();
 
            if (isDo) {
                result = DownloadStatus.DownloadFromBreakSuccess;
            } else {
                result = DownloadStatus.DownloadFromBreakFailed;
            }
 
        } else {
 
            OutputStream out = new FileOutputStream(f);
            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
            byte[] bytes = new byte[1024];
            long step = lRemoteSize / 100;
            step = step == 0 ? 1 : step;// 文件过小，step可能为0
            long process = 0;
            long localSize = 0L;
            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
                localSize += c;
                long nowProcess = localSize / step;
                if (nowProcess > process) {
                    process = nowProcess;
                    if (process % 10 == 0) {
                        log.debug("下载进度：" + process);
                    }
                }
            }
            in.close();
            out.close();
            boolean upNewStatus = ftpClient.completePendingCommand();
            if (upNewStatus) {
                result = DownloadStatus.DownloadNewSuccess;
            } else {
                result = DownloadStatus.DownloadNewFailed;
            }
        }
        return result;
    }
 
     boolean changeDirectory(String path) throws IOException {
        return ftpClient.changeWorkingDirectory(path);
    }
 
     boolean removeDirectory(String path) throws IOException {
        return ftpClient.removeDirectory(path);
    }
 
     boolean removeDirectory(String path, boolean isAll) throws IOException {
        if (!isAll) {
            return removeDirectory(path);
        }
 
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr == null || ftpFileArr.length == 0) {
            return removeDirectory(path);
        }
 
        for (FTPFile ftpFile : ftpFileArr) {
            String name = ftpFile.getName();
            if (ftpFile.isDirectory()) {
                log.debug("* [sD]Delete subPath [" + path + "/" + name + "]");
                if (!".".equals(ftpFile.getName())
                && (!"..".equals(ftpFile.getName()))) {
                    removeDirectory(path + "/" + name, true);
                }
            } else if (ftpFile.isFile()) {
                log.debug("* [sF]Delete file [" + path + "/" + name + "]");
                deleteFile(path + "/" + name);
            } else if (ftpFile.isSymbolicLink()) {
            } else if (ftpFile.isUnknown()) {
            }
        }
        return ftpClient.removeDirectory(path);
 
    }
 
    /**
     * 查看目录是否存在
     */
     boolean isDirectoryExists(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        for (FTPFile ftpFile : ftpFileArr) {
            if (ftpFile.isDirectory() && ftpFile.getName().equalsIgnoreCase(path)) {
                flag = true;
                break;
            }
        }
        return flag;
    }
 
    /**
     * 得到某个目录下的文件名列表
     */
 
     List<String> getFileList(String path) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles(path);
        List<String> retList = new ArrayList<String>();
        if (ftpFiles == null || ftpFiles.length == 0) {
            return retList;
        }
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isFile()) {
                retList.add(ftpFile.getName());
            }
        }
        return retList;
    }
 
     boolean deleteFile(String pathName) throws IOException {
        return ftpClient.deleteFile(pathName);
    }
 
    /**
     * 上传文件到FTP服务器，支持断点续传
     * @param local 本地文件名称，绝对路径
     * @param remote 远程文件路径，按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构
     * @return 上传结果
     * @throws IOException IO异常
     */
 
     UploadStatus upload(String local, String remote) throws IOException {
 
        // 设置PassiveMode传输
        ftpClient.enterLocalPassiveMode();
        // 设置以二进制流的方式传输
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setControlEncoding("GBK");
        UploadStatus result;
        // 对远程目录的处理
        String remoteFileName = remote;
        if (remote.contains("/")) {
            remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
            // 创建服务器远程目录结构，创建失败直接返回
            if (createDirectory(remote) == UploadStatus.CreateDirectoryFail) {
                return UploadStatus.CreateDirectoryFail;
            }
        }
 
        // 检查远程是否存在文件
        FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("UTF-8"), "iso-8859-1"));
 
        if (files.length == 1) {
            long remoteSize = files[0].getSize();
            File f = new File(local);
            long localSize = f.length();
            if (remoteSize == localSize) { // 文件存在
                return UploadStatus.FileExits;
            } else if (remoteSize > localSize) {
                return UploadStatus.RemoteFileBiggerThanLocalFile;
            }
 
            // 尝试移动文件内读取指针,实现断点续传
            result = uploadFile(remoteFileName, f, remoteSize);
 
            // 如果断点续传没有成功，则删除服务器上文件，重新上传
            if (result == UploadStatus.UploadFromBreakFailed) {
                if (!ftpClient.deleteFile(remoteFileName)) {
                    return UploadStatus.DeleteRemoteFaild;
                }
                result = uploadFile(remoteFileName, f, 0);
            }
        } else {
            result = uploadFile(remoteFileName, new File(local), 0);
        }
 
        return result;
 
    }
 
    /**
     * 
     * 递归创建远程服务器目录
     * 
     * @param remote 远程服务器文件绝对路径
     * @return 目录创建是否成功
     */
 
     UploadStatus createDirectory(String remote) throws IOException {
 
        UploadStatus status = UploadStatus.CreateDirectorySuccess;
        String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
        if (!"/".equalsIgnoreCase(directory)
            && !ftpClient.changeWorkingDirectory(new String(directory
                .getBytes("UTF-8"), "iso-8859-1"))) {
 
            // 如果远程目录不存在，则递归创建远程服务器目录
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes("UTF-8"), "iso-8859-1");
                if (!ftpClient.changeWorkingDirectory(subDirectory)) {
                    if (ftpClient.makeDirectory(subDirectory)) {
                        ftpClient.changeWorkingDirectory(subDirectory);
                    } else {
                        log.error("创建目录失败");
                        return UploadStatus.CreateDirectoryFail;
                    }
                }
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return status;
    }
 
    /**
     * 
     * 上传文件到服务器,新上传和断点续传
     * 
     * @param remoteFile 远程文件名，在上传之前已经将服务器工作目录做了改变
     * 
     * @param localFile 本地文件File句柄，绝对路径
     * 
     */
 
     UploadStatus uploadFile(String remoteFile, File localFile, long remoteSize) throws IOException {
 
        UploadStatus status;
        long step = localFile.length() / 100;
        step = step == 0 ? 1 : step;// 文件过小，step可能为0
        long process = 0;
        long localreadbytes = 0L;
        RandomAccessFile raf = new RandomAccessFile(localFile, "r");
        OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes("UTF-8"), "iso-8859-1"));
 
        // 断点续传
        if (remoteSize > 0) {
            ftpClient.setRestartOffset(remoteSize);
            process = remoteSize / step;
            raf.seek(remoteSize);
            localreadbytes = remoteSize;
        }
        byte[] bytes = new byte[1024];
        int c;
        while ((c = raf.read(bytes)) != -1) {
            out.write(bytes, 0, c);
            localreadbytes += c;
            if (localreadbytes / step != process) {
                process = localreadbytes / step;
                if (process % 10 == 0) {
                    log.debug("上传进度：" + process);
                }
            }
        }
        out.flush();
        raf.close();
        out.close();
        boolean result = ftpClient.completePendingCommand();
        if (remoteSize > 0) {
            status = result ? UploadStatus.UploadFromBreakSuccess
            : UploadStatus.UploadFromBreakFailed;
        } else {
            status = result ? UploadStatus.UploadNewFileSuccess
            : UploadStatus.UploadNewFileFailed;
        }
        return status;
    }
 
     InputStream downFile(String sourceFileName) throws IOException {
        return ftpClient.retrieveFileStream(sourceFileName);
    }
 
     boolean rename(String sourceFilePath,String targetFilePath) throws IOException {
        return ftpClient.rename(sourceFilePath, targetFilePath);
    }
}