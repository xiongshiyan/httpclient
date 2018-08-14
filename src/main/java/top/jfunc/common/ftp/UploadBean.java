package top.jfunc.common.ftp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author xiongshiyan at 2018/4/8
 */
public class UploadBean {
    private String remotePath;
    private String destFileName;
    private InputStream inputSteam;
    private String uploadFileName;

    public UploadBean() {
    }

    public UploadBean(String remotePath, String destFileName, InputStream inputSteam) {
        this.remotePath = remotePath;
        this.destFileName = destFileName;
        this.inputSteam = inputSteam;
    }
    public UploadBean(String remotePath, String destFileName, String uploadFileName) {
        this.remotePath = remotePath;
        this.destFileName = destFileName;
        this.uploadFileName = uploadFileName;
        try {
            this.inputSteam = new FileInputStream(uploadFileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getRemotePath() {
        return remotePath;
    }

    public UploadBean setRemotePath(String remotePath) {
        this.remotePath = remotePath;
        return this;
    }

    public String getDestFileName() {
        return destFileName;
    }

    public UploadBean setDestFileName(String destFileName) {
        this.destFileName = destFileName;
        return this;
    }

    public InputStream getInputSteam() {
        return inputSteam;
    }

    public UploadBean setInputSteam(InputStream inputSteam) {
        this.inputSteam = inputSteam;
        return this;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }
}
