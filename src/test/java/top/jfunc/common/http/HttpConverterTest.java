package top.jfunc.common.http;

import top.jfunc.common.converter.Json2BeanConverter;
import top.jfunc.common.http.basic.*;
import top.jfunc.common.http.smart.Request;
import top.jfunc.common.http.withconverter.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 熊诗言 2017/11/24
 * @see HttpClient
 */
@Ignore
public class HttpConverterTest {

    @Test
    public void testGetOkHttp3(){
        ConverterSmartHttpClient http = new ConverterOkHttp3SmartHttpClient();
        http.setConverter(new Json2BeanConverter());
        testGet(http);
    }
    @Test
    public void testGetApacheHttp(){
        ConverterSmartHttpClient http = new ConverterApacheSmartHttpClient(new Json2BeanConverter());
        testGet(http);
    }
    @Test
    public void testGetNativeHttp(){
        ConverterSmartHttpClient http = new ConverterNativeSmartHttpClient(new Json2BeanConverter());
        testGet(http);
    }
    private void testGet(ConverterSmartHttpClient http){
        String url = "https://dzgtest.palmte.cn/dzg/api/v2/h5/common/getIp";
//        String url = "https://www.hao123.com/";
        try {
            Map<String , String> headers = new HashMap<>(1);
            headers.put("saleType" , "2");
            Bean bean1 = http.get(url, null, headers, Bean.class);
            System.out.println(bean1.getResultCode());
            System.out.println(bean1.getMessage());
            System.out.println(bean1.getData());

            Bean bean2 = http.get(Request.of(url).setHeaders(headers), Bean.class);
            System.out.println(bean2.getResultCode());
            System.out.println(bean2.getMessage());
            System.out.println(bean2.getData());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private static class Bean{
        private int ResultCode;
        private String Message;
        private Object Data;

        public int getResultCode() {
            return ResultCode;
        }

        public void setResultCode(int resultCode) {
            ResultCode = resultCode;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            Message = message;
        }

        public Object getData() {
            return Data;
        }

        public void setData(Object data) {
            Data = data;
        }
    }
}
