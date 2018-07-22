package cn.zytx.common.http.basic;

import cn.zytx.common.http.base.AbstractOkHttp3;
import cn.zytx.common.http.ParamUtil;
import cn.zytx.common.http.Method;
import cn.zytx.common.utils.ArrayListMultimap;
import cn.zytx.common.utils.IoUtil;
import okhttp3.Headers;
import okhttp3.MultipartBody;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static cn.zytx.common.http.HttpConstants.DEFAULT_CHARSET;

/**
 * @author xiongshiyan at 2018/1/11
 */
public class OkHttp3Client extends AbstractOkHttp3 implements HttpClient {
    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, int connectTimeout, int readTimeout, String resultCharset) throws IOException {
        return template(ParamUtil.contactUrlParams(url , params , DEFAULT_CHARSET),Method.GET,null,null,
                ArrayListMultimap.fromMap(headers),
                connectTimeout,readTimeout,resultCharset,false,(b,r,h)-> IoUtil.read(b ,r));
    }

    /**
     * @param bodyCharset 请求体的编码，不支持，需要在contentType中指定
     */
    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) throws IOException {
        return template(url, Method.POST, contentType, d->setRequestBody(d , Method.POST , stringBody(body , contentType)) ,
                ArrayListMultimap.fromMap(headers),
                connectTimeout,readTimeout,resultCharset,false,
                (b,r,h)-> IoUtil.read(b ,r));
    }

    @Override
    public byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout) throws IOException {
        return template(url,Method.GET,null,null, headers ,
                connectTimeout,readTimeout,null,false,
                (b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(String url, ArrayListMultimap<String, String> headers, File file, int connectTimeout, int readTimeout) throws IOException {
        return template(url,Method.GET,null,null, headers ,
                connectTimeout,readTimeout,null,false,
                (b,r,h)-> IoUtil.copy2File(b, file));
    }

    @Override
    public String upload(String url, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset, FormFile... files) throws IOException {
        MultipartBody requestBody = getFilesBody(files);

        return template(url, Method.POST, null , d->setRequestBody(d, Method.POST , requestBody), headers,
                connectTimeout,readTimeout,resultCharset,false,
                (b,r,h)-> IoUtil.read(b ,r));
    }

    protected MultipartBody getFilesBody(FormFile... files) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (FormFile formFile : files) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + formFile.getParameterName() + "\"; filename=\"" + formFile.getFilName() + "\"") ,
                    inputStreamBody(formFile.getContentType() , formFile.getInStream() , formFile.getFileLen()));
        }
        return builder.build();
    }

    @Override
    public String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset , FormFile... files) throws IOException {
        MultipartBody requestBody = getFilesBody(params , files);

        return template(url, Method.POST, null , d->setRequestBody(d, Method.POST , requestBody), headers,
                connectTimeout,readTimeout,resultCharset,false,
                (b,r,h)-> IoUtil.read(b ,r));
    }

    protected MultipartBody getFilesBody(ArrayListMultimap<String, String> params , FormFile... files) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if(null != params){
            params.keySet().forEach(key->params.get(key).forEach(value->builder.addFormDataPart(key , value)));
        }

        for (FormFile formFile : files) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + formFile.getParameterName() + "\"; filename=\"" + formFile.getFilName() + "\"") ,
                    inputStreamBody(formFile.getContentType() , formFile.getInStream() , formFile.getFileLen()));
        }
        return builder.build();
    }
}
