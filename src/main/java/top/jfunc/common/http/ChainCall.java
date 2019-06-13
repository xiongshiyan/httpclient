package top.jfunc.common.http;

/**
 * 链式调用，返回 this
 * @author xiongshiyan
 */
public interface ChainCall<THIS extends ChainCall> {
    /**
     * 返回this供链式调用的
     * @return this
     */
    @SuppressWarnings("unchecked")
    default THIS myself(){
        return (THIS)this;
    }
}
