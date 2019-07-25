package top.jfunc.common.http.util;

import top.jfunc.common.http.HeaderRegular;
import top.jfunc.common.http.HttpStatus;
import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import static top.jfunc.common.http.HttpConstants.CRLF;
import static top.jfunc.common.http.HttpConstants.TWO_HYPHENS;
import static top.jfunc.common.http.base.StreamUtil.emptyInputStream;

/**
 * @author xiongshiyan at 2019/7/12 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class NativeUtil {
    private static final String BOUNDARY = "*****xsyloveyou******";
    /**
     * 数据开始标志
     */
    private static final String PART_BEGIN_LINE = TWO_HYPHENS + BOUNDARY + CRLF;
    /**
     * 数据结束标志
     */
    private static final String END_LINE = TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + CRLF;


    public static void upload0(HttpURLConnection connection , FormFile... files) throws IOException {
        connection.setRequestProperty(HeaderRegular.CONTENT_LENGTH.toString() , String.valueOf(getFormFilesLen(files) + END_LINE.length()));
        connection.setRequestProperty(HeaderRegular.CONTENT_TYPE.toString() , "multipart/form-data; boundary=" + BOUNDARY);

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

    public static void upload0(HttpURLConnection connection , MultiValueMap<String, String> params, String paramCharset, FormFile... files) throws IOException{
        int fileDataLength = getFormFilesLen(files);

        String textEntity = getTextEntity(params);
        // 计算传输给服务器的实体数据总长度
        int dataLength = textEntity.getBytes(paramCharset).length + fileDataLength + END_LINE.length();

        connection.setRequestProperty(HeaderRegular.CONTENT_LENGTH.toString() , String.valueOf(dataLength));
        connection.setRequestProperty(HeaderRegular.CONTENT_TYPE.toString() , "multipart/form-data; boundary=" + BOUNDARY);

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
     * @see NativeUtil#getFormFilesLen(FormFile...)
     */
    private static void writeOneFile(DataOutputStream ds, FormFile formFile) throws IOException {
        ds.writeBytes(PART_BEGIN_LINE);
        ds.writeBytes("Content-Disposition: form-data; name=\"" + formFile.getParameterName() + "\"; filename=\"" + formFile.getFilName() + "\"" + CRLF);
        ds.writeBytes("Content-Type: " + formFile.getContentType() + CRLF + CRLF);

        InputStream inStream = formFile.getInStream();
        IoUtil.copy(inStream, ds);
        ds.writeBytes(CRLF);

        IoUtil.close(inStream);
    }

    /**
     * 计算需要传输的字节数
     * @see NativeUtil#writeOneFile(DataOutputStream, FormFile)
     * @param files FormFile
     * @return 总的字节数
     */
    private static int getFormFilesLen(FormFile... files){
        int fileDataLength = 0;
        for (FormFile formFile : files) {
            StringBuilder fileExplain = new StringBuilder();
            fileExplain.append(PART_BEGIN_LINE);
            fileExplain.append("Content-Disposition: form-data; name=\"" + formFile.getParameterName() + "\";filename=\"" + formFile.getFilName() + "\"" + CRLF);
            fileExplain.append("Content-Type: " + formFile.getContentType() + CRLF + CRLF);
            fileDataLength += fileExplain.length();
            fileDataLength += formFile.getFileLen();
            fileDataLength += CRLF.length();
        }
        return fileDataLength;
    }

    private static String getTextEntity(MultiValueMap<String, String> params) {
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
                textEntity.append("Content-Disposition: form-data; name=\"" + key + "\"" + CRLF + CRLF);
                textEntity.append(value).append(CRLF);
            });
        }
        return textEntity.toString();
    }
    /*protected MultiValueMap<String, String> mergeHeaders(MultiValueMap<String, String> headers) {
        if(null == headers){
            headers = new ArrayListMultiValueMap<>(1);
        }
        ///headers.put("Connection" , "Keep-Alive");
        //headers.add("Charset" , "UTF-8");
        headers.add(HeaderRegular.CONTENT_TYPE.toString() , "multipart/form-data; boundary=" + BOUNDARY);
        return headers;
    }*/

    public static InputStream getStreamFrom(HttpURLConnection connect , int statusCode , boolean ignoreResponseBody) throws IOException{
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

    /**
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#build()
     * @see top.jfunc.common.http.base.ssl.SSLSocketFactoryBuilder#build(String, String)
     */
    public static void initSSL(HttpsURLConnection con , HostnameVerifier hostnameVerifier , SSLSocketFactory sslSocketFactory) {
        // 验证域
        if(null != hostnameVerifier){
            con.setHostnameVerifier(hostnameVerifier);
        }
        if(null != sslSocketFactory){
            con.setSSLSocketFactory(sslSocketFactory);
        }
    }

    public static void setRequestHeaders(HttpURLConnection connection, String contentType,
                                     MultiValueMap<String, String> headers) {
        //add方式处理多值header
        if(null != headers && !headers.isEmpty()) {
            ///
            /*Set<String> keySet = headers.keySet();
            keySet.forEach((k)->headers.get(k).forEach((v)->connection.addRequestProperty(k,v)));*/
            headers.forEachKeyValue(connection::addRequestProperty);
        }

        ///set方式处理单值header
        /*if(null != overwriteHeaders && !overwriteHeaders.isEmpty()){
            overwriteHeaders.forEach(connection::setRequestProperty);
        }*/

        if(null != contentType){
            connection.setRequestProperty(HeaderRegular.CONTENT_TYPE.toString(), contentType);
        }
    }

    public static void writeContent(HttpURLConnection connect, String data, String bodyCharset) throws IOException {
        if (null == data) {
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

    public static MultiValueMap<String , String> parseHeaders(HttpURLConnection connect, boolean includeHeaders) {
        if(!includeHeaders){
            return new ArrayListMultiValueMap<>(0);
        }
        return new ArrayListMultiValueMap<>(connect.getHeaderFields());
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
}
