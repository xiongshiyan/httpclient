package top.jfunc.common.http.component;

import top.jfunc.common.http.base.ContentCallback;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.request.StringBodyRequest;

import java.io.IOException;

/**
 * @author xiongshiyan at 2020/1/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractBodyContentCallbackCreator<CC> implements ContentCallbackCreator<CC> {
    @Override
    public ContentCallback<CC> create(HttpRequest httpRequest) throws IOException{

        if(!supportBody(httpRequest)){
            return null;
        }

        return doCreate((StringBodyRequest) httpRequest);
    }

    /**
     * 真正的创建方法
     * @param stringBodyRequest StringBodyRequest
     * @return ContentCallback
     * @throws IOException IOException
     */
    protected abstract ContentCallback<CC> doCreate(StringBodyRequest stringBodyRequest) throws IOException;


    protected boolean supportBody(HttpRequest httpRequest){
        //方法不支持
        if(!httpRequest.getConfig().getMethodContentStrategy().supportContent(httpRequest.getMethod())){
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
}
