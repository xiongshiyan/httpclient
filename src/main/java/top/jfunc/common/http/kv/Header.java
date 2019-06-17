package top.jfunc.common.http.kv;

/**
 * Http的Header模型(multiValue),key-value1,value2
 * @author xiongshiyan at 2019/5/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class Header extends MultiValueEntry {
    public Header(String key, String value , String... values) {
        super(key, value , values);
    }
    public Header(String key, Iterable<String> values) {
        super(key, values);
    }

    public static Header of(String name, String value, String... values){
        return new Header(name, value , values);
    }

    public static Header of(String name, Iterable<String> values){
        return new Header(name, values);
    }
}
