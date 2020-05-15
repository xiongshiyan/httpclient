package top.jfunc.common.http.holder;

/**
 * Query参数、Form参数处理器
 * @author xiongshiyan
 */
public interface ParamHolder extends ValuesMapHolder{
    /**
     * 参数编码
     * @return 参数编码
     */
    String getParamCharset();

    /**
     * 设置参数编码
     * @param charset 编码
     * @return this
     */
    ParamHolder setParamCharset(String charset);
}
