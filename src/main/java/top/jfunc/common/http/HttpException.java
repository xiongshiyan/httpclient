package top.jfunc.common.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1.HTTP请求异常，包括http组件内部错误-1，服务器返回错误（错误码和错误信息）
 * 2.超时异常由专门的IOException表达
 * @see top.jfunc.common.http.smart.Response
 * @author 熊诗言 2017/11/24
 */
public class HttpException extends RuntimeException{
    private int responseCode = -1;
    private String errorMessage = "error happens in client";
    private Map<String, List<String>> headers = new HashMap<>(0);

    public HttpException(int responseCode,String errorMessage , Map<String, List<String>> headers){
        super(errorMessage);
        this.responseCode = responseCode;
        this.errorMessage = errorMessage;
        this.headers = headers;
    }
    public HttpException(int responseCode,String errorMessage){
        super(errorMessage);
        this.responseCode = responseCode;
        this.errorMessage = errorMessage;
    }
    public HttpException(String errorMessage){
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
    public HttpException(Exception e){
        super(e);
        this.errorMessage = e.getMessage();
    }
    public HttpException(){
    }


    public int getResponseCode() {
        return responseCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}
