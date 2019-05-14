package top.jfunc.common.http;

import top.jfunc.common.Editor;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.Joiner;
import top.jfunc.common.utils.StrUtil;

import java.io.*;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author xiongshiyan at 2017/12/11
 */
public class ParamUtil {
    private ParamUtil(){}

    private static final String SPLASH = "/";

    /**
     * 检测是否https
     * @param url 完整的URL
     * @return 是否https
     */
    public static boolean isHttps(String url) {
        return url.trim().toLowerCase().startsWith("https://");
    }
    /**
     * 检测是否http
     * @param url 完整的URL
     * @return 是否https
     */
    public static boolean isHttp(String url) {
        return url.trim().toLowerCase().startsWith("http://");
    }

    /**
     * 默认UTF-8编码
     */
    public static String contactMap(Map<String, String> value){
        return contactMap(value , HttpConstants.DEFAULT_CHARSET);
    }
    /**
     * key1=value1&key2=value2,如果value=null 或者 size=0 返回 ""
     * @param value 键值对
     */
    public static String contactMap(Map<String, String> value , final String valueCharset){
        if(null == value){return "";}
        ///
		/*value.forEach((k,v)->{
            try {
                value.put(k,URLEncoder.encode(v, CharsetUtil.UTF_8));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });*/
        Editor<String> editor = (v)->{
            try {
                return URLEncoder.encode(v, valueCharset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        };
        return Joiner.on("&").withKeyValueSeparator("=",editor).useForNull("").join(value);
		/*try {
			String params;
			params = "";
			Iterator<String> iterator = value.keySet().iterator();
			while(iterator.hasNext()){
                String key = iterator.next().toString();
                params += key + "=" + URLEncoder.encode(value.get(key), "UTF8") + "&";
            }
			if(params.length() > 0){
                params = params.substring(0, params.length() - 1);
            }
			return params;
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
			return "";
		}*/
    }
    public static String contactMap(ArrayListMultimap<String, String> value){
        return contactMap(value , HttpConstants.DEFAULT_CHARSET);
    }
    /**
     * key1=value1&key2=value2&key2=value3,如果value=null 或者 size=0 返回 ""
     * @param value 键值对
     */
    public static String contactMap(ArrayListMultimap<String, String> value , String valueCharset){
        if(null == value){return "";}
        try {
            StringBuilder params = new StringBuilder();
            Iterator<String> iterator = value.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                List<String> vList = value.get(key);
                int len = vList.size();
                for (int i = 0; i < len; i++) {
                    params.append(key).append("=").append(URLEncoder.encode(vList.get(i), valueCharset)).append("&");
                }
            }
            if(params.length() > 0){
                params = params.deleteCharAt(params.length() - 1);
            }
            return params.toString();
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return "";
        }
    }

    /**
     * URL和参数
     * @param actionName URL，可以包含?
     * @param paramString 参数字符串，可以为""，null，k1=v1&k2=v2
     * @return 连接后的字符串
     */
    public static String contactUrlParams(String actionName, String paramString) {
        String url = actionName;
        if(!"".equals(paramString)) {
            //如果包含?，則直接追加//不包含?，則用?追加
            url = actionName.contains("?") ? actionName + "&" + paramString : actionName + "?" + paramString;
        }
        return url;
    }
    public static String contactUrlParams(String actionName, ArrayListMultimap<String , String> params) {
        Objects.requireNonNull(actionName);
        return contactUrlParams(actionName , contactMap(params , HttpConstants.DEFAULT_CHARSET));
    }
    /**
     * @see ParamUtil#contactMap(ArrayListMultimap,String)
     * @see ParamUtil#contactUrlParams(String, String)
     */
    public static String contactUrlParams(String actionName, ArrayListMultimap<String , String> params , String valueCharset) {
        Objects.requireNonNull(actionName);
        return contactUrlParams(actionName , contactMap(params , valueCharset));
    }
    /**
     * @see ParamUtil#contactMap(Map , String)
     * @see ParamUtil#contactUrlParams(String, String)
     */
    public static String contactUrlParams(String actionName, Map<String , String> params , String valueCharset) {
        Objects.requireNonNull(actionName);
        return contactUrlParams(actionName , contactMap(params , valueCharset));
    }
    public static String contactUrlParams(String actionName, Map<String , String> params) {
        Objects.requireNonNull(actionName);
        return contactUrlParams(actionName , contactMap(params , HttpConstants.DEFAULT_CHARSET));
    }

    /**
     * 判断给定的字符串是否是完整的URL
     * https://localhost:8080/... true
     * http://localhost:8080/... true
     * /xxx/xxx false
     * yyy/yyy false
     *
     */
    public static boolean isCompletedUrl(String url){
        return isHttp(url) || isHttps(url);
    }

    /**
     * 如果有必要就添加baseUrl，对 / 的兼容处理
     * @param baseUrl baseUrl
     * @param inputUrl inputUrl
     */
    public static String addBaseUrlIfNecessary(String baseUrl , String inputUrl){
        //1.baseUrl为空不处理
        //2.本身是完整的URL不处理
        if(StrUtil.isEmpty(baseUrl) || ParamUtil.isCompletedUrl(inputUrl)){
            return inputUrl;
        }
        if(baseUrl.endsWith(SPLASH) && inputUrl.startsWith(SPLASH)){
            return baseUrl + inputUrl.substring(1);
        }
        if(!baseUrl.endsWith(SPLASH) && !inputUrl.startsWith(SPLASH)){
            return baseUrl + SPLASH + inputUrl;
        }
        return baseUrl + inputUrl;
    }
}
