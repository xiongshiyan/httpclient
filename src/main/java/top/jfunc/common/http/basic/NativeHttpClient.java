package top.jfunc.common.http.basic;

import top.jfunc.common.http.HttpConstants;
import top.jfunc.common.http.HttpStatus;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;
import top.jfunc.common.http.base.*;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.IoUtil;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 使用URLConnection实现的Http请求类
 * @author 熊诗言2017/11/24
 */
public class NativeHttpClient extends AbstractConfigurableHttp implements HttpTemplate<HttpURLConnection> , HttpClient {
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
    public NativeHttpClient setConfig(Config config) {
        super.setConfig(config);
        return this;
    }

    @Override
    public <R> R template(String url, Method method, String contentType, ContentCallback<HttpURLConnection> contentCallback, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , boolean includeHeaders , ResultCallback<R> resultCallback) throws IOException {
        //默认的https校验
        // 后面会处理的，这里就不需要了 initDefaultSSL(sslVer);

        HttpURLConnection connect = null;
        InputStream inputStream = null;
        try {
            //1.获取连接
            String completedUrl = addBaseUrlIfNecessary(url);
            connect = (HttpURLConnection)new java.net.URL(completedUrl).openConnection();

            //2.处理header
            setConnectProperty(connect, method, contentType,
                    mergeDefaultHeaders(headers),
                    getConnectionTimeoutWithDefault(connectTimeout),
                    getReadTimeoutWithDefault(readTimeout));

            ////////////////////////////////////ssl处理///////////////////////////////////
            if(connect instanceof HttpsURLConnection){
                //默认设置这些
                HttpsURLConnection con = (HttpsURLConnection)connect;
                initSSL(con , getHostnameVerifier() , getSSLSocketFactory());
            }
            ////////////////////////////////////ssl处理///////////////////////////////////

            //3.留给子类复写的机会:给connection设置更多参数
            doWithConnection(connect);

            //5.写入内容，只对post有效
            if(contentCallback != null && method.hasContent()){
                contentCallback.doWriteWith(connect);
            }

            //4.连接
            connect.connect();

            //6.获取返回值
            int statusCode = connect.getResponseCode();
            ///保留起非200抛异常的方式
//            if( HttpStatus.HTTP_OK == statusCode){
//                //6.1获取body
//                /*InputStream is = connect.getInputStream();
//                byte[] result = IoUtil.stream2Bytes(is);
//                is.close();
//
//                String s = new String(result, resultCharset);*/
//
//                //6.2获取header
//
//                inputStream = connect.getInputStream();
//
//                return resultCallback.convert(inputStream, resultCharset, includeHeaders ? connect.getHeaderFields() : new HashMap<>(0));
//
//            } else{
//                String err = errMessage(connect);
//                throw new HttpException(statusCode,err,connect.getHeaderFields());
//            }

            inputStream = getStreamFrom(connect , statusCode , false);

            return resultCallback.convert(statusCode , inputStream, getResultCharsetWithDefault(resultCharset), parseHeaders(connect, includeHeaders));
        } catch (IOException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            //关闭顺序不能改变，否则服务端可能出现这个异常  严重: java.io.IOException: 远程主机强迫关闭了一个现有的连接
            //1 . 关闭连接
            disconnectQuietly(connect);
            //2 . 关闭流
            IoUtil.close(inputStream);
        }
    }

    protected Map<String, List<String>> parseHeaders(HttpURLConnection connect, boolean includeHeaders) {
        return includeHeaders ? connect.getHeaderFields() : new HashMap<>(0);
    }

