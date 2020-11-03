package top.jfunc.common.http.paramsign;

import top.jfunc.common.utils.CommonUtil;
import top.jfunc.common.utils.MultiValueMap;

/**
 * @author xiongshiyan at 2020/11/3 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractSignCreator implements SignCreator {
    @Override
    public String create(MultiValueMap<String, String> params) {
        MultiValueMap<String, String> map = handleMap(params);
        String signStr = ParamSignUtil.getSignStr(map);
        return doSign(signStr);
    }
    protected MultiValueMap<String, String> handleMap(MultiValueMap<String, String> paramMap){
        String timestamp = String.valueOf(System.currentTimeMillis());
        String nonceStr  = CommonUtil.randomString(8);
        return ParamSignUtil.handleMap(paramMap, timestamp, nonceStr);
    }

    /**
     * 具体的签名算法，还可以添加上密钥等
     * @param toSign 待签名字符串
     * @return 签名串
     */
    protected abstract String doSign(String toSign);
}
