package top.jfunc.common.ftp.another;

public enum UploadStatus {

        /**
         * 远程服务器相应目录创建失败
         */
        CreateDirectoryFail,
        /**
         *远程服务器创建目录成功
         */
        CreateDirectorySuccess,
        /**
         *上传新文件成功
         */
        UploadNewFileSuccess,
        /**
         *上传新文件失败
         */
        UploadNewFileFailed,
        /**
         *文件已经存在
         */
        FileExits,
        /**
         *远程文件大于本地文件
         */
        RemoteFileBiggerThanLocalFile,
        /**
         *断点续传成功
         */
        UploadFromBreakSuccess,
        /**
         *断点续传失败
         */
        UploadFromBreakFailed,
        /**
         *删除远程文件失败
         */
        DeleteRemoteFaild;
}