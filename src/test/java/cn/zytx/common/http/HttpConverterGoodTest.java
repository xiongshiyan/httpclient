package cn.zytx.common.http;

import cn.zytx.common.converter.DefaultJsonConverter;
import cn.zytx.common.http.basic.HttpClient;
import cn.zytx.common.http.converter.json.*;
import cn.zytx.common.http.smart.*;
import cn.zytx.common.json.JsonObject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static cn.zytx.common.http.HttpConstants.JSON_WITH_DEFAULT_CHARSET;

/**
 * @author 熊诗言 2017/11/24
 * @see HttpClient
 */
public class HttpConverterGoodTest {

    @Test@Ignore
    public void testGetOkHttp3(){
        ConverterSmartHttpClient http = new ConverterOkHttp3SmartHttpClient(new DefaultJsonConverter());
        testGet(http);
    }
    @Test@Ignore
    public void testGetApacheHttp(){
        ConverterSmartHttpClient http = new ConverterApacheSmartHttpClient(new DefaultJsonConverter());
        testGet(http);
    }
    @Test@Ignore
    public void testGetNativeHttp(){
        ConverterSmartHttpClient http = new ConverterNativeSmartHttpClient().setConverter(new DefaultJsonConverter());
        testGet(http);
    }
    private void testGet(ConverterSmartHttpClient http){
        String url = "https://dzgtest.palmte.cn/dzg/api/v2/h5/common/getIp";
//        String url = "https://www.hao123.com/";
        try {
            Request request = Request.of(url).addHeader("saleType" , "2");

            ResultConverter convert = http.getAndConvert(request);
            Bean bean1 = convert.toBean(Bean.class);

            Bean bean2 = http.get(request, Bean.class);
            System.out.println(bean1.getResultCode());
            System.out.println(bean1.getMessage());
            System.out.println(bean1.getData());
            System.out.println(bean2.getResultCode());
            System.out.println(bean2.getMessage());
            System.out.println(bean2.getData());

        }catch (IOException e){
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
            System.out.println(e.getResponseCode());
            System.out.println(e.getErrorMessage());
        }
    }


    @Test@Ignore
    public void testPostOkHttp3(){
        ConverterSmartHttpClient http = new ConverterOkHttp3SmartHttpClient(new DefaultJsonConverter());
        testPost(http);
    }
    @Test@Ignore
    public void testPostApacheHttp(){
        ConverterSmartHttpClient http = new ConverterApacheSmartHttpClient(new DefaultJsonConverter());
        testPost(http);
    }
    @Test@Ignore
    public void testPostNativeHttp(){
        ConverterSmartHttpClient http = new ConverterNativeSmartHttpClient().setConverter(new DefaultJsonConverter());
        testPost(http);
    }
    public void testPost(ConverterSmartHttpClient http){
        String url = "https://dzgtest.palmte.cn/dzg/api/v2/test/boss";
        try {
            Request request = Request.of(url).setIncludeHeaders(true).addHeader("ss" , "ss").addHeader("ss" , "dd").setBody("{\"name\":\"熊诗言\"}").setContentType(JSON_WITH_DEFAULT_CHARSET).setConnectionTimeout(10000).setReadTimeout(10000).setResultCharset("UTF-8");

            ResultConverter resultConverter = http.postAndConvert(request);

            JsonObject jsonObject = resultConverter.toJsonObject();
            System.out.println(jsonObject);
            Assert.assertEquals("熊诗言" , jsonObject.getString("name"));
        } catch (HttpException e) {
            e.printStackTrace();
            System.out.println(e.getResponseCode());
            System.out.println(e.getErrorMessage());
        }catch (IOException e){
            System.out.println("超时异常");
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
