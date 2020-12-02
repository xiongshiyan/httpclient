package top.jfunc.common.http.paramsign;
/**
 * 此包是对参数签名功能的一个封装，{@link top.jfunc.common.http.paramsign.ParamSigner}代表服务端对请求参数进行签名，支持servlet、jersey等
 * 如果签名不通过，则直接抛出异常{@link top.jfunc.common.http.paramsign.ParamSignException}
 * {@link top.jfunc.common.http.paramsign.SignParam}代表参数签名的必要字段
 *
 * 因为客户端和服务端计算签名的算法是一致的，所以提取出来了{@link top.jfunc.common.http.paramsign.SignCreator}以供使用，参数的处理是一致的
 */