package top.jfunc.common.http;

/**
 * HTTP模块的一些默认设置
 */
public class HttpConstants {
    public static final int DEFAULT_CONNECT_TIMEOUT;
    public static final int DEFAULT_READ_TIMEOUT;
    public static final String DEFAULT_CHARSET                      = "UTF-8";

    public static final String FORM_URLENCODED                      = "application/x-www-form-urlencoded";
    public static final String JSON                                 = "application/json";
    public static final String TEXT_XML                             = "text/xml";
    public static final String FORM_URLENCODED_WITH_DEFAULT_CHARSET = FORM_URLENCODED + ";charset=" + DEFAULT_CHARSET;
    public static final String TEXT_XML_WITH_DEFAULT_CHARSET        = TEXT_XML + ";charset=" + DEFAULT_CHARSET;
    public static final String JSON_WITH_DEFAULT_CHARSET            = JSON + ";charset=" + DEFAULT_CHARSET;

    static {
        //给应用一个修改默认值的机会,使用System.setProperty()或者-D来设置
        String dct = getProp("DEFAULT_CONNECT_TIMEOUT");
        String drt = getProp("DEFAULT_READ_TIMEOUT");
        DEFAULT_CONNECT_TIMEOUT = (dct==null) ? 15000 : Integer.parseInt(dct);
        DEFAULT_READ_TIMEOUT    = (drt==null) ? 15000 : Integer.parseInt(drt);
    }

    private static String getProp(String key){
        String property = System.getProperty(key);
        if(null == property || "".equals(property)){
            property = System.getenv(key);
        }
        return property;
    }
}
