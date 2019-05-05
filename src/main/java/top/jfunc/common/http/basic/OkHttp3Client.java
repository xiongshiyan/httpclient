package top.jfunc.common.http.basic;

import okhttp3.MultipartBody;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.AbstractOkHttp3HttpTemplate;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.IoUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author xiongshiyan at 2018/1/11
 */
public class OkHttp3Client extends AbstractOkHttp3HttpTemplate implements HttpClient {

    @Override
    public OkHttp3Client setConfig(Config config) {
        super.setConfig(config);
        return this;
    }

    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException {
        return template(ParamUtil.contactUrlParams(url , params , getDefaultBodyCharset()),Method.GET,null,null,
                ArrayListMultimap.fromMap(headers),
                connectTimeout,readTimeout,resultCharset,false,(s,b,r,h)-> IoUtil.read(b ,r));
    }

    /**
     * @param bodyCharset 请求体的编码，不支持，需要在contentType中指定
     */
    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException {
        return template(url, Method.POST, contentType, d->setRequestBody(d , Method.POST , stringBody(body , contentType)) ,
                ArrayListMultimap.fromMap(headers),
                connectTimeout,readTimeout,resultCharset,false,
                (s,b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url,Method.GET,null,null, headers ,
                connectTimeout,readTimeout,null,false,
                (s,b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(String url, ArrayListMultimap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url,Method.GET,null,null, headers ,
                connectTimeout,readTimeout,null,false,
                (s,b,r,h)-> IoUtil.copy2File(b, file));
    }

    @Override
    public String upload(String url, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException {
        MultipartBody requestBody = filesBody(null , files);

        return template(url, Method.POST, null , d->setRequestBody(d, Method.POST , requestBody), headers,
                connectTimeout,readTimeout,resultCharset,false,
                (s,b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , FormFile... files) throws IOException {
        MultipartBody requestBody = filesBody(params , files);

        return template(url, Method.POST, null , d->setRequestBody(d, Method.POST , requestBody), headers,
                connectTimeout,readTimeout,resultCharset,false,
                (s,b,r,h)-> IoUtil.read(b ,r));
    }
}
