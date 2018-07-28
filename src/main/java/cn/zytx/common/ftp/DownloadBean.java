package cn.zytx.common.ftp;

/**
 * @author xiongshiyan at 2018/4/8
 */
public class DownloadBean {
    private String remotePath;
    private String remoteFileName;
    private String localFileName;

    public DownloadBean() {
    }

    public DownloadBean(String remotePath, String remoteFileName, String localFileName) {
        this.remotePath = remotePath;
        this.remoteFileName = remoteFileName;
        this.localFileName = localFileName;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public DownloadBean setRemotePath(String remotePath) {
        this.remotePath = remotePath;
        return this;
    }

    public String getRemoteFileName() {
        return remoteFileName;
    }

    public DownloadBean setRemoteFileName(String remoteFileName) {
        this.remoteFileName = remoteFileName;
        return this;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public DownloadBean setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
        return this;
    }
}
