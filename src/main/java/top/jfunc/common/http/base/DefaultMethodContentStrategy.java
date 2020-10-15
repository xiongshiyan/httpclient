package top.jfunc.common.http.base;

import top.jfunc.common.http.annotation.method.GET;
import top.jfunc.common.http.annotation.method.PATCH;
import top.jfunc.common.http.annotation.method.POST;
import top.jfunc.common.http.annotation.method.PUT;
import top.jfunc.common.http.component.AbstractBodyContentCallbackCreator;

/**
 * @author xiongshiyan at 2020/10/15 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class DefaultMethodContentStrategy implements MethodContentStrategy{
    /**
     默认{@link POST}、{@link PUT}、{@link PATCH}支持，
     但是在es这种rest风格的应用中，可能{@link GET}也需要支持BODY，
     那么只需要替换掉{@link AbstractBodyContentCallbackCreator#methodContentStrategy}即可
    */
    @Override
    public boolean supportContent(Method method) {
        return method == Method.POST || method == Method.PUT | method == Method.PATCH;
    }
}
