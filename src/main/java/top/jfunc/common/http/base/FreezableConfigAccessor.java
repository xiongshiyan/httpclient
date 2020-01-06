package top.jfunc.common.http.base;

/**
 * 提供冻结功能
 * @author xiongshiyan at 2019/12/16 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public interface FreezableConfigAccessor extends ConfigAccessor {
    /**
     * 冻结配置
     */
    void freezeConfig();
}
