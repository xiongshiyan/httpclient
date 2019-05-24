package top.jfunc.common.http.annotation.method;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Treat the response body on methods returning as is,
 * i.e. without converting the body to {@code byte[]}.
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Streaming {
}