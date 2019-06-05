package top.jfunc.common.http.request;

import top.jfunc.common.http.holder.FileHolder;

import java.io.File;

/**
 * 文件下载请求
 * @author xiongshiyan
 */
public interface DownLoadRequest extends HttpRequest {
    /**
     * 下载到的文件
     * @return file
     */
    default File getFile(){
        return fileHolder().getFile();
    }

    /**
     * 设置下载到哪个文件
     * @param file file
     * @return this
     */
    //DownLoadRequest setFile(File file);

    /**
     * 返回文件信息处理器
     * @return fileHolder must not be null
     */
    FileHolder fileHolder();
}
