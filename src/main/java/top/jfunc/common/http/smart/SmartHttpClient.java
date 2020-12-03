package top.jfunc.common.http.smart;

/**
 * 是面向使用者的一个门面类，既有{@link SimpleHttpClient}的简单快捷，也有{@link HttpRequestHttpClient}的简约全面
 * @author xiongshiyan at 2017/12/9
 */
public interface SmartHttpClient extends SimpleHttpClient, HttpRequestHttpClient {}
