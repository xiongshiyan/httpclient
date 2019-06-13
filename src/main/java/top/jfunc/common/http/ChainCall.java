package top.jfunc.common.http;

/**
 * 链式调用，返回 this
 * @author xiongshiyan
 */
public interface ChainCall<T extends ChainCall> {
    /**
     * 返回this供链式调用的
     * @return this
     */
    @SuppressWarnings("unchecked")
    default T myself(){
        return (T)this;
    }
}
