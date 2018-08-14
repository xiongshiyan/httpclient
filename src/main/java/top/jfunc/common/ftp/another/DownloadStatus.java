package top.jfunc.common.ftp.another;

public enum DownloadStatus {

        /**
         * 下载文件成功
         */
        DownloadNewSuccess,
        /**
         *下载文件失败
         */
        DownloadNewFailed,
        /**
         *本地文件大于远程文件
         */
        LocalFileBiggerThanRemoteFile,
        /**
         *断点续传成功
         */
        DownloadFromBreakSuccess,
        /**
         *远程文件不存在
         */
        RemoteFileNotExist,
        /**
         *断点续传失败
         */
        DownloadFromBreakFailed;
}