package top.jfunc.common.http.base;

import okhttp3.RequestBody;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * 写入body数据，属于一个回调方法
 * @author xiongshiyan at 2018/6/7
 * @param <CC> 处理的泛型，不同的实现方式有不同的写入方式
 * @see HttpURLConnection#getOutputStream()
 * @see org.apache.http.client.methods.HttpEntityEnclosingRequestBase#setEntity(HttpEntity)
 * @see okhttp3.Request.Builder#method(String, RequestBody)
 */
@FunctionalInterface
public interface ContentCallback<CC> {
    /**
     * 往connection中写入数据
     * @param cc 连接
     * @throws IOException IOException
     */
    void doWriteWith(CC cc) throws IOException;
}
