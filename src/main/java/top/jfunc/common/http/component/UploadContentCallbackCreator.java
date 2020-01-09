package top.jfunc.common.http.component;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface UploadContentCallbackCreator<CC> extends ContentCallbackCreator<CC>{
    /**
     * 真正实现获取 ContentCallback<CC>
     * @param params params
     * @param paramCharset paramCharset
     * @param formFiles formFiles
     * @return ContentCallback<CC>
     * @throws IOException IOException
     */
    ContentCallback<CC> create(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException;
}
