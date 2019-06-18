package top.jfunc.common.http.base;

/**
 * 配置是否冻结检测器
 * @author xiongshiyan at 2019/6/14 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ConfigFrozen {
    /**
     * 是否冻结全局配置:当全局配置被冻结之后，是不允许再修改的，防止意外或者其他线程修改。
     * 因为全局配置是一个实例的所有请求共享的，如果被意外修改，可能引发一些严重的错误而不好排查。
     * 一般在配置完成之后调用一下{@link ConfigFrozen#freezeConfig()}
     */
    private boolean frozen = false;
    /**
     * 冻结配置
     */
    public void freezeConfig() {
        this.frozen = true;
    }

    /**
     * 在修改的地方调用此方法
     */
    protected void ensureConfigNotFreeze(){
        if(frozen){
            throw new IllegalStateException("全局配置已经被冻结,不允许再修改");
        }
    }
}
