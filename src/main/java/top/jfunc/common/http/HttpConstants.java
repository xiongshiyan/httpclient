package top.jfunc.common.http;

import top.jfunc.common.http.base.MediaType;
import top.jfunc.common.utils.CharsetUtil;

/**
 * HTTP模块的一些默认设置
 * @author xiongshiyan
 */
public class HttpConstants {
    /**
     * 一些常用字符串
     */
    public static final String BLANK         = "";
    public static final String SPLASH        = "/";
    public static final String QUESTION_MARK = "?";
    public static final String AND           = "&";
    public static final String EQUALS        = "=";
    public static final String COLON         = ":";
    public static final String SEMICOLON     = ";";
    public static final String COMMA         = ",";
    public static final String COLON_SPLASH  = "://";
    public static final String HTTP          = "http";
    public static final String HTTPS         = "https";
    public static final String CRLF          = "\r\n";
    public static final String TWO_HYPHENS   = "--";
    public static final String HTTP_PREFIX   = HTTP + COLON_SPLASH;
    public static final String HTTPS_PREFIX  = HTTPS + COLON_SPLASH;


    /**
     * 未指定的时候，相关设置就是{@link top.jfunc.common.http.base.Config}默认的
     */
    public static final int UNSIGNED                      = -1;
    public static final int ENABLE                        = 1;
    public static final int UN_ENABLE                     = 0;
    public static final int DEFAULT_CONNECT_TIMEOUT       = 15000;
    public static final int DEFAULT_READ_TIMEOUT          = 15000;
    public static final String  DEFAULT_CHARSET           = CharsetUtil.UTF_8;

    public static final String FORM_URLENCODED                      = "application/x-www-form-urlencoded";
    public static final String JSON                                 = "application/json";
    public static final String FORM_URLENCODED_WITH_DEFAULT_CHARSET = MediaType.APPLICATIPON_FORM_DATA.withCharset(DEFAULT_CHARSET).toString();
    public static final String JSON_WITH_DEFAULT_CHARSET            = MediaType.APPLICATIPON_JSON.withCharset(DEFAULT_CHARSET).toString();
}
