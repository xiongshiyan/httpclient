package top.jfunc.common.http.paramsign;

/**
 * @author xiongshiyan at 2020/7/31 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class ParamSignException extends RuntimeException {
    private SignParam signParam;
    public ParamSignException(String message , SignParam signParam) {
        super(message);
        this.signParam = signParam;
    }

    public SignParam getSignParam() {
        return signParam;
    }
}
