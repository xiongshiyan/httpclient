package cn.zytx.common.http;

/**
 * 1.HTTP请求异常，包括http组件内部错误-1，服务器返回错误（错误码和错误信息）
 * 2.超时异常由专门的IOException表达
 * @author 熊诗言 2017/11/24
 */
public class HttpException extends RuntimeException{
    private int responseCode = -1;
    private String errorMessage = "error happens in client";

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
}
