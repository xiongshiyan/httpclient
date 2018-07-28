package cn.zytx.common.ftp;

/**
 * @author xiongshiyan at 2018/4/8
 */
public class ConnectBean {
    private String host;
    private int port = 21;
    private String encoding = "ISO-8859-1";
    private String username;
    private String password;
    private String path;

    public ConnectBean() {
    }

    public ConnectBean(String host, int port, String encoding, String username, String password) {
        this.host = host;
        this.port = port;
        this.encoding = encoding;
        this.username = username;
        this.password = password;
    }
    public ConnectBean(String host, String encoding, String username, String password) {
        this.host = host;
        this.encoding = encoding;
        this.username = username;
        this.password = password;
    }
    public ConnectBean(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public ConnectBean(String encoding) {
        this.encoding = encoding;
    }

    public String getHost() {
        return host;
    }

    public ConnectBean setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ConnectBean setPort(int port) {
        this.port = port;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public ConnectBean setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ConnectBean setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ConnectBean setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPath() {
        return path;
    }

    public ConnectBean setPath(String path) {
        this.path = path;
        return this;
    }
}
