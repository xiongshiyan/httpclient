package top.jfunc.common.http;

import java.util.Arrays;

/**
 * Http的Query或者Form参数模型(multiValue),key-value1,value2
 * @author xiongshiyan at 2019/5/17 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class Parameter {
    private String key;
    private Iterable<String> values;

    public Parameter(String key, String... values) {
        this.key = key;
        this.values = Arrays.asList(values);
    }
    public Parameter(String key, Iterable<String> values) {
        this.key = key;
        this.values = values;
    }


    public static Parameter of(String name, String... values){
        return new Parameter(name, values);
    }

    public static Parameter of(String name, Iterable<String> values){
        return new Parameter(name, values);
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Iterable<String> getValues() {
        return values;
    }

    public void setValues(Iterable<String> values) {
        this.values = values;
    }
}
