package top.jfunc.common.http.holder;

import top.jfunc.common.http.kv.Header;
import top.jfunc.common.utils.ArrayListMultiValueMap;
import top.jfunc.common.utils.ArrayListMultimap;
import top.jfunc.common.utils.MultiValueMap;

import java.util.Map;

/**
 * wrap of {@link MultiValueMap} and impl HeaderHolder
 * @see HeaderHolder
 * @see MultiValueMap
 * @author xiongshiyan
 */
public class DefaultHeaderHolder implements HeaderHolder {
    /**
     * 请求头，懒加载
     */
    private MultiValueMap<String,String> headers;

    @Override
    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    @Override
    public HeaderHolder setHeaders(MultiValueMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public HeaderHolder setHeaders(ArrayListMultimap<String, String> headers) {
        if(null != headers){
            this.headers = ArrayListMultiValueMap.fromMap(headers);
        }
        return this;
    }
    @Override
    public HeaderHolder setHeaders(Map<String, String> headers) {
        if(null != headers){
            this.headers = ArrayListMultiValueMap.fromMap(headers);
        }
        return this;
    }

    @Override
    public HeaderHolder setHeader(String key, String value) {
        initHeaders();
        this.headers.set(key, value);
        return this;
    }

    @Override
    public HeaderHolder addHeader(String key, String value){
        initHeaders();
        this.headers.add(key, value);
        return this;
    }
    @Override
    public HeaderHolder addHeader(String key, String value , String... values){
        initHeaders();
        this.headers.add(key , value);
        for (String val : values) {
            this.headers.add(key , val);
        }
        return this;
    }
    @Override
    public HeaderHolder addHeader(String key, Iterable<String> values){
        initHeaders();
        for (String value : values) {
            this.headers.add(key , value);
        }
        return this;
    }
    @Override
    public HeaderHolder addHeader(Header header , Header... headers){
        addHeader(header.getKey() , header.getValue());
        for (Header h : headers) {
            addHeader(h.getKey() , h.getValue());
        }
        return this;
    }
    @Override
    public HeaderHolder addHeader(Iterable<Header> headers){
        for (Header header : headers) {
            addHeader(header.getKey() , header.getValue());
        }
        return this;
    }
    @Override
    public HeaderHolder addHeader(Map.Entry<String , Iterable<String>> header , Map.Entry<String , Iterable<String>>... headers){
        addHeader(header.getKey() , header.getValue());
        for (Map.Entry<String , Iterable<String>> h : headers) {
            addHeader(h.getKey() , h.getValue());
        }
        return this;
    }
    private void initHeaders(){
        if(null == this.headers){
            this.headers = new ArrayListMultiValueMap<>(2);
        }
    }
}
