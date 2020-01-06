package top.jfunc.common.http.smart;

import java.io.IOException;

/**
 * 封装真正执行的一步
 * @see RequestSender
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface RequestExecutor<Client , Request , Response> {
    /**
     * 客户端Client执行器来执行请求Request，得到响应Response
     * @param client Client
     * @param request Request
     * @return Response
     * @throws IOException IOException
     */
    Response execute(Client client , Request request) throws IOException;
}
