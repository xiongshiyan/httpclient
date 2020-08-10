package top.jfunc.common.http.util;

import top.jfunc.common.Editor;
import top.jfunc.common.http.base.Config;
import top.jfunc.common.utils.Joiner;
import top.jfunc.common.utils.MapUtil;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.StrUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import static top.jfunc.common.utils.StrUtil.*;

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
     * 对于key1=value1&key2=value2解析为map
     * @param kvParams key1=value1&key2=value2
     */
    public static Map<String , String> parseParam(String kvParams){
        return parseParam(kvParams , true);
    }
    /**
     * 对于key1=value1&key2=value2解析为map
     * @param kvParams key1=value1&key2=value2
     * @param decoded 是否进行URL解码
     */
    public static Map<String , String> parseParam(String kvParams , boolean decoded){
        if(StrUtil.isEmpty(kvParams)){
            return Collections.emptyMap();
        }

        String[] kvs = kvParams.split(StrUtil.AND);
        Map<String , String> params = new HashMap<>(kvs.length);
        for (String kv : kvs) {
            //不包含=
            if(!kv.contains(StrUtil.EQUALS)){
                params.put(kv , StrUtil.BLANK);
                continue;
            }
            String[] split = kv.split(StrUtil.EQUALS);
            //k1=的情况，value为空
            if(split.length == 1){
                params.put(split[0] , StrUtil.BLANK);
                continue;
            }
            String value = split[1];
            if(decoded && StrUtil.isNotEmpty(value)){
                value = urlDecode(value , Config.DEFAULT_CHARSET);
            }
            params.put(split[0] , value);
        }

        return params;
    }


    /**
     * 默认UTF-8编码
     */
    public static String contactMap(Map<String, String> value){
        return contactMap(value , Config.DEFAULT_CHARSET);
    }
    /**
     * key1=value1&key2=value2,如果value=null 或者 size=0 返回 ""
     * @param value 键值对
     */
    public static String contactMap(Map<String, String> value , final String valueCharset){
        Editor<String> editor = (v)-> urlEncode(v , valueCharset);
        return contactMap(value, editor);
    }
    public static String contactMap(Map<String, String> value , final Editor<String> editor){
        if(MapUtil.isEmpty(value)){return BLANK;}
        return Joiner.on(AND).withKeyValueSeparator(EQUALS,editor).useForNull(StrUtil.BLANK).join(value);
    }

    public static String contactMap(MultiValueMap<String, String> value){
        return contactMap(value , Config.DEFAULT_CHARSET);
    }

    /**
     * key1=value1&key2=value2&key2=value3,如果value=null 或者 size=0 返回 ""
     * @param multiValueMap 键值对
     */
    public static String contactMap(MultiValueMap<String, String> multiValueMap , final String valueCharset){
        if(MapUtil.isEmpty(multiValueMap)){return BLANK;}

        Set<Map.Entry<String, List<String>>> entries = multiValueMap.entrySet();

        return contactIterable(entries, valueCharset);
    }

    /**
     * value进行URL编码
     * key1=value1&key2=value2&key2=value3,如果value=null 或者 size=0 返回 ""
     * @param entries Map.Entries
     */
    public static String contactIterable(Iterable<Map.Entry<String, List<String>>> entries, String valueCharset) {
        return contactIterable(entries , (v) -> urlEncode(v , valueCharset));
    }
    /**
     * value不进行URL编码
     * key1=value1&key2=value2&key2=value3,如果value=null 或者 size=0 返回 ""
     * @param entries Map.Entries
     */
    public static String contactIterableNotEncode(Iterable<Map.Entry<String, List<String>>> entries) {
        return contactIterable(entries , (v) -> v);
    }

    /**
     * 连接key-value，并对value做一定的处理
     * @param entries Iterable<Map.Entry<String, List<String>>>
     * @param valueEditor value处理器
     * @return 连接后的字符串
     */
    public static String contactIterable(Iterable<Map.Entry<String, List<String>>> entries , Editor<String> valueEditor){
        if(null == entries){
            return StrUtil.BLANK;
        }

        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : entries) {
            String key = entry.getKey();
            List<String> vList = entry.getValue();
            for (String v : vList) {
                params.append(key).append(EQUALS)
                        .append(valueEditor.edit(v))
                        .append(AND);
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
     * 对字符串进行URL解码
     * @param valueCharset 字符编码,字符编码不对原样返回
     * @param src 原字符串
     * @return 解码后的字符串
     */
    public static String urlDecode(String src, String valueCharset) {
        try {
            return URLDecoder.decode(src, valueCharset);
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
        if(!StrUtil.BLANK.equals(paramString)) {
            //如果包含?，則直接追加//不包含?，則用?追加
            url = actionName.contains(QUESTION_MARK) ? actionName + AND + paramString : actionName + QUESTION_MARK + paramString;
        }
        return url;
    }
    public static String contactUrlParams(String actionName, MultiValueMap<String , String> params) {
        Objects.requireNonNull(actionName);
        return contactUrlParams(actionName , contactMap(params , Config.DEFAULT_CHARSET));
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
        return contactUrlParams(actionName , contactMap(params , Config.DEFAULT_CHARSET));
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
     * 将两截URL连接起来，主要功能是对 / 的兼容处理
     * @param firstFragment firstFragment
     * @param secondFragment secondFragment
     */
    public static String concatUrlIfNecessary(String firstFragment , String secondFragment){
        //1.baseUrl为空不处理
        //2.本身是完整的URL不处理
        if(StrUtil.isEmpty(firstFragment) || ParamUtil.isCompletedUrl(secondFragment)){
            return secondFragment;
        }
        if(firstFragment.endsWith(StrUtil.SLASH) && secondFragment.startsWith(StrUtil.SLASH)){
            return firstFragment + secondFragment.substring(1);
        }
        if(!firstFragment.endsWith(StrUtil.SLASH) && !secondFragment.startsWith(StrUtil.SLASH)){
            return firstFragment + StrUtil.SLASH + secondFragment;
        }
        return firstFragment + secondFragment;
    }

    /**
     * 处理路径参数
     * @param originUrl 形如 http://httpbin.org/book/{id}
     * @param routeParams 参数值
     * @return 处理过后的URL
     */
    public static String replaceRouteParamsIfNecessary(String originUrl , Map<String , String> routeParams){
        if(MapUtil.isEmpty(routeParams)){
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
