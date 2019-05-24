package top.jfunc.common.http.interfacing;

import top.jfunc.common.http.base.FormFile;
import top.jfunc.common.http.request.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Objects;

/**
 * 参数处理器定义及对应注解的实现
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
abstract class AbstractParameterHandler<R extends HttpRequest , P>{
    abstract void apply(R r, P p) throws IOException;

    /**
     * 处理header的
     * @see top.jfunc.common.http.annotation.parameter.Header
     */
    static final class Header  extends AbstractParameterHandler<HttpRequest ,String> {
        private final String name;
        Header(String name) {
            this.name = Objects.requireNonNull(name);
        }

        @Override
        public void apply(HttpRequest httpRequest, String value) throws IOException {
            if (value == null) {
                return; // Skip null values.
            }
            httpRequest.addHeader(name, value);
        }
    }
    /**
     * 处理headerMap的
     * @see top.jfunc.common.http.annotation.parameter.HeaderMap
     */
    static final class HeaderMap  extends AbstractParameterHandler<HttpRequest ,Map<String , String>> {

        @Override
        public void apply(HttpRequest httpRequest, Map<String , String> headers) throws IOException {
            if (headers == null || headers.isEmpty()) {
                return; // Skip null values.
            }
            headers.forEach(httpRequest::addHeader);
        }
    }

    /**
     * 处理查询参数的
     * @see top.jfunc.common.http.annotation.parameter.Query
     */
    static final class Query  extends AbstractParameterHandler<HttpRequest ,String> {
        private final String name;
        Query(String name) {
            this.name = Objects.requireNonNull(name);
        }

        @Override
        public void apply(HttpRequest httpRequest, String value) throws IOException {
            if (value == null) {
                return; // Skip null values.
            }
            httpRequest.addQueryParam(name, value);
        }
    }
    /**
     * 处理headerMap的
     * @see top.jfunc.common.http.annotation.parameter.QueryMap
     */
    static final class QueryMap  extends AbstractParameterHandler<HttpRequest ,Map<String , String>> {

        @Override
        public void apply(HttpRequest httpRequest, Map<String , String> querys) throws IOException {
            if (querys == null || querys.isEmpty()) {
                return; // Skip null values.
            }
            querys.forEach(httpRequest::addQueryParam);
        }
    }
    /**
     * 处理路径参数的
     * @see top.jfunc.common.http.annotation.parameter.Path
     */
    static final class Route extends AbstractParameterHandler<HttpRequest ,String> {
        private final String name;
        Route(String name) {
            this.name = Objects.requireNonNull(name);
        }

        @Override
        public void apply(HttpRequest httpRequest, String value) throws IOException {
            if (value == null) {
                return; // Skip null values.
            }
            httpRequest.addRouteParam(name, value);
        }
    }
    /**
     * 处理headerMap的
     * @see top.jfunc.common.http.annotation.parameter.PathMap
     */
    static final class RouteMap  extends AbstractParameterHandler<HttpRequest ,Map<String , String>> {

        @Override
        public void apply(HttpRequest httpRequest, Map<String , String> routes) throws IOException {
            if (routes == null || routes.isEmpty()) {
                return; // Skip null values.
            }
            routes.forEach(httpRequest::addRouteParam);
        }
    }
    /**
     * 处理Part
     * @see top.jfunc.common.http.annotation.parameter.Part
     */
    static final class Part extends AbstractParameterHandler<UploadRequest,Object> {
        private final String name;
        Part(String name) {
            this.name = Objects.requireNonNull(name);
        }

        @Override
        public void apply(UploadRequest httpRequest, Object value) throws IOException {
            if (value == null) {
                return; // Skip null values.
            }
            if(value instanceof String){
                httpRequest.addFormParam(name, (String) value);
            }else if(value instanceof FormFile){
                httpRequest.addFormFile((FormFile)value);
            }else if(value instanceof Array && Array.get(value , 0) instanceof FormFile){
                int length = Array.getLength(value);
                FormFile[] formFiles = new FormFile[length];
                System.arraycopy(value , 0 , formFiles , 0 , length);
                httpRequest.addFormFile(formFiles);
            }else if(value instanceof Iterable){
                Iterable<FormFile> formFiles = (Iterable<FormFile>) value;
                formFiles.forEach(httpRequest::addFormFile);
            }
            throw new IllegalArgumentException("参数错误");
        }
    }
    /**
     * 处理field的
     * @see top.jfunc.common.http.annotation.parameter.Field
     */
    static final class Field  extends AbstractParameterHandler<FormRequest,String> {
        private final String name;
        Field(String name) {
            this.name = Objects.requireNonNull(name);
        }

        @Override
        public void apply(FormRequest httpRequest, String value) throws IOException {
            if (value == null) {
                return; // Skip null values.
            }
            httpRequest.addFormParam(name, value);
        }
    }
    /**
     * 处理fieldMap的
     * @see top.jfunc.common.http.annotation.parameter.FieldMap
     */
    static final class FieldMap  extends AbstractParameterHandler<FormRequest ,Map<String , String>> {
        @Override
        public void apply(FormRequest httpRequest, Map<String , String> fields) throws IOException {
            if (fields == null || fields.isEmpty()) {
                return; // Skip null values.
            }
            fields.forEach(httpRequest::addFormParam);
        }
    }

    /**
     * 处理Body的
     * @see top.jfunc.common.http.annotation.parameter.Body
     */
    static final class Body  extends AbstractParameterHandler<ChangeableStringBodyRequest,String> {

        @Override
        public void apply(ChangeableStringBodyRequest httpRequest, String value) throws IOException {
            if (value == null) {
                return; // Skip null values.
            }
            httpRequest.setBody(value);
        }
    }
    /**
     * 处理请求URL的
     * @see top.jfunc.common.http.annotation.parameter.Url
     */
    static final class Url  extends AbstractParameterHandler<HttpRequest,Object> {

        @Override
        public void apply(HttpRequest httpRequest, Object value) throws IOException {
            if (value == null) {
                return; // Skip null values.
            }
            //支持URL和String
            httpRequest.setUrl(value.toString());
        }
    }
}
