package top.jfunc.common.http.sms;

/**
 * 短信发送器，可能是第三方实现，也可能是移动自己的接口
 * @author 熊诗言
 */
@FunctionalInterface
public interface SmsSender {
    /**
     * 发送短信
     * @param phone 发送给谁？
     * @param content 发送的内容
     * @return 是否成功
     */
	boolean send(String phone, String content);
}
