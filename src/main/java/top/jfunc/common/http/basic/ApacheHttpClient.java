package top.jfunc.common.http.basic;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.ResultCallback;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import java.io.IOException;
import java.io.InputStream;

import static top.jfunc.common.http.util.ApacheUtil.*;

/**
 * 使用Apache HttpClient 实现的Http请求类
 * @author 熊诗言2017/12/01
 */
public class ApacheHttpClient extends AbstractHttpClient<HttpEntityEnclosingRequest> {
    @Override
    public  <R> R doInternalTemplate(String url, Method method , String contentType, ContentCallback<HttpEntityEnclosingRequest> contentCallback, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , boolean includeHeader , ResultCallback<R> resultCallback) throws Exception {
        //1.获取完成的URL，创建请求
        String completedUrl = addBaseUrlIfNecessary(url);
        ///*URIBuilder builder = new URIBuilder(url);
        //URI uri = builder.build();*/
        HttpUriRequest httpUriRequest = createHttpUriRequest(completedUrl , method);


        //2.设置请求参数
        setRequestProperty((HttpRequestBase) httpUriRequest,
                getConnectionTimeoutWithDefault(connectTimeout),
                getReadTimeoutWithDefault(readTimeout));

        //3.创建请求内容，如果有的话
        if(httpUriRequest instanceof HttpEntityEnclosingRequest){
            if(contentCallback != null){
                contentCallback.doWriteWith((HttpEntityEnclosingRequest)httpUriRequest);
            }
        }

        //4.设置请求头
        setRequestHeaders(httpUriRequest, contentType, mergeDefaultHeaders(headers));

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        InputStream inputStream = null;
        try {

            //5.创建http客户端
            ///CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpClientBuilder clientBuilder = getCloseableHttpClientBuilder(completedUrl, getHostnameVerifier(), getSSLContext());

            //给子类复写的机会
            doWithClient(clientBuilder);

            httpClient = clientBuilder.build();

            //6.发送请求
            response = httpClient.execute(httpUriRequest  , HttpClientContext.create());
            int statusCode = response.getStatusLine().getStatusCode();
            /*String resultString = EntityUtils.toString(response.getEntity(), resultCharset);*/

            entity = response.getEntity();

            inputStream = getStreamFrom(entity, false);

            return resultCallback.convert(statusCode , inputStream, getResultCharsetWithDefault(resultCharset),  parseHeaders(response , includeHeader));
        }finally {
            IoUtil.close(inputStream);
            EntityUtils.consumeQuietly(entity);
            IoUtil.close(response);
            IoUtil.close(httpClient);
        }
    }

    protected void doWithClient(HttpClientBuilder httpClientBuilder) throws Exception{
        //default do nothing, give children a chance to do more config
    }

    @Override
    protected ContentCallback<HttpEntityEnclosingRequest> bodyContentCallback(Method method , String body, String bodyCharset, String contentType) throws IOException {
        return request -> setRequestBody(request , body , bodyCharset);
    }

    @Override
    protected ContentCallback<HttpEntityEnclosingRequest> uploadContentCallback(MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException {
        return request -> upload0(request, params, paramCharset, formFiles);
    }

    @Override
    public String toString() {
        return "HttpClient implemented by Apache's httpcomponents";
    }
}
