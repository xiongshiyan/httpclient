package top.jfunc.common.http.interfacing;

import top.jfunc.common.http.HeaderRegular;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.annotation.method.*;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.impl.CommonBodyRequest;
import top.jfunc.common.http.request.impl.CommonRequest;
import top.jfunc.common.http.request.impl.FileParamUploadRequest;
import top.jfunc.common.http.request.impl.FormBodyRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.MultiValueMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
class HttpRequestFactory implements RequestFactory{
    private static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
    /**
     * 形如{id}这样的路径参数
     */
    private static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM + ")\\}");
    private static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);

    /**
     * 通过method计算获取请求方法
     */
    private Method httpMethod;

    private java.lang.reflect.Method method;


    private final Annotation[] methodAnnotations;
    private final Type[] parameterTypes;
    private final Annotation[][] parameterAnnotationsArray;

    private boolean multiPart = false;
    private boolean hasBody;
    private boolean formEncoded;
    private MediaType contentType;
    private MultiValueMap<String , String> headers;
    private String relativeUrl;
    private Set<String> relativeUrlParamNames;

    /**
     * 根据Method等信息生成的
     */
    private HttpRequest httpRequest;

    public HttpRequestFactory(java.lang.reflect.Method method) {
        this.method = method;

        this.methodAnnotations = method.getAnnotations();
        this.parameterTypes = method.getGenericParameterTypes();
        this.parameterAnnotationsArray = method.getParameterAnnotations();
    }

    @Override
    public Method getHttpMethod() {
        return httpMethod;
    }

    @Override
    public HttpRequest httpRequest(Object[] args){
        for (Annotation annotation : methodAnnotations) {
            parseMethodAnnotation(annotation);
        }
        if (httpMethod == null) {
            throw methodError(method, "HTTP method annotation is required (e.g., @GET, @POST, etc.).");
        }

        if (!hasBody) {
            if (multiPart) {
                throw methodError(method,
                        "Multipart can only be specified on HTTP methods with request body (e.g., @POST).");
            }
            if (formEncoded) {
                throw methodError(method, "FormUrlEncoded can only be specified on HTTP methods with "
                        + "request body (e.g., @POST).");
            }
        }

        //如果直接传递的是HttpRequest，就忽略其他的注解，因为他已经包含了请求所需的所有信息
        if(args.length==1 && args[0] instanceof HttpRequest){
            return (HttpRequest) (args[0]);
        }

        int parameterCount = parameterAnnotationsArray.length;
        AbstractParameterHandler<?>[] parameterHandlers = new AbstractParameterHandler<?>[parameterCount];
        for (int p = 0; p < parameterCount; p++) {
            parameterHandlers[p] = parseParameter(p, parameterTypes[p], parameterAnnotationsArray[p]);
        }

        int argumentCount = args.length;
        if (argumentCount != parameterHandlers.length) {
            throw new IllegalArgumentException("Argument count (" + argumentCount
                    + ") doesn't match expected count (" + parameterHandlers.length + ")");
        }

        AbstractParameterHandler<Object>[] handlers = (AbstractParameterHandler<Object>[]) parameterHandlers;
        for (int p = 0; p < argumentCount; p++) {
            handlers[p].apply(httpRequest, args[p]);
        }

        validateRouteParams();

        //处理方法上的Headers
        if(null != headers && !headers.isEmpty()){
            headers.forEachKeyValue(httpRequest::addHeader);
        }
        if(null != contentType){
            httpRequest.addHeader(HeaderRegular.CONTENT_TYPE.toString() , contentType.toString());
        }


        return httpRequest;
    }

    protected void validateRouteParams() {
        Map<String, String> routeParams = httpRequest.getRouteParams();
        //校验路径参数是否一致
        int size = 0;
        if(null != routeParams && !routeParams.isEmpty()){
            size = routeParams.size();
            if(size != relativeUrlParamNames.size()){
                httpRequest = null;
                throw new IllegalArgumentException("路径参数个数不匹配");
            }

            int x= 0;
            for (String paramName : relativeUrlParamNames) {
                for (Map.Entry<String, String> entry : routeParams.entrySet()) {
                    if (entry.getKey().equals(paramName)) {
                        x ++;
                    }
                }
            }
            if(x != size){
                httpRequest = null;
                throw new IllegalArgumentException("路径参数名称对应不上");
            }
        }
    }

    /**
     * 多个参数注解解析
     */
    private AbstractParameterHandler<?> parseParameter(
            int p, Type parameterType, Annotation[] annotations) {
        AbstractParameterHandler<?> result = null;
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                AbstractParameterHandler<?> annotationAction =
                        parseParameterAnnotation(p, parameterType, annotations, annotation);

                if (annotationAction == null) {
                    continue;
                }

                if (result != null) {
                    throw parameterError(method, p,
                            "Multiple Jfunc annotations found, only one allowed.");
                }

                result = annotationAction;
            }
        }

        if (result == null) {
            throw parameterError(method, p, "No Jfunc annotation found.");
        }

        return result;
    }

    /**
     * 一个参数上的注解解析
     */
    private AbstractParameterHandler<?> parseParameterAnnotation(
            int p, Type type, Annotation[] annotations, Annotation annotation) {
        //TODO 根据注解生成参数处理器
        return null;
    }
    /**
     * 解析方法上的注解
     */
    private void parseMethodAnnotation(Annotation annotation) {
        if (annotation instanceof DELETE) {
            parseHttpMethodAndPath(Method.DELETE, ((DELETE) annotation).value());
        } else if (annotation instanceof GET) {
            parseHttpMethodAndPath(Method.GET, ((GET) annotation).value());
        } else if (annotation instanceof HEAD) {
            parseHttpMethodAndPath(Method.HEAD, ((HEAD) annotation).value());
        } else if (annotation instanceof PATCH) {
            parseHttpMethodAndPath(Method.PATCH, ((PATCH) annotation).value());
        } else if (annotation instanceof POST) {
            parseHttpMethodAndPath(Method.POST, ((POST) annotation).value());
        } else if (annotation instanceof PUT) {
            parseHttpMethodAndPath(Method.PUT, ((PUT) annotation).value());
        } else if (annotation instanceof OPTIONS) {
            parseHttpMethodAndPath(Method.OPTIONS, ((OPTIONS) annotation).value());
        } else if (annotation instanceof HTTP) {
            HTTP http = (HTTP) annotation;
            parseHttpMethodAndPath(http.method(), http.path());
        } else if (annotation instanceof Headers) {
            String[] headersToParse = ((Headers) annotation).value();
            if (headersToParse.length == 0) {
                throw methodError(method, "@Headers annotation is empty.");
            }
            headers = parseHeaders(headersToParse);
        }


        if (annotation instanceof Multipart) {
            if (formEncoded) {
                throw methodError(method, "Only one encoding annotation is allowed.");
            }
            multiPart = true;
            httpRequest = FileParamUploadRequest.of(relativeUrl);

        } else if (annotation instanceof FormUrlEncoded) {
            if (multiPart) {
                throw methodError(method, "Only one encoding annotation is allowed.");
            }
            formEncoded = true;

            httpRequest = FormBodyRequest.of(relativeUrl);
        }
    }

    /**
     * 解析方法上的Headers注解，用于静态header 形如 @Headers("k1:v1" , "k2:v2")
     */
    private MultiValueMap<String , String> parseHeaders(String[] headers) {
        if(null == headers || 0 == headers.length){
            return null;
        }
        MultiValueMap<String , String> h = new ArrayListMultiValueMap<>(headers.length);
        for (String header : headers) {
            int colon = header.indexOf(':');
            if (colon == -1 || colon == 0 || colon == header.length() - 1) {
                throw methodError(method,
                        "@Headers value must be in the form \"Name: Value\". Found: \"%s\"", header);
            }
            String headerName = header.substring(0, colon);
            String headerValue = header.substring(colon + 1).trim();
            if (HeaderRegular.CONTENT_TYPE.toString().equalsIgnoreCase(headerName)) {
                try {
                    contentType = MediaType.parse(headerValue);
                } catch (Exception e) {
                    throw methodError(method, e, "Malformed content type: %s", headerValue);
                }
            } else {
                h.add(headerName, headerValue);
            }
        }
        return h;
    }

    /**
     * 1.请求方法注解上的URL,可以包括query参数，但是不能在query参数中使用路径参数，即 ?k={v}，可以使用@Query注解传递
     * 2.路径上的path参数保存到集合中，供后面配合@Path注解使用
     * @param httpMethod 请求方法
     * @param value 请求方法注解中的URL
     */
    private void parseHttpMethodAndPath(Method httpMethod, String value) {
        if (this.httpMethod != null) {
            throw methodError(method, "Only one HTTP method is allowed. Found: %s and %s.",
                    this.httpMethod, httpMethod);
        }
        this.httpMethod = httpMethod;
        this.hasBody = httpMethod.hasContent();

        if (value.isEmpty()) {
            return;
        }

        // Get the relative URL path and existing query string, if present.
        int question = value.indexOf('?');
        if (question != -1 && question < value.length() - 1) {
            // Ensure the query string does not have any named parameters.
            String queryParams = value.substring(question + 1);
            Matcher queryParamMatcher = PARAM_URL_REGEX.matcher(queryParams);
            if (queryParamMatcher.find()) {
                throw methodError(method, "URL query string \"%s\" must not have replace block. "
                        + "For dynamic query parameters use @Query.", queryParams);
            }
        }

        this.relativeUrl = value;
        this.relativeUrlParamNames = parsePathParameters(value);

        if(hasBody){
            httpRequest = CommonBodyRequest.of(relativeUrl);
        }else {
            httpRequest = CommonRequest.of(relativeUrl);
        }
    }
    /**
     * Gets the set of unique path parameters used in the given URI. If a parameter is used twice
     * in the URI, it will only show up once in the set.
     */
    static Set<String> parsePathParameters(String path) {
        Matcher m = PARAM_URL_REGEX.matcher(path);
        Set<String> patterns = new LinkedHashSet<>();
        while (m.find()) {
            patterns.add(m.group(1));
        }
        return patterns;
    }
    static RuntimeException methodError(java.lang.reflect.Method method, String message,
                                        Object... args) {
        message = String.format(message, args);
        return new IllegalArgumentException(message
                + "\n    for method "
                + method.getDeclaringClass().getSimpleName()
                + "."
                + method.getName(), null);
    }
    static RuntimeException methodError(java.lang.reflect.Method method, Throwable cause, String message,
                                        Object... args) {
        message = String.format(message, args);
        return new IllegalArgumentException(message
                + "\n    for method "
                + method.getDeclaringClass().getSimpleName()
                + "."
                + method.getName(), cause);
    }

    static RuntimeException parameterError(java.lang.reflect.Method method,
                                           Throwable cause, int p, String message, Object... args) {
        return methodError(method, cause, message + " (parameter #" + (p + 1) + ")", args);
    }

    static RuntimeException parameterError(java.lang.reflect.Method method, int p, String message, Object... args) {
        return methodError(method, message + " (parameter #" + (p + 1) + ")", args);
    }
}
