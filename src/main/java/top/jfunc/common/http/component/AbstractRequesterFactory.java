package top.jfunc.common.http.component;

import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.utils.CollectionUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiongshiyan at 2020/1/6 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public abstract class AbstractRequesterFactory<C> implements RequesterFactory<C> {
    private List<RequesterCustomizer<C>> requesterCustomizers;

    @Override
    public C create(HttpRequest httpRequest) throws IOException{
        C c = doCreate(httpRequest);

        if(CollectionUtil.isNotEmpty(getRequesterCustomizers())){
            for (RequesterCustomizer<C> customer : getRequesterCustomizers()) {
                customer.customize(c , httpRequest);
            }
        }

        return c;
    }

    /**真正的创建*/
    protected abstract C doCreate(HttpRequest httpRequest) throws IOException;


    public List<RequesterCustomizer<C>> getRequesterCustomizers() {
        return requesterCustomizers;
    }

    public void setRequesterCustomizers(List<RequesterCustomizer<C>> requesterCustomizers) {
        this.requesterCustomizers = requesterCustomizers;
    }

    public void addCustomizers(RequesterCustomizer<C>... requesterCustomizers){
        if(null == this.requesterCustomizers){
            this.requesterCustomizers = new ArrayList<>();
        }

        this.requesterCustomizers.addAll(Arrays.asList(requesterCustomizers));
    }
}
