package top.jfunc.common.http.basic;

import top.jfunc.common.http.HeaderRegular;
import top.jfunc.common.http.HttpStatus;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.*;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * 使用URLConnection实现的Http请求类
 * @author 熊诗言2017/11/24
 */
public class NativeHttpClient extends AbstractConfigurableHttp implements HttpTemplate<HttpURLConnection>, HttpClient {
    private static final String END = "\r\n";
    private static final String TWO_HYPHENS = "--";
    private static final String BOUNDARY = "*****xsyloveyou******";
    /**
     * 数据开始标志
     */
    private static final String PART_BEGIN_LINE = TWO_HYPHENS + BOUNDARY + END;
    /**
     * 数据结束标志
     */
    private static final String END_LINE = TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + END;

    @Override
    public <R> R template(String url, Method method, String contentType, ContentCallback<HttpURLConnection> contentCallback, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , boolean includeHeaders , ResultCallback<R> resultCallback) throws IOException {
        //默认的https校验
        // 后面会处理的，这里就不需要了 initDefaultSSL(sslVer);

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            //1.获取连接
            String completedUrl = addBaseUrlIfNecessary(url);
            connection = (HttpURLConnection)new java.net.URL(completedUrl).openConnection();

            ////////////////////////////////////ssl处理///////////////////////////////////
            if(connection instanceof HttpsURLConnection){
                //默认设置这些
                HttpsURLConnection con = (HttpsURLConnection)connection;
                initSSL(con , getHostnameVerifier() , getSSLSocketFactory());
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod(method.name());
            connection.setConnectTimeout(getConnectionTimeoutWithDefault(connectTimeout));
            connection.setReadTimeout(getReadTimeoutWithDefault(readTimeout));

            //2.处理header
            setRequestHeaders(connection, contentType, mergeDefaultHeaders(headers) , null);

            //3.留给子类复写的机会:给connection设置更多参数
            doWithConnection(connection);

            //4.写入内容，只对post有效
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(connection);
            }

            //5.连接
            connection.connect();

            //6.获取返回值
            int statusCode = connection.getResponseCode();

            inputStream = getStreamFrom(connection , statusCode , false);

            return resultCallback.convert(statusCode , inputStream, getResultCharsetWithDefault(resultCharset), parseHeaders(connection, includeHeaders));
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            //关闭顺序不能改变，否则服务端可能出现这个异常  严重: java.io.IOException: 远程主机强迫关闭了一个现有的连接
            //1 . 关闭连接
            disconnectQuietly(connection);
            //2 . 关闭流
            IoUtil.close(inputStream);
        }
    }

    protected MultiValueMap<String , String> parseHeaders(HttpURLConnection connect, boolean includeHeaders) {
        if(!includeHeaders){
            return new ArrayListMultiValueMap<>(0);
        }
        return new ArrayListMultiValueMap<>(connect.getHeaderFields());
    }

    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException {
        return template(ParamUtil.contactUrlParams(url , params , getDefaultBodyCharset()), Method.GET, null, null, ArrayListMultiValueMap.fromMap(headers),  connectTimeout, readTimeout,
                resultCharset, false , (s, b,r,h)-> IoUtil.read(b , r));
    }

    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException {
        return template(url, Method.POST, contentType, connect -> writeContent(connect , body , getBodyCharsetWithDefault(bodyCharset)),
                ArrayListMultiValueMap.fromMap(headers), connectTimeout, readTimeout, resultCharset, false, (s, b, r, h) -> IoUtil.read(b, r));
    }

    @Override
    public byte[] getAsBytes(String url, MultiValueMap<String, String> headers,Integer connectTimeout,Integer readTimeout) throws IOException {
        return template(url, Method.GET, null, null, headers,
                connectTimeout, readTimeout, null, false ,
                (s, b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(String url, MultiValueMap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET, null, null, headers,
                connectTimeout, readTimeout, null, false ,
                (s, b,r,h)-> IoUtil.copy2File(b, file));
    }

    /**
     * 上传文件
     */
    @Override
    public String upload(String url , MultiValueMap<String,String> headers , Integer connectTimeout , Integer readTimeout , String resultCharset ,FormFile... files) throws IOException{
        MultiValueMap<String, String> multimap = mergeHeaders(headers);
        return template(url, Method.POST, null, connect -> this.upload0(connect , files), multimap ,
                connectTimeout, readTimeout, resultCharset, false,
                (s, b, r, h) -> IoUtil.read(b, r));
    }

    /**
     * 上传文件和params参数传递，form-data类型的完全支持
     */
    @Override
    public String upload(String url, MultiValueMap<String, String> params, MultiValueMap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , FormFile... files) throws IOException {
        MultiValueMap<String, String> multimap = mergeHeaders(headers);
        return template(url, Method.POST, null, connect -> this.upload0(connect , params , getDefaultBodyCharset() , files),
                multimap , connectTimeout, readTimeout, resultCharset, false,
                (s, b, r, h) -> IoUtil.read(b, r));
    }

    protected void upload0(HttpURLConnection connection , FormFile... files) throws IOException{
        connection.addRequestProperty(HeaderRegular.CONTENT_LENGTH.toString() , String.valueOf(getFormFilesLen(files) + END_LINE.length()));

        // 设置DataOutputStream
        DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
        /*for (int i = 0; i < files.length; i++) {
            writeOneFile(ds, files[i]);
        }*/
        for (FormFile formFile : files) {
            writeOneFile(ds, formFile);
        }
        ds.writeBytes(END_LINE);
        /* close streams */
        ds.flush();
        //ds.close();
    }

    protected void upload0(HttpURLConnection connection , MultiValueMap<String, String> params, String paramCharset, FormFile... files) throws IOException{
        int fileDataLength = getFormFilesLen(files);

        String textEntity = getTextEntity(params);
        // 计算传输给服务器的实体数据总长度
        int dataLength = textEntity.getBytes(paramCharset).length + fileDataLength + END_LINE.length();

        connection.addRequestProperty(HeaderRegular.CONTENT_LENGTH.toString() , String.valueOf(dataLength));

        DataOutputStream ds = new DataOutputStream(connection.getOutputStream());

        //写params数据
        ds.writeBytes(textEntity);
        //写文件
        /*for (int i = 0; i < files.length; i++) {
            writeOneFile(ds, files[i]);
        }*/
        for (FormFile formFile : files) {
            writeOneFile(ds, formFile);
        }
        //写末尾行
        ds.writeBytes(END_LINE);
        /* close streams */
        ds.flush();
        ds.close();
    }

    /**
     * 写一个文件 ， 必须保证和getFormFilesLen的内容一致
     * @see NativeHttpClient#getFormFilesLen(FormFile...)
     */
    private void writeOneFile(DataOutputStream ds, FormFile formFile) throws IOException {
        ds.writeBytes(PART_BEGIN_LINE);
        ds.writeBytes("Content-Disposition: form-data; name=\"" + formFile.getParameterName() + "\"; filename=\"" + formFile.getFilName() + "\"" + END);
        ds.writeBytes("Content-Type: " + formFile.getContentType() + END + END);

        InputStream inStream = formFile.getInStream();
        IoUtil.copy(inStream, ds);
        ds.writeBytes(END);

        IoUtil.close(inStream);
    }

    /**
     * 计算需要传输的字节数
     * @see NativeHttpClient#writeOneFile(DataOutputStream, FormFile)
     * @param files FormFile
     * @return 总的字节数
     */
    protected int getFormFilesLen(FormFile... files){
        int fileDataLength = 0;
        for (FormFile formFile : files) {
            StringBuilder fileExplain = new StringBuilder();
            fileExplain.append(PART_BEGIN_LINE);
            fileExplain.append("Content-Disposition: form-data; name=\"" + formFile.getParameterName() + "\";filename=\"" + formFile.getFilName() + "\"" + END);
            fileExplain.append("Content-Type: " + formFile.getContentType() + END + END);
            fileDataLength += fileExplain.length();
            fileDataLength += formFile.getFileLen();
            fileDataLength += END.length();
        }
        return fileDataLength;
    }

    private String getTextEntity(MultiValueMap<String, String> params) {
        StringBuilder textEntity = new StringBuilder();
        // 构造文本类型参数的实体数据
        if(null != params){
            ///
            /*Set<String> keySet = params.keySet();
            for(String key : keySet){
                List<String> list = params.get(key);
                for(String value : list){
                    textEntity.append(PART_BEGIN_LINE);
                    textEntity.append("Content-Disposition: form-data; name=\"" + key + "\"" + END + END);
                    textEntity.append(value).append(END);
                }
            }*/
            params.forEachKeyValue((key,value)->{
                textEntity.append(PART_BEGIN_LINE);
                textEntity.append("Content-Disposition: form-data; name=\"" + key + "\"" + END + END);
                textEntity.append(value).append(END);
            });
        }
        return textEntity.toString();
    }
    protected MultiValueMap<String, String> mergeHeaders(MultiValueMap<String, String> headers) {
        if(null == headers){
            headers = new ArrayListMultiValueMap<>(1);
        }
        ///headers.put("Connection" , "Keep-Alive");
        //headers.add("Charset" , "UTF-8");
        headers.add(HeaderRegular.CONTENT_TYPE.toString() , "multipart/form-data; boundary=" + BOUNDARY);
        return headers;
    }

    protected InputStream getStreamFrom(HttpURLConnection connect , int statusCode , boolean ignoreResponseBody) throws IOException{
        //忽略返回body的情况下，直接返回空的
        if(ignoreResponseBody){
            return emptyInputStream();
        }

        InputStream inputStream;
        if(HttpStatus.HTTP_OK == statusCode){
            inputStream = connect.getInputStream();
        }else {
            inputStream = connect.getErrorStream();
        }
        if(null == inputStream){
            inputStream = emptyInputStream();
        }
        return inputStream;
    }

    /**子类复写需要首先调用此方法，保证http的功能*/
    protected void doWithConnection(HttpURLConnection connect) throws IOException{
        //default do nothing, give children a chance to do more config
    }

    /**
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#build()
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#build(String, String)
     */
    protected void initSSL(HttpsURLConnection con , HostnameVerifier hostnameVerifier , SSLSocketFactory sslSocketFactory) {
        // 验证域
        if(null != hostnameVerifier){
            con.setHostnameVerifier(hostnameVerifier);
        }
        if(null != sslSocketFactory){
            con.setSSLSocketFactory(sslSocketFactory);
        }
    }

    protected void setRequestHeaders(HttpURLConnection connection, String contentType,
                                     MultiValueMap<String, String> headers,
                                     Map<String , String> overwriteHeaders) {
        //add方式处理多值header
        if(null != headers && !headers.isEmpty()) {
            ///
            /*Set<String> keySet = headers.keySet();
            keySet.forEach((k)->headers.get(k).forEach((v)->connection.addRequestProperty(k,v)));*/
            headers.forEachKeyValue(connection::addRequestProperty);
        }

        //set方式处理单值header
        if(null != overwriteHeaders && !overwriteHeaders.isEmpty()){
            overwriteHeaders.forEach(connection::setRequestProperty);
        }

        if(null != contentType){
            connection.setRequestProperty(HeaderRegular.CONTENT_TYPE.toString(), contentType);
        }
    }

    protected void writeContent(HttpURLConnection connect, String data, String bodyCharset) throws IOException {
        if (null == data || null == bodyCharset) {
            return;
        }
        OutputStream outputStream = connect.getOutputStream();
        outputStream.write(data.getBytes(bodyCharset));
        outputStream.close();
        /* PrintWriter out = new PrintWriter(connect.getOutputStream());
        if (null != data) {
            out.print(data);
            out.flush();
        }
        out.close();*/
    }




    public static void disconnectQuietly(HttpURLConnection connect) {
        if(null != connect){
            try {
                connect.disconnect();
            } catch (Exception e) {}
        }
    }
    ///
    /*
    protected void initDefaultSSL(String sslVer) {
        try {
            TrustManager[] tmCerts = new TrustManager[1];
            tmCerts[0] = new DefaultTrustManager();
            SSLContext sslContext = SSLContext.getInstance(sslVer);
            sslContext.init(null, tmCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            HostnameVerifier hostnameVerifier = new TrustAnyHostnameVerifier();
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        } catch (Exception e) {
        }
    }*/

    /**
     * form-data的格式为：
     */
/*
    --*****xsyloveyou******
    Content-Disposition: form-data; name="k1"

    v1
    --*****xsyloveyou******
    Content-Disposition: form-data; name="k2"

    v2
    --*****xsyloveyou******
    Content-Disposition: form-data; name="filedata2"; filename="BugReport.png"
    Content-Type: application/octet-stream

            我是文本类容文件
    --*****xsyloveyou******
    Content-Disposition: form-data; name="filedata"; filename="838586397836550106.jpg"
    Content-Type: application/octet-stream

            我是文件内容2
    --*****xsyloveyou******--

*/

    @Override
    public String toString() {
        return "HttpClient implemented by JDK's HttpURLConnection";
    }
}
