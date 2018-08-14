package top.jfunc.common.http.basic;

import top.jfunc.common.http.base.AbstractNativeHttp;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.IoUtil;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.ParamUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static top.jfunc.common.http.HttpConstants.DEFAULT_CHARSET;

/**
 * 使用URLConnection实现的Http请求类
 * @author 熊诗言2017/11/24
 */
public class NativeHttpClient extends AbstractNativeHttp implements HttpClient {
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
    public String get(String actionName, Map<String, String> params, Map<String, String> headers, int connectTimeout, int readTimeout, String resultCharset) throws IOException {
        return template(ParamUtil.contactUrlParams(actionName , params , DEFAULT_CHARSET), Method.GET, null, null, ArrayListMultimap.fromMap(headers),  connectTimeout, readTimeout,
                resultCharset, false , (s, b,r,h)-> IoUtil.read(b , r));
    }

    @Override
    public String post(String url, String body, String contentType, Map<String, String> headers, int connectTimeout, int readTimeout, String bodyCharset, String resultCharset) throws IOException {
        return template(url, Method.POST, contentType, connect -> writeContent(connect , body , bodyCharset),
                ArrayListMultimap.fromMap(headers), connectTimeout, readTimeout, resultCharset, false, (s, b, r, h) -> IoUtil.read(b, r));
    }

    @Override
    public byte[] getAsBytes(String url, ArrayListMultimap<String, String> headers,int connectTimeout,int readTimeout) throws IOException {
        return template(url, Method.GET, null, null, headers,
                connectTimeout, readTimeout, null, false ,
                (s, b,r,h)-> IoUtil.stream2Bytes(b));
    }

    @Override
    public File getAsFile(String url, ArrayListMultimap<String, String> headers, File file, int connectTimeout, int readTimeout) throws IOException {
        return template(url, Method.GET, null, null, headers,
                connectTimeout, readTimeout, null, false ,
                (s, b,r,h)-> IoUtil.copy2File(b, file));
    }

    /**
     * 上传文件
     */
    @Override
    public String upload(String url , ArrayListMultimap<String,String> headers , int connectTimeout , int readTimeout , String resultCharset ,FormFile... files) throws IOException{
        ArrayListMultimap<String, String> multimap = mergeHeaders(headers);
        return template(url, Method.POST, null, connect -> this.upload0(connect , files), multimap ,
                connectTimeout, readTimeout, resultCharset, false,
                (s, b, r, h) -> IoUtil.read(b, r));
    }

    /**
     * 上传文件和params参数传递，form-data类型的完全支持
     */
    @Override
    public String upload(String url, ArrayListMultimap<String, String> params, ArrayListMultimap<String, String> headers, int connectTimeout, int readTimeout, String resultCharset , FormFile... files) throws IOException {
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
        int dataLength = textEntity.getBytes().length + fileDataLength + END_LINE.length();

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
        //headers.put("Connection" , "Keep-Alive");
        headers.put("Charset" , "UTF-8");
        headers.put("Content-Type" , "multipart/form-data; boundary=" + BOUNDARY);
        return headers;
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
