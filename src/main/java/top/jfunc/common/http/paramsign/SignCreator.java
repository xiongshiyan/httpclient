package top.jfunc.common.http.paramsign;

import top.jfunc.common.utils.MultiValueMap;

/**
 * 用于客户端根据请求参数计算签名串
 * @author xiongshiyan at 2020/11/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface SignCreator {
    /**
     * 参数签名生成器
     * @param params 所有待签名参数
     * @return 签名串
     */
    String create(MultiValueMap<String, String> params);
}
