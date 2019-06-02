package top.jfunc.common.http.interfacing;

import top.jfunc.common.http.HeaderRegular;
import top.jfunc.common.http.MediaType;
import top.jfunc.common.http.Method;
import top.jfunc.common.http.annotation.method.*;
import top.jfunc.common.http.annotation.parameter.*;
import top.jfunc.common.http.holder.HeaderHolder;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.impl.CommonBodyRequest;
import top.jfunc.common.http.request.impl.CommonRequest;
import top.jfunc.common.http.request.impl.FileParamUploadRequest;
import top.jfunc.common.http.request.impl.FormBodyRequest;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.MultiValueMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.jfunc.common.http.interfacing.TypeUtils.methodError;
import static top.jfunc.common.http.interfacing.TypeUtils.parameterError;

/**
 * @author xiongshiyan at 2019/5/24 , contact me with email yanshixiong@126.com or phone 15208384257
 */
class HttpRequestFactory implements RequestFactory {
    private static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
    /**
     * 形如{id}这样的路径参数
     */
    private static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM + ")\\}");
    private static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);
    /**
     * 传入的方法
     */
    private java.lang.reflect.Method method;

    /**
     * 通过method计算获取请求方法
     */
    private Method httpMethod;

    /**
     * 参数对应的处理器
     */
    private final AbstractParameterHandler<?>[] parameterHandlers;

    /**
     * 是否有Body
     * @see POST
     * @see GET
     */
    private boolean hasBody = false;
    /**
     * 是否是文件上传
     * @see Multipart
     */
    private boolean multiPart = false;
    /**
     * 是否是FormUrlEncoded
     * @see FormUrlEncoded
     */
    private boolean formEncoded = false;
    /**
     * Content-Type
     */
    private MediaType contentType;
    /**
     * <code>@Headers</code> 注解添加的header
     */
    private MultiValueMap<String , String> headers;
    /**
     * 请求的相对URL
     */
    private String relativeUrl;
    /**
     * 路径参数名称集合
     */
    private Set<String> relativeUrlParamNames;

    private boolean gotField;
    private boolean gotPart;
    private boolean gotBody;
    private boolean gotPath;
    private boolean gotQuery;
    private boolean gotQueryMap;
    private boolean gotUrl;

    public HttpRequestFactory(java.lang.reflect.Method method) {
        this.method = method;

        //处理方法上的注解
        for (Annotation annotation : method.getAnnotations()) {
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

        //方法泛型参数类型
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        //方法的实际类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        //方法参数的注解
        Annotation[][] parameterAnnotationsArray = method.getParameterAnnotations();

        int parameterCount = parameterAnnotationsArray.length;
        parameterHandlers = new AbstractParameterHandler<?>[parameterCount];
        for (int p = 0; p < parameterCount; p++) {

            if(HttpRequest.class.isAssignableFrom(parameterTypes[p])){
                //HttpRequest已经有URL了
                gotUrl = true;
                parameterHandlers[p] = AbstractParameterHandler.DoNothing.INSTANCE;
            }else {
                parameterHandlers[p] = parseParameter(p, genericParameterTypes[p], parameterAnnotationsArray[p]);
            }

        }

        if (relativeUrl == null && !gotUrl) {
            throw methodError(method, "Missing either @%s URL or @Url parameter.", httpMethod);
        }
        if (!formEncoded && !multiPart && !hasBody && gotBody) {
            throw methodError(method, "Non-body HTTP method cannot contain @Body.");
        }
        if (formEncoded && !gotField) {
            throw methodError(method, "Form-encoded method must contain at least one @Field.");
        }
        if (multiPart && !gotPart) {
            throw methodError(method, "Multipart method must contain at least one @Part.");
        }
    }

    @Override
    public Method getHttpMethod() {
        return httpMethod;
    }

    @Override
    public HttpRequest httpRequest(Object[] args){

        int argumentCount = args.length;
        if (argumentCount != parameterHandlers.length) {
            throw new IllegalArgumentException("Argument count (" + argumentCount
                    + ") doesn't match expected count (" + parameterHandlers.length + ")");
        }


        HttpRequest httpRequest = initHttpRequest();

        //如果直接传递的是HttpRequest，就忽略其他的注解，因为他已经包含了请求所需的所有信息
        if(args[0] instanceof HttpRequest){
            httpRequest = (HttpRequest) (args[0]);
        }



        AbstractParameterHandler<Object>[] handlers = (AbstractParameterHandler<Object>[]) parameterHandlers;
        for (int p = 0; p < argumentCount; p++) {
            handlers[p].apply(httpRequest, args[p]);
        }

        validateRouteParams(httpRequest);

        HeaderHolder headerHolder = httpRequest.headerHolder();
        //处理方法上的Headers
        if(null != this.headers && !this.headers.isEmpty()){
            this.headers.forEachKeyValue(headerHolder::addHeader);
        }
        if(null != contentType){
            headerHolder.addHeader(HeaderRegular.CONTENT_TYPE.toString() , contentType.toString());
        }


        return httpRequest;
    }

    /**
     * 初始化HttpRequest
     */
    private HttpRequest initHttpRequest() {
        HttpRequest httpRequest;
        if(hasBody){
            httpRequest = CommonBodyRequest.of(relativeUrl);
        }else {
            httpRequest = CommonRequest.of(relativeUrl);
        }

        if(multiPart){
            httpRequest = FileParamUploadRequest.of(relativeUrl);
        }
        if (formEncoded){
            httpRequest = FormBodyRequest.of(relativeUrl);
        }

        return httpRequest;
    }

    private void validateRouteParams(HttpRequest httpRequest) {
        Map<String, String> routeParams = httpRequest.getRouteParams();
        //校验路径参数是否一致
        if(null != routeParams && !routeParams.isEmpty()){
            int size = routeParams.size();
            if(size != relativeUrlParamNames.size()){
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
                throw new IllegalArgumentException("路径参数名称对应不上");
            }
        }
    }

    /**
     * 一个参数多个注解解析
     */
    private AbstractParameterHandler<?> parseParameter(int pos, Type parameterType, Annotation[] annotations) {
        AbstractParameterHandler<?> result = null;
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                AbstractParameterHandler<?> annotationAction =
                        parseParameterAnnotation(pos, parameterType, annotations, annotation);

                if (annotationAction == null) {
                    continue;
                }

                if (result != null) {
                    throw parameterError(method, pos,
                            "Multiple HttpService annotations found, only one allowed.");
                }

                result = annotationAction;
            }
        }

        if (result == null) {
            throw parameterError(method, pos, "No HttpService annotation found.");
        }

        return result;
    }

    /**
     * 一个参数上的注解解析
     */
    private AbstractParameterHandler<?> parseParameterAnnotation(int p, Type type, Annotation[] annotations, Annotation annotation) {
        if (annotation instanceof Url) {
            validateResolvableType(p, type);
            if (gotUrl) {
                throw parameterError(method, p, "Multiple @Url method annotations found.");
            }
            if (gotPath) {
                throw parameterError(method, p, "@Path parameters may not be used with @Url.");
            }
            if (gotQuery) {
                throw parameterError(method, p, "A @Url parameter must not come after a @Query.");
            }
            if (gotQueryMap) {
                throw parameterError(method, p, "A @Url parameter must not come after a @QueryMap.");
            }
            if (relativeUrl != null) {
                throw parameterError(method, p, "@Url cannot be used with @%s URL", httpMethod);
            }

            gotUrl = true;

            if (type == String.class
                    || type == URL.class
                    || type == URI.class) {
                return new AbstractParameterHandler.Url();
            } else {
                throw parameterError(method, p,
                        "@Url must be okhttp3.HttpUrl, String, java.net.URI, or android.net.Uri type.");
            }

        } else if (annotation instanceof Path) {
            validateResolvableType(p, type);
            if (gotQuery) {
                throw parameterError(method, p, "A @Path parameter must not come after a @Query.");
            }
            if (gotQueryMap) {
                throw parameterError(method, p, "A @Path parameter must not come after a @QueryMap.");
            }
            if (gotUrl) {
                throw parameterError(method, p, "@Path parameters may not be used with @Url.");
            }
            if (relativeUrl == null) {
                throw parameterError(method, p, "@Path can only be used with relative url on @%s",
                        httpMethod);
            }
            gotPath = true;

            Path path = (Path) annotation;
            String name = path.value();
            validatePathName(p, name);

            return new AbstractParameterHandler.Route(name);
        } else if (annotation instanceof PathMap) {
            validateResolvableType(p, type);
            Class<?> rawParameterType = TypeUtils.getRawType(type);
            if (!Map.class.isAssignableFrom(rawParameterType)) {
                throw parameterError(method, p, "@PathMap parameter type must be Map.");
            }
            Type mapType = TypeUtils.getSupertype(type, rawParameterType, Map.class);
            if (!(mapType instanceof ParameterizedType)) {
                throw parameterError(method, p,
                        "Map must include generic types (e.g., Map<String, String>)");
            }
            ParameterizedType parameterizedType = (ParameterizedType) mapType;
            Type keyType = TypeUtils.getParameterUpperBound(0, parameterizedType);
            if (String.class != keyType) {
                throw parameterError(method, p, "@PathMap keys must be of type String: " + keyType);
            }
            new AbstractParameterHandler.RouteMap();
        } else if (annotation instanceof Query) {
            validateResolvableType(p, type);
            Query query = (Query) annotation;
            String name = query.value();
            gotQuery = true;
            return new AbstractParameterHandler.Query(name);
        } else if (annotation instanceof QueryMap) {
            validateResolvableType(p, type);
            Class<?> rawParameterType = TypeUtils.getRawType(type);
            gotQueryMap = true;
            if (!Map.class.isAssignableFrom(rawParameterType)) {
                throw parameterError(method, p, "@QueryMap parameter type must be Map.");
            }
            Type mapType = TypeUtils.getSupertype(type, rawParameterType, Map.class);
            if (!(mapType instanceof ParameterizedType)) {
                throw parameterError(method, p,
                        "Map must include generic types (e.g., Map<String, String>)");
            }
            ParameterizedType parameterizedType = (ParameterizedType) mapType;
            Type keyType = TypeUtils.getParameterUpperBound(0, parameterizedType);
            if (String.class != keyType) {
                throw parameterError(method, p, "@QueryMap keys must be of type String: " + keyType);
            }
            return new AbstractParameterHandler.QueryMap();

        } else if (annotation instanceof Header) {
            validateResolvableType(p, type);
            Header header = (Header) annotation;
            String name = header.value();

            return new AbstractParameterHandler.Header(name);

        } else if (annotation instanceof HeaderMap) {
            validateResolvableType(p, type);
            Class<?> rawParameterType = TypeUtils.getRawType(type);
            if (!Map.class.isAssignableFrom(rawParameterType)) {
                throw parameterError(method, p, "@HeaderMap parameter type must be Map.");
            }
            Type mapType = TypeUtils.getSupertype(type, rawParameterType, Map.class);
            if (!(mapType instanceof ParameterizedType)) {
                throw parameterError(method, p,
                        "Map must include generic types (e.g., Map<String, String>)");
            }
            ParameterizedType parameterizedType = (ParameterizedType) mapType;
            Type keyType = TypeUtils.getParameterUpperBound(0, parameterizedType);
            if (String.class != keyType) {
                throw parameterError(method, p, "@HeaderMap keys must be of type String: " + keyType);
            }
            return new AbstractParameterHandler.HeaderMap();
        } else if (annotation instanceof Field) {
            validateResolvableType(p, type);
            if (!formEncoded) {
                throw parameterError(method, p, "@Field parameters can only be used with form encoding.");
            }
            Field field = (Field) annotation;
            String name = field.value();
            gotField = true;

            return new AbstractParameterHandler.Field(name);

        } else if (annotation instanceof FieldMap) {
            validateResolvableType(p, type);
            if (!formEncoded) {
                throw parameterError(method, p,
                        "@FieldMap parameters can only be used with form encoding.");
            }
            Class<?> rawParameterType = TypeUtils.getRawType(type);
            if (!Map.class.isAssignableFrom(rawParameterType)) {
                throw parameterError(method, p, "@FieldMap parameter type must be Map.");
            }
            Type mapType = TypeUtils.getSupertype(type, rawParameterType, Map.class);
            if (!(mapType instanceof ParameterizedType)) {
                throw parameterError(method, p,
                        "Map must include generic types (e.g., Map<String, String>)");
            }
            ParameterizedType parameterizedType = (ParameterizedType) mapType;
            Type keyType = TypeUtils.getParameterUpperBound(0, parameterizedType);
            if (String.class != keyType) {
                throw parameterError(method, p, "@FieldMap keys must be of type String: " + keyType);
            }
            gotField = true;
            return new AbstractParameterHandler.FieldMap();

        } else if (annotation instanceof Part) {
            validateResolvableType(p, type);
            if (!multiPart) {
                throw parameterError(method, p,
                        "@Part parameters can only be used with multipart encoding.");
            }
            Part part = (Part) annotation;
            gotPart = true;

            return new AbstractParameterHandler.Part(part.value());
        } else if (annotation instanceof Body) {
            validateResolvableType(p, type);
            if (formEncoded || multiPart) {
                throw parameterError(method, p,
                        "@Body parameters cannot be used with form or multi-part encoding.");
            }
            if (gotBody) {
                throw parameterError(method, p, "Multiple @Body method annotations found.");
            }
            gotBody = true;
            return new AbstractParameterHandler.Body();
        }

        // Not a HttpService annotation.
        return null;
    }

    private void validateResolvableType(int p, Type type) {
        if (TypeUtils.hasUnresolvableType(type)) {
            throw parameterError(method, p,
                    "Parameter type must not include a type variable or wildcard: %s", type);
        }
    }
    private void validatePathName(int p, String name) {
        if (!PARAM_NAME_REGEX.matcher(name).matches()) {
            throw parameterError(method, p, "@Path parameter name must match %s. Found: %s",
                    PARAM_URL_REGEX.pattern(), name);
        }
        // Verify URL replacement name is actually present in the URL path.
        if (!relativeUrlParamNames.contains(name)) {
            throw parameterError(method, p, "URL \"%s\" does not contain \"{%s}\".", relativeUrl, name);
        }
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
        }else if (annotation instanceof Multipart) {
            if (formEncoded) {
                throw methodError(method, "Only one encoding annotation is allowed.");
            }
            multiPart = true;
        } else if (annotation instanceof FormUrlEncoded) {
            if (multiPart) {
                throw methodError(method, "Only one encoding annotation is allowed.");
            }
            formEncoded = true;
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
}
