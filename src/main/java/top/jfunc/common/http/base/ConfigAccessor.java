package top.jfunc.common.http.base;

/**
 * {@link top.jfunc.common.http.base.Config} 访问器
 * @author xiongshiyan at 2019/12/16 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface ConfigAccessor {
    /**
     * 设置全局默认配置,不调用就用系统设置的
     * @param config config
     */
    void setConfig(Config config);

    /**
     * 获取全局配置
     * @return 全局配置的config
     */
    Config getConfig();
}
