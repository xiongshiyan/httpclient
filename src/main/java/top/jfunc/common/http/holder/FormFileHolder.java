package top.jfunc.common.http.holder;

import top.jfunc.common.http.base.FormFile;

/**
 * 上传文件处理器，新增、获取上传文件的信息
 * @author xiongshiyan
 */
public interface FormFileHolder{
    /**
     * 上传文件信息
     * @return 上传文件信息
     */
    FormFile[] getFormFiles();

    /**
     * 新增文件上传信息
     * @param formFiles 上传的文件
     * @return this
     */
    FormFileHolder addFormFile(FormFile... formFiles);

    /**
     * 新增文件上传信息
     * @param formFiles 上传的文件
     * @return this
     */
    FormFileHolder addFormFiles(Iterable<FormFile> formFiles);
}
