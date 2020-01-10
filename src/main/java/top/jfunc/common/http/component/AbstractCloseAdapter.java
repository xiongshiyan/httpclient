package top.jfunc.common.http.component;

import java.io.Closeable;
import java.io.IOException;

/**
 * 关闭执行器
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractCloseAdapter<C> implements Closeable{

    private C c;

    public AbstractCloseAdapter(C c) {
        this.c = c;
    }

    @Override
    public void close() throws IOException {
        if(null != c){
            doClose(c);
        }
    }

    /**
     * 调用真正的关闭方法
     * @param c C
     * @throws IOException IOException
     */
    protected abstract void doClose(C c) throws IOException ;
}
