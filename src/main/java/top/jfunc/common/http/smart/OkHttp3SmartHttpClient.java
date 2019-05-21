package top.jfunc.common.http.smart;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.http.basic.OkHttp3Client;
import top.jfunc.common.http.request.DownLoadRequest;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;
import top.jfunc.common.http.request.UploadRequest;
import top.jfunc.common.http.request.impl.GetRequest;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * 使用OkHttp3 实现的Http请求类
 * @author xiongshiyan at 2018/1/11
 */
public class OkHttp3SmartHttpClient extends OkHttp3Client implements SmartHttpClient, SmartHttpTemplate<Request.Builder> {

    @Override
    public OkHttp3SmartHttpClient setConfig(Config config) {
        super.setConfig(config);
        return this;
    }

    @Override
    public <R> R template(HttpRequest httpRequest, Method method , ContentCallback<Request.Builder> contentCallback , ResultCallback<R> resultCallback) throws IOException {
        okhttp3.Response response = null;
        InputStream inputStream = null;
        try {
            String completedUrl = handleUrlIfNecessary(httpRequest.getUrl() , httpRequest.getRouteParams() ,httpRequest.getQueryParams() , httpRequest.getBodyCharset());

            //1.构造OkHttpClient
            OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                    .connectTimeout(getConnectionTimeoutWithDefault(httpRequest.getConnectionTimeout()), TimeUnit.MILLISECONDS)
                    .readTimeout(getReadTimeoutWithDefault(httpRequest.getReadTimeout()), TimeUnit.MILLISECONDS);
            //1.1如果存在就设置代理
            if(null != httpRequest.getProxyInfo()){
                clientBuilder.proxy(httpRequest.getProxyInfo().getProxy());
            }

            ////////////////////////////////////ssl处理///////////////////////////////////
            if(ParamUtil.isHttps(completedUrl)){
                initSSL(clientBuilder , getHostnameVerifierWithDefault(httpRequest.getHostnameVerifier()) ,
                        getSSLSocketFactoryWithDefault(httpRequest.getSslSocketFactory()) ,
                        getX509TrustManagerWithDefault(httpRequest.getX509TrustManager()));
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            //给子类复写的机会
            doWithBuilder(clientBuilder , ParamUtil.isHttps(completedUrl));

            OkHttpClient client = clientBuilder.build();

            doWithClient(client);

            //2.1设置URL
            Request.Builder builder = new Request.Builder().url(completedUrl);

            //2.2处理请求体
            if(null != contentCallback && method.hasContent()){
                contentCallback.doWriteWith(builder);
            }

            //2.3设置headers
            MultiValueMap<String, String> headers = mergeDefaultHeaders(httpRequest.getHeaders());

            headers = handleCookieIfNecessary(completedUrl, headers);

            setRequestHeaders(builder , httpRequest.getContentType() , headers);

            //3.构造请求
            Request okRequest = builder.build();

            //4.执行请求
            response = client.newCall(okRequest).execute();

            //5.获取响应
            inputStream = getStreamFrom(response , httpRequest.isIgnoreResponseBody());

            //6.处理header，包括Cookie的处理
            boolean includeHeaders = httpRequest.isIncludeHeaders();
            if(supportCookie()){
                includeHeaders = top.jfunc.common.http.request.HttpRequest.INCLUDE_HEADERS;
            }
            MultiValueMap<String, String> parseHeaders = parseHeaders(response, includeHeaders);

            //存入Cookie
            if(supportCookie()){
                if(null != getCookieHandler() && null != parseHeaders){
                    CookieHandler cookieHandler = getCookieHandler();
                    cookieHandler.put(URI.create(completedUrl) , parseHeaders);
                }
            }

            return resultCallback.convert(response.code() , inputStream,
                    getResultCharsetWithDefault(httpRequest.getResultCharset()),
                    parseHeaders);
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(response);
        }
    }

    @Override
    public Response get(HttpRequest req) throws IOException {
        HttpRequest request = beforeTemplate(req);
        Response response = template(request , Method.GET , null , Response::with);
        return afterTemplate(request , response);
    }
    /**
     * @param req 请求体的编码，不支持，需要在contentType中指定
     */
    @Override
    public Response post(StringBodyRequest req) throws IOException {
        StringBodyRequest request = beforeTemplate(req);
        String body = request.getBody();
        Response response = template(request, Method.POST ,
                d -> setRequestBody(d, Method.POST, stringBody(body, request.getContentType())), Response::with);
        return afterTemplate(request , response);
    }


    @Override
    public Response httpMethod(HttpRequest req, Method method) throws IOException {
        HttpRequest request = beforeTemplate(req);
        ContentCallback<Request.Builder> contentCallback = null;
        if(method.hasContent() && request instanceof StringBodyRequest){
            String body = ((StringBodyRequest)request).getBody();
            contentCallback = d -> setRequestBody(d, method, stringBody(body, request.getContentType()));
        }
        Response response = template(request, method , contentCallback , Response::with);
        return afterTemplate(request , response);
    }


    @Override
    public byte[] getAsBytes(HttpRequest req) throws IOException {
        HttpRequest request = beforeTemplate(req);
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File download(DownLoadRequest req) throws IOException {
        DownLoadRequest request = beforeTemplate(req);
        return template(request , Method.GET , null , (s, b, r, h)-> IoUtil.copy2File(b, request.getFile()));
    }

    @Override
    public Response upload(UploadRequest req) throws IOException {
        UploadRequest request = beforeTemplate(req);
        MultipartBody requestBody = filesBody(request.getFormParams() , request.getFormFiles());
        Response response = template(request, Method.POST ,
                d -> setRequestBody(d, Method.POST, requestBody) ,
                Response::with);
        return afterTemplate(request , response);
    }

    @Override
    public Response afterTemplate(HttpRequest request, Response response) throws IOException{
        if(request.isRedirectable() && response.needRedirect()){
            return get(GetRequest.of(response.getRedirectUrl()));
        }
        return response;
    }
}
