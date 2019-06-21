package top.jfunc.common.http;

/**
 * 提供返回this的方法，便于在多级继承的时候方法连缀
 * 这个借口一般用在接口或者抽象类中，具体实体类一般不需要，避免引入额外的复杂性
 * @see top.jfunc.common.http.request.impl.BaseRequest
 * @author xiongshiyan
 */
public interface ChainCall<THIS extends ChainCall> {
    /**
     * 返回自己便于方法连缀
     * @return this
     */
    @SuppressWarnings("unchecked")
    default THIS myself(){
        return (THIS)this;
    }
}
