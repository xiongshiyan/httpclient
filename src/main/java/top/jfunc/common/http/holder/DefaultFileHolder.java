package top.jfunc.common.http.holder;

import java.io.File;

/**
 * 默认的 FileHolder 实现
 * @author xiongshiyan
 */
public class DefaultFileHolder implements FileHolder {
    /**
     * 为文件下载确定信息
     */
    private File file = null;

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public FileHolder setFile(File file) {
        this.file = file;
        return this;
    }

    @Override
    public FileHolder setFile(String filePath) {
        this.file = new File(filePath);
        return this;
    }
}
