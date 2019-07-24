package top.jfunc.common.http;

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
    public static final String COLON_SPLASH  = "://";
    public static final String HTTP          = "http";
    public static final String HTTPS         = "https";
    public static final String CRLF          = "\r\n";
    public static final String TWO_HYPHENS   = "--";
    public static final String HTTP_PREFIX   = HTTP + COLON_SPLASH;
    public static final String HTTPS_PREFIX  = HTTPS + COLON_SPLASH;



    public static final Integer DEFAULT_CONNECT_TIMEOUT;
    public static final Integer DEFAULT_READ_TIMEOUT;
    public static final String  DEFAULT_CHARSET;

    public static final String FORM_URLENCODED                      = "application/x-www-form-urlencoded";
    public static final String JSON                                 = "application/json";
    public static final String TEXT_XML                             = "text/xml";
    public static final String FORM_URLENCODED_WITH_DEFAULT_CHARSET;
    public static final String TEXT_XML_WITH_DEFAULT_CHARSET       ;
    public static final String JSON_WITH_DEFAULT_CHARSET           ;

    static {
        //给应用一个修改默认值的机会,使用System.setProperty()或者-D来设置
        String dct = getProp("DEFAULT_CONNECT_TIMEOUT");
        String drt = getProp("DEFAULT_READ_TIMEOUT");
        String dc = getProp("DEFAULT_CHARSET");
        DEFAULT_CONNECT_TIMEOUT = (dct==null) ? 15000 : Integer.valueOf(dct);
        DEFAULT_READ_TIMEOUT    = (drt==null) ? 15000 : Integer.valueOf(drt);
        DEFAULT_CHARSET = ( dc == null ) ? "UTF-8" : dc;

        FORM_URLENCODED_WITH_DEFAULT_CHARSET = FORM_URLENCODED + ";charset=" + DEFAULT_CHARSET;
        TEXT_XML_WITH_DEFAULT_CHARSET        = TEXT_XML + ";charset=" + DEFAULT_CHARSET;
        JSON_WITH_DEFAULT_CHARSET            = JSON + ";charset=" + DEFAULT_CHARSET;
    }

    private static String getProp(String key){
        String property = System.getProperty(key);
        if(null == property || "".equals(property)){
            property = System.getenv(key);
        }
        return property;
    }
}
