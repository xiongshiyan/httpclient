package top.jfunc.common.http;

import top.jfunc.common.Editor;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.Joiner;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.StrUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static top.jfunc.common.http.HttpConstants.*;

/**
 * @author xiongshiyan at 2017/12/11
 * @since 1.1.1新增MultiValueMap的支持
 */
public class ParamUtil {
    private ParamUtil(){}

    /**
     * 检测是否https
     * @param url 完整的URL
     * @return 是否https
     */
    public static boolean isHttps(String url) {
        return url.trim().toLowerCase().startsWith(HTTPS_PREFIX);
    }
    /**
     * 检测是否http
     * @param url 完整的URL
     * @return 是否https
     */
    public static boolean isHttp(String url) {
        return url.trim().toLowerCase().startsWith(HTTP_PREFIX);
    }

    /**
     * 默认UTF-8编码
     */
    public static String contactMap(Map<String, String> value){
        return contactMap(value , DEFAULT_CHARSET);
    }
    /**
     * key1=value1&key2=value2,如果value=null 或者 size=0 返回 ""
     * @param value 键值对
     */
    public static String contactMap(Map<String, String> value , final String valueCharset){
        if(null == value || value.isEmpty()){return BLANK;}

        Editor<String> editor = (v)-> urlEncode(v , valueCharset);
        return Joiner.on(AND).withKeyValueSeparator(EQUALS,editor).useForNull(BLANK).join(value);
    }
    
    public static String contactMap(ArrayListMultimap<String, String> value){
        return contactMap(value , DEFAULT_CHARSET);
    }
    public static String contactMap(MultiValueMap<String, String> value){
        return contactMap(value , DEFAULT_CHARSET);
    }
    /**
     * key1=value1&key2=value2&key2=value3,如果value=null 或者 size=0 返回 ""
     * @param multimap 键值对
     */
    public static String contactMap(ArrayListMultimap<String, String> multimap , final String valueCharset){
        if(null == multimap || multimap.keySet().isEmpty()){return BLANK;}

        Set<Map.Entry<String, List<String>>> entries = multimap.getMap().entrySet();

        return contactIterable(entries, valueCharset);
    }

    /**
     * key1=value1&key2=value2&key2=value3,如果value=null 或者 size=0 返回 ""
     * @param multiValueMap 键值对
     */
    public static String contactMap(MultiValueMap<String, String> multiValueMap , final String valueCharset){
        if(null == multiValueMap || multiValueMap.isEmpty()){return BLANK;}

        Set<Map.Entry<String, List<String>>> entries = multiValueMap.entrySet();

        return contactIterable(entries, valueCharset);
    }

    /**
     * key1=value1&key2=value2&key2=value3,如果value=null 或者 size=0 返回 ""
     * @param entries Map.Entrys
     */
    public static String contactIterable(Iterable<Map.Entry<String, List<String>>> entries, String valueCharset) {
        if(null == entries || !entries.iterator().hasNext()){
            return BLANK;
        }

        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : entries) {
            String key = entry.getKey();
            List<String> vList = entry.getValue();
            for (String v : vList) {
                params.append(key).append(EQUALS).append(urlEncode(v, valueCharset)).append(AND);
            }
        }

        if(params.length() > 0){
            params = params.deleteCharAt(params.length() - 1);
        }
        return params.toString();
    }

    /**
     * 对字符串进行URL编码
     * @param valueCharset 字符编码,字符编码不对原样返回
     * @param src 原字符串
     * @return 编码后的字符串
     */
    public static String urlEncode(String src, String valueCharset) {
        try {
            return URLEncoder.encode(src, valueCharset);
        }catch (UnsupportedEncodingException e){
            return src;
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
        if(!BLANK.equals(paramString)) {
            //如果包含?，則直接追加//不包含?，則用?追加
            url = actionName.contains(QUESTION_MARK) ? actionName + AND + paramString : actionName + QUESTION_MARK + paramString;
        }
        return url;
    }
    public static String contactUrlParams(String actionName, ArrayListMultimap<String , String> params) {
        Objects.requireNonNull(actionName);
        return contactUrlParams(actionName , contactMap(params , DEFAULT_CHARSET));
    }
    public static String contactUrlParams(String actionName, MultiValueMap<String , String> params) {
        Objects.requireNonNull(actionName);
        return contactUrlParams(actionName , contactMap(params , DEFAULT_CHARSET));
    }
    /**
     * @see ParamUtil#contactMap(ArrayListMultimap,String)
     * @see ParamUtil#contactUrlParams(String, String)
     */
    public static String contactUrlParams(String actionName, ArrayListMultimap<String , String> params , String valueCharset) {
        Objects.requireNonNull(actionName);
        return contactUrlParams(actionName , contactMap(params , valueCharset));
    }
    public static String contactUrlParams(String actionName, MultiValueMap<String , String> params , String valueCharset) {
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
        return contactUrlParams(actionName , contactMap(params , DEFAULT_CHARSET));
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

    /**
     * 处理路径参数
     * @param originUrl 形如 http://httpbin.org/book/{id}
     * @param routeParams 参数值
     * @return 处理过后的URL
     */
    public static String replaceRouteParamsIfNecessary(String originUrl , Map<String , String> routeParams){
        if(null == routeParams || routeParams.isEmpty()){
            return originUrl;
        }
        String url = originUrl;
        for (Map.Entry<String, String> entry : routeParams.entrySet()) {
            String key = entry.getKey();
            url = url.replaceFirst("\\{" + key + "}", entry.getValue());
            /*if(url.contains(key)){
                //只替换那些url中包含key的，提升效率[一般map中应该很少有那么多冗余的]
            }*/
        }

        return url;
    }
}
