package top.jfunc.common.http;

/**
 * http相关协议
 * @author xiongshiyan
 */
public enum Protocol {
    /**
     * Http，默认端口80
     */
    HTTP{
        @Override
        public int getDefaultPort() {
            return 80;
        }
    } ,
    /**
     * Https，默认端口443
     */
    HTTPS{
        @Override
        public int getDefaultPort() {
            return 443;
        }
    };

    public abstract int getDefaultPort();
}