    @Override
    public String get(String url, Map<String, String> params, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset) throws IOException {
        return template(ParamUtil.contactUrlParams(url , params , getDefaultBodyCharset()), Method.GET, null, null, ArrayListMultimap.fromMap(headers),  connectTimeout, readTimeout,
                resultCharset, false , (s, b,r,h)-> IoUtil.read(b , r));
    }

    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, Integer connectTimeout, Integer readTimeout, String bodyCharset, String resultCharset) throws IOException {
        return template(url, Method.POST, contentType, connect -> writeContent(connect , body , getBodyCharsetWithDefault(bodyCharset)),
                ArrayListMultimap.fromMap(headers), connectTimeout, readTimeout, resultCharset, false, (s, b, r, h) -> IoUtil.read(b, r));
    }

    @Override
    public byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers,Integer connectTimeout,Integer readTimeout) throws IOException {
        return template(url, Method.GET, null, null, headers,
                connectTimeout, readTimeout, null, false ,
                (s, b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(String url, ArrayListMultimap<String, String> headers, File file, Integer connectTimeout, Integer readTimeout) throws IOException {
        return template(url, Method.GET, null, null, headers,
                connectTimeout, readTimeout, null, false ,
                (s, b,r,h)-> IoUtil.copy2File(b, file));
    }

    /**
     * 上传文件
     */
    @Override
    public String upload(String url , ArrayListMultimap<String,String> headers , Integer connectTimeout , Integer readTimeout , String resultCharset ,FormFile... files) throws IOException{
        ArrayListMultimap<String, String> multimap = mergeHeaders(headers);
        return template(url, Method.POST, null, connect -> this.upload0(connect , files), multimap ,
                connectTimeout, readTimeout, resultCharset, false,
                (s, b, r, h) -> IoUtil.read(b, r));
    }

    /**
     * 上传文件和params参数传递，form-data类型的完全支持
     */
    @Override
    public String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, Integer connectTimeout, Integer readTimeout, String resultCharset , FormFile... files) throws IOException {
        ArrayListMultimap<String, String> multimap = mergeHeaders(headers);
        return template(url, Method.POST, null, connect -> this.upload0(connect , params , files), multimap ,
                connectTimeout, readTimeout, resultCharset, false,
                (s, b, r, h) -> IoUtil.read(b, r));
    }

    protected void upload0(HttpURLConnection connection , FormFile... files) throws IOException{
        connection.addRequestProperty("Content-Length" , String.valueOf(getFormFilesLen(files) + END_LINE.length()));

        // 设置DataOutputStream
        DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
        for (int i = 0; i < files.length; i++) {
            writeOneFile(ds, files[i]);
        }
        ds.writeBytes(END_LINE);
        /* close streams */
        ds.flush();
        //ds.close();
    }

    protected void upload0(HttpURLConnection connection , ArrayListMultimap<String, String> params, FormFile... files) throws IOException{
        int fileDataLength = getFormFilesLen(files);

        String textEntity = getTextEntity(params);
        // 计算传输给服务器的实体数据总长度
        int dataLength = textEntity.getBytes(HttpConstants.DEFAULT_CHARSET).length + fileDataLength + END_LINE.length();

        connection.addRequestProperty("Content-Length" , String.valueOf(dataLength));

        DataOutputStream ds = new DataOutputStream(connection.getOutputStream());

        //写params数据
        ds.writeBytes(textEntity);
        //写文件
        for (int i = 0; i < files.length; i++) {
            writeOneFile(ds, files[i]);
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

    private String getTextEntity(ArrayListMultimap<String, String> params) {
        StringBuilder textEntity = new StringBuilder();
        // 构造文本类型参数的实体数据
        if(null != params){
            Set<String> keySet = params.keySet();
            for(String key : keySet){
                List<String> list = params.get(key);
                for(String value : list){
                    textEntity.append(PART_BEGIN_LINE);
                    textEntity.append("Content-Disposition: form-data; name=\"" + key + "\"" + END + END);
                    textEntity.append(value).append(END);
                }
            }
        }
        return textEntity.toString();
    }
    protected ArrayListMultimap<String, String> mergeHeaders(ArrayListMultimap<String, String> headers) {
        if(null == headers){
            headers = new ArrayListMultimap<>();
        }
        ///headers.put("Connection" , "Keep-Alive");
        headers.put("Charset" , "UTF-8");
        headers.put("Content-Type" , "multipart/form-data; boundary=" + BOUNDARY);
        return headers;
    }

    protected InputStream getStreamFrom(HttpURLConnection connect , int statusCode , boolean ignoreResponseBody) throws IOException{
        //忽略返回body的情况下，直接返回空的
        if(ignoreResponseBody){
            return top.jfunc.common.http.IoUtil.emptyInputStream();
        }

        InputStream inputStream;
        if(HttpStatus.HTTP_OK == statusCode){
            inputStream = connect.getInputStream();
        }else {
            inputStream = connect.getErrorStream();
        }
        if(null == inputStream){
            inputStream = top.jfunc.common.http.IoUtil.emptyInputStream();
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

    protected void setConnectProperty(HttpURLConnection connect, Method method, String contentType, ArrayListMultimap<String,String> headers, int connectTimeout, int readTimeout) throws ProtocolException {
        connect.setRequestMethod(method.name());
        connect.setDoOutput(true);
        connect.setUseCaches(false);
        setRequestHeaders(connect , contentType , headers);
        connect.setConnectTimeout(connectTimeout);
        connect.setReadTimeout(readTimeout);
    }

    protected void setRequestHeaders(HttpURLConnection connection, String contentType, ArrayListMultimap<String, String> headers) {
        if(null != headers) {
            Set<String> keySet = headers.keySet();
            keySet.forEach((k)->headers.get(k).forEach((v)->connection.addRequestProperty(k,v)));
        }
        if(null != contentType){
            connection.setRequestProperty(top.jfunc.common.http.Header.CONTENT_TYPE.toString(), contentType);
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



    /**
     * 获取输出流中的错误信息，针对HttpURLConnection
     * @param connect HttpURLConnection
     * @return 错误信息
     * @see HttpURLConnection
     * @throws IOException IO异常
     */
    protected String errMessage(HttpURLConnection connect) throws IOException {
        //如果服务器返回的HTTP状态不是HTTP_OK，则表示发生了错误，此时可以通过如下方法了解错误原因。
        // 通过getErrorStream了解错误的详情
        InputStream is = connect.getErrorStream();
        if(null==is){return "";}
        InputStreamReader isr = new InputStreamReader(is, HttpConstants.DEFAULT_CHARSET);
        BufferedReader in = new BufferedReader(isr);
        String inputLine;
        StringWriter bw = new StringWriter();
        while ((inputLine = in.readLine()) != null)
        {
            bw.write(inputLine);
            bw.write("\n");
        }
        bw.close();
        in.close();

        //disconnectQuietly(connect);

        return bw.getBuffer().toString();
    }

    public static void disconnectQuietly(HttpURLConnection connect) {
        if(null != connect){
            try {
                connect.disconnect();
            } catch (Exception e) {}
        }
    }
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

}
