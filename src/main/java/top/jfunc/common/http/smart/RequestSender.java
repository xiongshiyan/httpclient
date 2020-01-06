package top.jfunc.common.http.smart;

import java.io.IOException;

/**
 * 封装真正执行的一步
 * @see RequestExecutor
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface RequestSender<Request , Response> {
    /**
     * Request发起请求，得到响应Response
     * @param request Request
     * @return Response
     * @throws IOException IOException
     */
    Response send(Request request) throws IOException;
}
