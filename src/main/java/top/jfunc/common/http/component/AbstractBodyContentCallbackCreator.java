package top.jfunc.common.http.component;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.base.DefaultMethodContentStrategy;
import top.jfunc.common.http.base.MethodContentStrategy;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractBodyContentCallbackCreator<CC> implements BodyContentCallbackCreator<CC> {

    private MethodContentStrategy methodContentStrategy = new DefaultMethodContentStrategy();

    @Override
    public ContentCallback<CC> create(HttpRequest httpRequest) throws IOException{

        if(!supportBody(httpRequest)){
            return null;
        }


        StringBodyRequest stringBodyRequest = (StringBodyRequest) httpRequest;

        Config config = httpRequest.getConfig();
        String bodyCharset = config.calculateBodyCharset(stringBodyRequest.getBodyCharset() , stringBodyRequest.getContentType());

        return create(httpRequest.getMethod() , stringBodyRequest.getBody() , bodyCharset , stringBodyRequest.getContentType());
    }


    protected boolean supportBody(HttpRequest httpRequest){
        //方法不支持
        if(!getMethodContentStrategy().supportContent(httpRequest.getMethod())){
            return false;
        }
        //不是StringBodyRequest
        if(!(httpRequest instanceof StringBodyRequest)){
            return false;
        }

        StringBodyRequest stringBodyRequest = (StringBodyRequest) httpRequest;
        String body = stringBodyRequest.getBody();

        //body为空
        if(null == body){
            return false;
        }

        return true;
    }

    public MethodContentStrategy getMethodContentStrategy() {
        return methodContentStrategy;
    }

    public void setMethodContentStrategy(MethodContentStrategy methodContentStrategy) {
        this.methodContentStrategy = methodContentStrategy;
    }
}
