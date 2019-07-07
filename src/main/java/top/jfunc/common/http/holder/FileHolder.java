package top.jfunc.common.http.holder;

import java.io.File;

/**
 * 下载文件处理器，新增、获取下载到哪个文件的信息
 * @author xiongshiyan
 */
public interface FileHolder extends Holder{
    /**
     * 下载到的文件
     * @return file
     */
    File getFile();

    /**
     * 设置下载到哪个文件
     * @param file file
     * @return this
     */
    FileHolder setFile(File file);

    /**
     * 设置下载到哪个文件的路径
     * @param filePath 文件路径
     * @return this
     */
    default FileHolder setFile(String filePath){
        return setFile(new File(filePath));
    }
}
