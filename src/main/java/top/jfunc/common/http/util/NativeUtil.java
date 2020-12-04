package top.jfunc.common.http.util;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.base.HttpHeaders;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import static top.jfunc.common.utils.StrUtil.CRLF;
import static top.jfunc.common.utils.StrUtil.TWO_HYPHENS;

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


    public static void upload0(HttpURLConnection connection , Iterable<FormFile> formFiles) throws IOException {
        connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH , String.valueOf(getFormFilesLen(formFiles) + END_LINE.length()));
        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE , "multipart/form-data; boundary=" + BOUNDARY);

        try(DataOutputStream ds = new DataOutputStream(connection.getOutputStream())) {
            for (FormFile formFile : formFiles) {
                writeOneFile(ds, formFile);
            }
            ds.writeBytes(END_LINE);
            ds.flush();
        }
    }

    public static void upload0(HttpURLConnection connection , MultiValueMap<String, String> params, String paramCharset, Iterable<FormFile> formFiles) throws IOException{
        int fileDataLength = getFormFilesLen(formFiles);

        String textEntity = getTextEntity(params);
        // 计算传输给服务器的实体数据总长度
        int dataLength = textEntity.getBytes(paramCharset).length + fileDataLength + END_LINE.length();

        connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH , String.valueOf(dataLength));
        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE , "multipart/form-data; boundary=" + BOUNDARY);

        try(DataOutputStream ds = new DataOutputStream(connection.getOutputStream())) {
            //写params数据
            ds.writeBytes(textEntity);
            //写文件
            for (FormFile formFile : formFiles) {
                writeOneFile(ds, formFile);
            }
            //写末尾行
            ds.writeBytes(END_LINE);
            ds.flush();
        }
    }

    /**
     * 写一个文件 ， 必须保证和getFormFilesLen的内容一致
     * @see NativeUtil#getFormFilesLen(Iterable)
     */
    private static void writeOneFile(DataOutputStream ds, FormFile formFile) throws IOException {
        ds.writeBytes(PART_BEGIN_LINE);
        ds.writeBytes("Content-Disposition: form-data; name=\"" + formFile.getParameterName() + "\"; filename=\"" + formFile.getFilName() + "\"" + CRLF);
        ds.writeBytes("Content-Type: " + formFile.getContentType() + CRLF + CRLF);

        try(InputStream inStream = formFile.getInStream()){
            IoUtil.copy(inStream, ds);
            ds.writeBytes(CRLF);
        }
    }

    /**
     * 计算需要传输的字节数
     * @see NativeUtil#writeOneFile(DataOutputStream, FormFile)
     * @param formFiles FormFile
     * @return 总的字节数
     */
    private static int getFormFilesLen(Iterable<FormFile> formFiles){
        int fileDataLength = 0;
        for (FormFile formFile : formFiles) {
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
        if(MapUtil.notEmpty(params)){
            params.forEachKeyValue((key,value)->{
                textEntity.append(PART_BEGIN_LINE);
                textEntity.append("Content-Disposition: form-data; name=\"" + key + "\"" + CRLF + CRLF);
                textEntity.append(value).append(CRLF);
            });
        }
        return textEntity.toString();
    }

    /**
     * @see top.jfunc.common.http.ssl.SSLSocketFactoryBuilder#build()
     * @see top.jfunc.common.http.ssl.SSLSocketFactoryBuilder#build(String, String)
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
        if(MapUtil.notEmpty(headers)) {
            headers.forEachKeyValue(connection::addRequestProperty);
        }

        if(null != contentType){
            connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, contentType);
        }
    }

    public static void writeContent(HttpURLConnection connect, String data, String bodyCharset) throws IOException {
        if (null == data) {
            return;
        }

        //！！！！！！设置ContentType非常重要，他写入的时候根据Content-Type的编码来写的！！！！！！！

        try(OutputStream outputStream = connect.getOutputStream()){
            outputStream.write(data.getBytes(bodyCharset));
        }
    }

    public static MultiValueMap<String , String> parseHeaders(HttpURLConnection connection) {
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        if(MapUtil.isEmpty(headerFields)){
            return null;
        }
        return new ArrayListMultiValueMap<>(headerFields);
    }


    public static void closeQuietly(HttpURLConnection connect) {
        if(null != connect){
            try {
                connect.disconnect();
            } catch (Exception e) {}
        }
    }

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
