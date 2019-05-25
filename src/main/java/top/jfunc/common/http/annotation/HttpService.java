package top.jfunc.common.http.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;

/**
 * 标注一个接口是Http接口化的接口，方便扫描，也可以自定义扫描规则
 * @see top.jfunc.common.http.interfacing.JfuncHttp#create(Class)
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface HttpService {
}
