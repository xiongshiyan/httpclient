package top.jfunc.common.http;

/**
 * http相关协议
 * @author xiongshiyan
 */
public enum Protocol {
    /**
     * Http
     * @see HttpConstants#HTTP
     */
    HTTP(80) ,
    /**
     * Https
     * @see HttpConstants#HTTPS
     */
    HTTPS(443);

    /**
     * 默认端口
     */
    int port;
    Protocol(int defaultPort){
        this.port = defaultPort;
    }

    public int getPort() {
        return port;
    }
}
