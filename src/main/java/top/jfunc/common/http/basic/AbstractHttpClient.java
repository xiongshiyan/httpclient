package top.jfunc.common.http.basic;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.AbstractConfigurableHttp;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.HttpTemplate;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 实现者只需要实现HttpTemplate接口、处理POST Body、文件上传Body即可
 * @see HttpTemplate
 * @see AbstractHttpClient#bodyContentCallback(String, String, String)
 * @see AbstractHttpClient#uploadContentCallback(MultiValueMap, FormFile[])
 * @author xiongshiyan at 2019/5/9 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractHttpClient<CC> extends AbstractConfigurableHttp implements HttpClient, HttpTemplate<CC> {

    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException{
        return template(ParamUtil.contactUrlParams(url , params , getDefaultBodyCharset()), Method.GET,null,null,
                ArrayListMultiValueMap.fromMap(headers),
                connectTimeout,readTimeout , resultCharset,false,(s, b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException {
        return template(url, Method.POST, contentType, bodyContentCallback(body, bodyCharset, contentType),
                ArrayListMultiValueMap.fromMap(headers),
                connectTimeout, readTimeout , resultCharset,false, (s, b,r,h)-> IoUtil.read(b ,r));
    }

    /**
     * 处理请求体的回调
     * @param body 请求体
     * @param bodyCharset 编码
     * @param contentType Content-Type
     * @return ContentCallback<CC>
     * @throws IOException IOException
     */
    abstract protected ContentCallback<CC> bodyContentCallback(String body , String bodyCharset , String contentType) throws IOException;

    @Override
    public byte[] getAsBytes(String url, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET,null,null, headers,
                connectTimeout,readTimeout , null,false,
                (s, b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(String url, MultiValueMap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET,null,null, headers ,
                connectTimeout,readTimeout , null,false,
                (s, b,r,h)-> IoUtil.copy2File(b, file));
    }


    @Override
    public String upload(String url, MultiValueMap<String,String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException{
        return template(url, Method.POST, null, uploadContentCallback(null , files),
                headers, connectTimeout, readTimeout , resultCharset,false, (s, b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException {
        return template(url, Method.POST, null, uploadContentCallback(params , files),
                headers, connectTimeout, readTimeout , resultCharset,false, (s, b,r,h)-> IoUtil.read(b ,r));
    }

    /**
     * 处理文件上传
     * @param params 参数，k-v，可能为null
     * @param formFiles 文件信息
     * @return ContentCallback<CC>
     * @throws IOException IOException
     */
    protected abstract ContentCallback<CC> uploadContentCallback(MultiValueMap<String, String> params , FormFile[] formFiles) throws IOException;
}