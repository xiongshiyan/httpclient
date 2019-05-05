package top.jfunc.common.http.basic;

import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.AbstractApacheHttpTemplate;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.IoUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 使用Apache SmartHttpClient 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class ApacheHttpClient extends AbstractApacheHttpTemplate implements HttpClient {

    @Override
    public ApacheHttpClient setConfig(Config config) {
        super.setConfig(config);
        return this;
    }

    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException{
        return template(ParamUtil.contactUrlParams(url , params , getDefaultBodyCharset()), Method.GET,null,null,
                ArrayListMultimap.fromMap(headers),
                connectTimeout,readTimeout , resultCharset,false,(s, b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException {
        return template(url,Method.POST, contentType, (request -> setRequestBody(request , body ,getBodyCharsetWithDefault(bodyCharset))),
                ArrayListMultimap.fromMap(headers),
                connectTimeout, readTimeout , resultCharset,false, (s, b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET,null,null, headers,
                connectTimeout,readTimeout , null,false,
                (s, b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(String url, ArrayListMultimap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET,null,null, headers ,
                connectTimeout,readTimeout , null,false,
                (s, b,r,h)-> IoUtil.copy2File(b, file));
    }


    @Override
    public String upload(String url, ArrayListMultimap<String,String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException{
        return template(url,Method.POST, null, (request -> addFormFiles(request, null , files)),
                headers, connectTimeout, readTimeout , resultCharset,false, (s, b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset, FormFile... files) throws IOException {
        return template(url,Method.POST, null, (request -> addFormFiles(request, params ,files)),
                headers, connectTimeout, readTimeout , resultCharset,false, (s, b,r,h)-> IoUtil.read(b ,r));
    }
}
