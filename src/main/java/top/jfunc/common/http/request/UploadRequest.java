package top.jfunc.common.http.request;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.utils.MultiValueMap;

/**
 * 文件上传请求
 * @author xiongshiyan at 2019/5/18 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface UploadRequest extends HttpRequest{
    /**
     * Form参数
     * @return Form参数
     */
    MultiValueMap<String, String> getFormParams();

    /**
     * 上传文件信息
     * @return 上传文件信息
     */
    FormFile[] getFormFiles();
}
