package top.jfunc.common.http.download;

import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.http.request.DownloadRequest;
import top.jfunc.common.http.request.RequestCreator;
import top.jfunc.common.http.smart.SmartHttpClient;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author xiongshiyan at 2020/2/16 , contact me with email yanshixiong@126.com or phone 15208384257
 */
class DownloadUtil {
    /**
     * 获取网络文件大小
     */
    static long getNetFileLength(SmartHttpClient smartHttpClient , DownloadRequest downloadRequest) throws IOException {
        DownloadRequest cloneRequest = RequestCreator.clone(downloadRequest);
        MultiValueMap<String, String> multiValueMap = smartHttpClient.head(cloneRequest);
        if(multiValueMap.containsKey(HttpHeaders.CONTENT_LENGTH)){
            return Long.parseLong(multiValueMap.getFirst(HttpHeaders.CONTENT_LENGTH));
        }else {
            return Long.parseLong(multiValueMap.getFirst(HttpHeaders.CONTENT_LENGTH.toLowerCase()));
        }
    }

    /**
     *
     * 保存文件格式为 total:downloadLength
     * 保存时覆盖
     * @param file 下载的文件，在这个文件相同的目录下新建一个新临时文件{file}.conf
     * @param totalLength total
     * @param downloadLength 下载量，没有就null
     * @throws IOException IOException
     */
    static void saveFileLength(File file , long totalLength , Long downloadLength) throws IOException{
        File tempFile = getConfFile(file);
        //保存总量但是文件已经存在，说明已经下载一部分了
        if(null == downloadLength && tempFile.exists()){
            return;
        }
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)){
            String toWrite = totalLength + ":" + (null == downloadLength ? 0 : downloadLength);
            outputStream.write(toWrite.getBytes());
            outputStream.flush();
        }
    }

    /**
     * 根据临时文件获取文件已经下载的数量
     * @param file 下载的文件
     * @return 下载量
     * @throws IOException IOException
     */
    static long readDownloadedLength(File file) throws IOException{
        File confFile = getConfFile(file);
        if(!confFile.exists()){
            return 0;
        }
        try (FileInputStream inputStream = new FileInputStream(confFile)){
            byte[] buffer = new byte[128];
            int len = inputStream.read(buffer);
            String temp = new String(buffer , 0 , len);
            return Long.parseLong(temp.split(":")[1]);
        }
    }

    /**
     * 获取文件已经下载量
     * @param file 下载的文件
     * @return 文件大小
     */
    static long getDownloadedLength(File file){
        if(!file.exists()){
            return 0;
        }
        return file.length();
    }

    static boolean deleteConfFile(File file){
        File confFile = getConfFile(file);
        if(confFile.exists()){
            return confFile.delete();
        }
        return true;
    }

    private static File getConfFile(File file) {
        return new File(file.getAbsolutePath() + ".conf");
    }
}
