package top.jfunc.common.ftp.another;

import top.jfunc.common.ftp.ConnectBean;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * FTP门面
 * @author xiongshiyan
 */
public class FtpUtil {
    public static void upload(ConnectBean config, Map<String, String> localFilePathAndRemoteFileName) throws IOException {
       FtpCore ftp = new FtpCore(); ftp.connectServer(config);
       Set<Map.Entry<String, String>> file_set = localFilePathAndRemoteFileName.entrySet();
       for(Map.Entry<String, String> file:file_set){
           ftp.upload(file.getKey(), file.getValue());
       }
 
    }
 
    public static void download(ConnectBean config,List<String> remoteFiles,String localFolderPath) throws IOException {
        if(!localFolderPath.endsWith(File.separator)){
            localFolderPath += File.separator;
        }  
        File folderPath = new File(localFolderPath);
        if(!folderPath.isDirectory()){
            folderPath.mkdirs();
        }
       FtpCore ftp = new FtpCore();
       ftp.connectServer(config);
       for(String file:remoteFiles){
           List<String> files = ftp.getFileList(file);
            for(String f:files){
                String local = localFolderPath + f;
                ftp.download(f, local);
            }
       }
    }
 
    public static DownloadStatus download(ConnectBean config,String remoteFile,String localFolderPath) throws IOException {
        if(!localFolderPath.endsWith(File.separator)){
            localFolderPath += File.separator;
        }  
 
        File folderPath = new File(localFolderPath);
        if(!folderPath.isDirectory()){
            folderPath.mkdirs();
        }
 
       FtpCore ftp = new FtpCore();
       ftp.connectServer(config);
 
       String local = localFolderPath + remoteFile;
       return ftp.download(remoteFile, local);
    }

    public static void createDir(ConnectBean config,String remotePath) throws IOException{
        FtpCore ftp = new FtpCore();
        ftp.connectServer(config);
        ftp.createDirectory(remotePath);
    }

    public static List<String> getFileList(ConnectBean config,String path) throws IOException{
        FtpCore ftp = new FtpCore();
        ftp.connectServer(config);
        return ftp.getFileList(path);
    }
 
    public static boolean deleteFile(ConnectBean config,String pathName) throws IOException{
        FtpCore ftp = new FtpCore();
        ftp.connectServer(config);
        return ftp.deleteFile(pathName);
    }  
 
    public static boolean renameFile(ConnectBean config,String form,String to) throws IOException{
        FtpCore ftp = new FtpCore();
        ftp.connectServer(config);
        return ftp.rename(form, to);
    }
}