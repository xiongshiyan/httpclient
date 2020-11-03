package top.jfunc.common.http.paramsign;

import top.jfunc.common.utils.CollectionUtil;
import top.jfunc.common.utils.MultiValueMap;
import top.jfunc.common.utils.StrUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 此类存在的意义是客户端和服务端的算法完全一致，
 * {@link ParamSignUtil#handleMap(MultiValueMap, String, String)}和{@link ParamSignUtil#getSignStr(MultiValueMap)}
 * 可以提出来共用
 * @author xiongshiyan at 2020/11/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ParamSignUtil {
    private static final String NULL       = "null";
    private static final String UNDEFINED  = "undefined";
    private static final String TS         = "ts";
    private static final String NONCE_STR  = "noncestr";

    /**
     * 对map进行处理，放入时间戳和随机串，去除空值，null，undefined
     */
    public static MultiValueMap<String, String> handleMap(MultiValueMap<String, String> paramMap, String timestamp, String nonceStr){
        paramMap.add(TS, timestamp);
        paramMap.add(NONCE_STR, nonceStr);
        //去掉空值
        paramMap.forEach((k,l) -> l.removeIf(ParamSignUtil::removeAble));
        //去掉空值之后如果value没有值，那么去掉这个key
        paramMap.entrySet().removeIf(ParamSignUtil::removeAble);
        return paramMap;
    }

    private static boolean removeAble(String value) {
        return StrUtil.isEmpty(value)
                || NULL.equalsIgnoreCase(value)
                || UNDEFINED.equalsIgnoreCase(value);
    }
    private static boolean removeAble(Map.Entry<String , List<String>> entry) {
        return CollectionUtil.isEmpty(entry.getValue());
    }

    /**
     * 字典序排序
     */
    public static String getSignStr(MultiValueMap<String, String> paramMap) {
        String[] keys = paramMap.keySet().toArray(new String[paramMap.size()]);
        Arrays.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            for (String value : paramMap.get(key)) {
                sb.append(key).append(StrUtil.EQUALS).append(value).append(StrUtil.AND);
            }
        }
        //去掉最后的&
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
