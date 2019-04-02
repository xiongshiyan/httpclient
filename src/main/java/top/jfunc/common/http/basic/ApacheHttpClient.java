package top.jfunc.common.http.basic;

import top.jfunc.common.http.base.AbstractApacheHttpTemplate;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.IoUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.CharsetUtils;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;

import java.io.*;
import java.util.Map;

import static top.jfunc.common.http.HttpConstants.DEFAULT_CHARSET;

/**
 * 使用Apache SmartHttpClient 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class ApacheHttpClient extends AbstractApacheHttpTemplate implements HttpClient {
    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, int connectTimeout, int readTimeout, String resultCharset) throws IOException{
        return template(ParamUtil.contactUrlParams(url , params , DEFAULT_CHARSET), Method.GET,null,null,
                ArrayListMultimap.fromMap(headers),
                connectTimeout,readTimeout , resultCharset,false,(s, b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) throws IOException {
        return template(url,Method.POST, contentType, (request -> setRequestBody(request , body ,bodyCharset)),
                ArrayListMultimap.fromMap(headers),
                connectTimeout, readTimeout , resultCharset,false, (s, b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout) throws IOException {
        return template(url, Method.GET,null,null, headers,
                connectTimeout,readTimeout , null,false,
                (s, b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(String url, ArrayListMultimap<String, String> headers, File file, int connectTimeout, int readTimeout) throws IOException {
        return template(url, Method.GET,null,null, headers ,
                connectTimeout,readTimeout , null,false,
                (s, b,r,h)-> IoUtil.copy2File(b, file));
    }


    @Override
    public String upload(String url, ArrayListMultimap<String,String> headers, int connectTimeout, int readTimeout, String resultCharset, FormFile... files) throws IOException{
        return template(url,Method.POST, null, (request -> addFormFiles(request, files)),
                headers, connectTimeout, readTimeout , resultCharset,false, (s, b,r,h)-> IoUtil.read(b ,r));
    }

    protected void addFormFiles(HttpEntityEnclosingRequest request, FormFile[] files) throws UnsupportedEncodingException {
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setCharset(CharsetUtils.get(DEFAULT_CHARSET));

        for (FormFile formFile : files) {
            multipartEntityBuilder.addBinaryBody(formFile.getParameterName(), formFile.getInStream() , ContentType.parse(formFile.getContentType()) , formFile.getFilName());
        }
        HttpEntity reqEntity = multipartEntityBuilder.build();
        request.setEntity(reqEntity);
    }


    @Override
    public String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset, FormFile... files) throws IOException {
        return template(url,Method.POST, null, (request -> addFormFiles(request, params ,files)),
                headers, connectTimeout, readTimeout , resultCharset,false, (s, b,r,h)-> IoUtil.read(b ,r));
    }

    protected void addFormFiles(HttpEntityEnclosingRequest request, ArrayListMultimap<String, String> params ,FormFile[] files) throws UnsupportedEncodingException {
        final MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setCharset(CharsetUtils.get(DEFAULT_CHARSET));

        if(null != params){
            params.keySet().forEach(key->params.get(key).forEach(value->multipartEntityBuilder.addTextBody(key , value)));
        }

        for (FormFile formFile : files) {
            multipartEntityBuilder.addBinaryBody(formFile.getParameterName(), formFile.getInStream() , ContentType.parse(formFile.getContentType()) , formFile.getFilName());
        }
        HttpEntity reqEntity = multipartEntityBuilder.build();
        request.setEntity(reqEntity);
    }
}
