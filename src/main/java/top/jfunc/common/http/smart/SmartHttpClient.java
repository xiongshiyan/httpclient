package top.jfunc.common.http.smart;

import top.jfunc.common.http.base.Config;
import top.jfunc.common.http.request.HttpRequest;
import top.jfunc.common.http.response.ClientHttpResponseConverter;

/**
 * 是面向使用者的一个门面类，既有{@link SimpleHttpClient}的简单快捷，也有{@link HttpRequestHttpClient}的简约全面
 * 1. 其实现是Thread-Safe的，一般的应用只需要实例化一个即可。
 * 2. 如果有多个第三方，那么可以为每个第三方实例化一个，因为一般地每个第三方的一些配置都不一样，例如BaseURL、header等等，通过{@link #setConfig(Config)}配置。
 * 3. 如果你对请求参数和响应没有太多额外的需求，那么可以直接使用{@link SimpleHttpClient}中的方法，响应就比较单一，参数简单地设置你想要的即可。
 * 4. 如果你对请求参数有比较丰富的需求，那么{@link HttpRequestHttpClient}就是为你量身定制的，{@link HttpRequest}及其子类是完全能够搞定五花八门的参数的。
 * 5. 如果你对响应有比较丰富的需求，那么{@link HttpRequestHttpClient}就是为你量身定制的，不仅可以获取比较全面的{@link Response}，甚至通过传入{@link ClientHttpResponseConverter}可以对响应自定义处理。
 * @author xiongshiyan at 2017/12/9
 */
public interface SmartHttpClient extends SimpleHttpClient, HttpRequestHttpClient {}
